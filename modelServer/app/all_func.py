from diffusers import StableDiffusionXLPipeline, DDIMScheduler
import torch
import mediapy
import styleAlign.sa_handler as sa_handler
import math
from diffusers.utils import load_image
import styleAlign.inversion as inversion
import numpy as np
import base64
import io

import clip
from tqdm import tqdm
from PIL import Image
import os
import json
from queue import Queue

from openai import OpenAI

import pandas as pd
from torch.utils.data import Dataset, DataLoader

# ======================================== DataLoader ========================================

class GenreImageDataset(Dataset):
    def __init__(self, preprocess, image_path_list):
        self.preprocess = preprocess
        self.image_path_list = image_path_list

    def __len__(self):
        return len(self.image_path_list)
    
    def __getitem__(self, idx):
        
        image_path = self.image_path_list[idx]
        image = self.preprocess(Image.open(image_path))
        
        return image, image_path


def get_genre_image_dataloader(preprocess, image_path_list, batch_size):
    genre_image_dataset = GenreImageDataset(preprocess, image_path_list)
    return DataLoader(genre_image_dataset, batch_size=batch_size, num_workers=4)
    
# ============================================================================================

# ======================================= initialization =======================================

def init_models(clip_model_name = 'ViT-B/32'):
    
    device = "cuda" if torch.cuda.is_available() else "cpu"
    scheduler = DDIMScheduler(
    beta_start=0.00085, beta_end=0.012, beta_schedule="scaled_linear",
    clip_sample=False, set_alpha_to_one=False)

    diffusion_pipeline = StableDiffusionXLPipeline.from_pretrained(
        "stabilityai/stable-diffusion-xl-base-1.0", torch_dtype=torch.float16, variant="fp16",
        use_safetensors=True,
        scheduler=scheduler
    ).to(device)
    

    clip_model, preprocess = clip.load(clip_model_name, device=device)
    
    return diffusion_pipeline, clip_model, preprocess, device

def init_image_tensor_retrieval(clip_model, preprocess, genre_list, device,
                                batch_size=1024, root_path_retrieval='./top5000_images'):
    image_tensor_dict = {}
    image_path_dict = {}
    
    for genre in genre_list:
        
        image_tensors = []
        ret_image_path_list = None
        
        _images = os.listdir(f'{root_path_retrieval}/{genre}')
        image_path_list = [os.path.join(f'{root_path_retrieval}/{genre}', image_id) for image_id in _images]

        init_dl = get_genre_image_dataloader(preprocess, image_path_list, batch_size=batch_size)

        for processed_images, image_path in tqdm(init_dl):
            with torch.no_grad():
                processed_images = processed_images.to(device)
                image_feature = clip_model.encode_image(processed_images)
                image_feature /= image_feature.norm(dim=-1, keepdim=True)

                image_tensors.append(image_feature)

                if ret_image_path_list == None:
                    ret_image_path_list = list(image_path)
                else:
                    ret_image_path_list += list(image_path)
                
        image_tensors = torch.cat(image_tensors)
#         print(image_tensors.shape)
        image_tensors_permute = image_tensors.permute(1,0)

        image_tensor_dict[genre] = image_tensors_permute
        image_path_dict[genre] = ret_image_path_list
        
    return image_tensor_dict, image_path_dict

def init_gpt_client(gpt_api_key):
    gpt_client = OpenAI(api_key = gpt_api_key)
    return gpt_client
# ============================================================================================

# ============================================ gpt ===========================================

def summarize_texts(input_text_list:list, gpt_client, gpt_model_name='gpt-3.5-turbo', seed_val=123)-> list: 
    
    assert type(input_text_list) == list, "input for summarization must be list of string"
    
    query = 'Please summarize the key parts of the story below in one short sentence. answer without any explanation.\n\
             answer with noun phrase and change \'proper noun\' to pronoun or common nouns\n'

    summarized_text_list = []
    
    for input_text in input_text_list:
        response = gpt_client.chat.completions.create(
            model = gpt_model_name,
            messages=
                [
                {"role": "system", "content": "You are a text summarizer. you must answer without any explanation"},
                {"role": "user", "content": query+input_text}
                ],
            seed = seed_val,
        )
        gpt_output = response.choices[0].message.content
        summarized_text_list.append(gpt_output)
    
    return summarized_text_list

def classify_genre(genre_list, summarized_text_list:list, gpt_client, gpt_model_name='gpt-3.5-turbo', seed_val=42):
    
    summarized_full_story = ""
    for summarized_text in summarized_text_list:
            summarized_full_story += summarized_text
    
    classified_genre = None
    
    query = f"Please classify the story below into one of the following genres: {genre_list}.\n\
              you must answer with one of the items in the list.\n"
    
    while True:
        response = gpt_client.chat.completions.create(
            model=gpt_model_name,
            messages=
                [
                {"role": "system", "content": "You are a cynical assistant. you must answer with single word"},
                {"role": "user", "content": query+summarized_full_story}
                ],
            seed = seed_val
        )

        classified_genre = response.choices[0].message.content.lower()
        
        if classified_genre in genre_list:
            break
            
        else:
            print(f'invalid genre: {classified_genre}. retry genre classification')
            query += f'{classified_genre} is not in {genre_list}. you must answer except {classified_genre}.\n'
    
    return classified_genre

# ============================================================================================

# ========================================= retrieval ========================================

def retrieve_topk_image_path(prompt, clip_model, image_tensor_dict, image_path_dict, genre, device, k_val=10):
    
    text = clip.tokenize(prompt, truncate=True).to(device)
    
    with torch.no_grad():
        text_features = clip_model.encode_text(text)
        text_features /= text_features.norm(dim=-1, keepdim=True)
        cos_sim = (text_features @ image_tensor_dict[genre]).squeeze(0)
    
    top_values, top_indices = torch.topk(cos_sim, k=k_val, dim=0)
    
    retrieved_image_path_list = []
    
    for retrieved_idx in top_indices:
        retrieved_image_path_list.append(image_path_dict[genre][retrieved_idx])
    
    return retrieved_image_path_list

# ============================================================================================

# =================================== inversion & generation =================================

def inverse_reference_image(pipeline, ref_prompt, ref_image_path):
    
    num_inference_steps = 50
    x0 = np.array(load_image(ref_image_path).resize((1024, 1024)))
    zts = inversion.ddim_inversion(pipeline, x0, ref_prompt, num_inference_steps, 2)
    zT, inversion_callback = inversion.make_inversion_callback(zts, offset=5)
    
    return zT, inversion_callback

def generate_image(pipeline, prompt_list, ref_prompt, ref_latent, inversion_callback, device):

    num_inference_steps = 50
    
    prompt_list.insert(0, ref_prompt)
    
#     shared_score_shift = np.log(2)  # higher value induces higher fidelity, set 0 for no shift
    shared_score_shift = 0
#     shared_score_scale = 1.0  # higher value induces higher, set 1 for no rescale
    shared_score_scale = 0.75
    
    handler = sa_handler.Handler(pipeline)
    sa_args = sa_handler.StyleAlignedArgs(
        share_group_norm=True, share_layer_norm=True, share_attention=True,
        adain_queries=True, adain_keys=True, adain_values=False,
        shared_score_shift=shared_score_shift, shared_score_scale=shared_score_scale,)
    handler.register(sa_args)

    g_cpu = torch.Generator(device='cpu')
    g_cpu.manual_seed(10)

    latents = torch.randn(len(prompt_list), 4, 128, 128, device='cpu', generator=g_cpu,
                          dtype=pipeline.unet.dtype,).to(device)
    
    latents[0] = ref_latent

    generated_images = pipeline(prompt_list, latents=latents,
                        callback_on_step_end=inversion_callback,
                        num_inference_steps=num_inference_steps, guidance_scale=10.0).images

    handler.remove()
    
    return generated_images

# ============================================================================================


# ================================= image loading & encoding =================================

def encode_image_to_base64(image_list):
    encoded_image_list = []

    for img in image_list:
        img_byte_array = io.BytesIO()
        img.save(img_byte_array, format='PNG')
        img_byte_array = img_byte_array.getvalue()
        encoded_img_data = base64.b64encode(img_byte_array)
        encoded_img_str = encoded_img_data.decode('utf-8')
        encoded_image_list.append(encoded_img_str)
        
    return encoded_image_list

def encode_single_image_to_base64(signle_image):

    img_byte_array = io.BytesIO()
    signle_image.save(img_byte_array, format='PNG')
    img_byte_array = img_byte_array.getvalue()
    encoded_img_data = base64.b64encode(img_byte_array)
    encoded_img_str = encoded_img_data.decode('utf-8')
        
    return encoded_img_str


def load_images_from_path(image_path_list:list):
    loaded_image_list = []
    for image_path in image_path_list:
        _img = Image.open(image_path)
        loaded_image_list.append(_img)
    return loaded_image_list

def load_single_image_from_path(image_path):
    _img = Image.open(image_path)
    return _img
    
# ============================================================================================

def get_dict_for_retrieval(summarized_text, image_paths, encoded_images):
    ret_dict = {}
    ret_dict['summarizedExampleText'] = summarized_text
    ret_dict['content'] = []
    for idx in range(len(encoded_images)):
        ret_dict['content'].append({"path": image_paths[idx], "data": encoded_images[idx]})
        
    return ret_dict

def get_dict_for_generation(project_id, encoded_ref_image, input_text_list, encoded_images):
    ret_dict = {}
    ret_dict['projectId'] = project_id
    ret_dict['exampleImage'] = encoded_ref_image
    ret_dict['generatedItems'] = []
    for idx in range(len(encoded_images)):
        ret_dict['generatedItems'].append({"index": idx, "originalPrompt": input_text_list[idx], "generatedImage": encoded_images[idx]})
        
    return ret_dict

def get_paired_prompt(ref_image_path):

    ref_image_id = ref_image_path.split('/')[-1]

    df = pd.read_parquet('./metadata.parquet')
    paired_prompt = df[df['image_name'] == ref_image_id]['prompt'].values[0]

    return paired_prompt

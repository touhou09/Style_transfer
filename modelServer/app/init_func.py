from diffusers import StableDiffusionXLPipeline, DDIMScheduler
import torch
import clip
from PIL import Image
import os


def init_gpt_client(gpt_api_key):
    gpt_client = OpenAI(api_key = gpt_api_key)
    return gpt_client

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

def init_image_tensor_retrieval(clip_model, preprocess, genre_list, device, root_path_retrieval='./top5000_images'):
    image_tensor_dict = {}
    image_path_dict = {}

    for genre in tqdm(genre_list):
#         print(f'\ngenre: {genre}')
        images = os.listdir(f'{root_path_retrieval}/{genre}')
        images = [os.path.join(f'{root_path_retrieval}/{genre}', image_id) for image_id in images]

        image_tensors = None

        for image_path in images:
            with torch.no_grad():
                image = preprocess(Image.open(image_path)).unsqueeze(0).to(device)
                image_feature = clip_model.encode_image(image)
                image_feature /= image_feature.norm(dim=-1, keepdim=True)

                if image_tensors == None:
                    image_tensors = image_feature
                else:
                    image_tensors = torch.cat([image_tensors, image_feature])


        image_tensors_permute = image_tensors.permute(1,0)

        image_tensor_dict[genre] = image_tensors_permute
        image_path_dict[genre] = images
        
    return image_tensor_dict, image_path_dict
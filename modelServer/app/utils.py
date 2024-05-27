from PIL import Image
import base64
import io
import pandas as pd

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


def load_images_from_path(image_path_list:list):
    loaded_image_list = []
    for image_path in image_path_list:
        _img = Image.open(image_path)
        loaded_image_list.append(_img)
    return loaded_image_list

def get_dict_for_retrieval(summarized_text, image_paths, encoded_images):
    ret_dict = {}
    ret_dict['summarizedExampleText'] = summarized_text
    ret_dict['content'] = []
    for idx in range(len(encoded_images)):
        ret_dict['content'].append({"id": idx+1, "path": image_paths[idx], "data": encoded_images[idx]})
        
    return ret_dict

def get_dict_for_generation(summarized_texts, encoded_images):
    ret_dict = {}
    ret_dict['generatedItems'] = []
    for idx in range(len(encoded_images)):
        ret_dict['generatedItems'].append({"index": idx+1, "summarizedText": summarized_texts[idx], "generatedImage": encoded_images[idx]})
        
    return ret_dict

def get_paired_prompt(ref_image_path):
    
    
    ref_image_id = ref_image_path.split('/')[-1]

    df = pd.read_parquet('./metadata.parquet')
    paired_prompt = df[df['image_name'] == ref_image_id]['prompt'].values[0]

    return paired_prompt
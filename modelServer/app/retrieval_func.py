import clip
import torch

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
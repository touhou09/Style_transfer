from all_func import *

def ai_model():
    
    gpt_client = init_gpt_client(gpt_api_key = 'put your personal API key')
    
    diffusion_pipeline, clip_model, preprocess, device = init_models(clip_model_name = 'ViT-B/32')
    
    genre_list = ["horror", "romance", "fantasy", "science fiction", "thriller", "adventure", "mystery"]
    image_tensor_dict, image_path_dict = init_image_tensor_retrieval(clip_model, preprocess, genre_list, device)

    while(1):
        received_dict = get_data_dict_from_service() # service server로 부터 받는 함수가 있다고 가정   
        # received_dict는 파이썬 딕셔너리라고 가정
        # received_dict의 'task' 에 retrieval인지 generation인지에 대한 정보가 있다고 가정
        if received_dict['task'] == 'retrieval':
            example_text = received_dict['text'] # retrieval: example prompt in 'text'
            summarized_prompt = summarize_texts(example_text, gpt_client, seed_val=123)
            
            classified_genre = classify_genre(genre_list, summarized_prompt, gpt_client, seed_val=42)
            topk_img_path_list = retrieve_topk_image_path(prompt, clip_model, image_tensor_dict, image_path_dict, classified_genre, device)
            loaded_images = load_images_from_path(topk_img_path_list)
            encoded_base64_images = encode_image_to_base64(loaded_images)
            
            ret_dict = get_dict_for_retrieval(summarized_prompt, topk_img_path_list, encoded_base64_images)
            
        elif received_dict['task'] == 'generation':
            
            ref_image_path = received_dict['exampleImagePath']
            
            ref_prompt = get_paired_prompt(ref_image_path)
            
            item_dict_list = received_dict['basicItems']
            input_text_list = []
            for item_dict in item_dict_list:
                input_text_list.append(item_dict['promptText'])
            
            summarized_text_list = summarize_texts(input_text_list, gpt_client, seed_val=123)
            
            zT, inversion_callback = inverse_reference_image(diffusion_pipeline, ref_prompt, ref_image_path)
            
            generated_images = []
            for summarized_prompt in summarized_text_list:
                gen_img = generate_image(diffusion_pipeline, [summarized_prompt], ref_prompt, zT, inversion_callback, device)
                genereated_images.append(gen_img[1])
            
            encoded_base64_images = encode_image_to_base64(generated_images)
            
            ret_dict = get_dict_for_generation(summarized_text_list, encoded_base64_images)
            
        send_data_dict_to_service(ret_dict) # service server로 보내는 함수가 있다고 가정


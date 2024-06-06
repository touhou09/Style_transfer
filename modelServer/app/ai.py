from all_func import *

def ai(get_data):
    
    gpt_client = init_gpt_client(gpt_api_key = 'put your personal api key!')
    
    diffusion_pipeline, clip_model, preprocess, device = init_models(clip_model_name = 'ViT-B/32')
    
    genre_list = ["horror", "romance", "fantasy", "science fiction", "thriller", "adventure", "mystery"]
    
    received_dict = get_data
    # received_dict는 파이썬 딕셔너리라고 가정
    # received_dict의 'task' 에 retrieval인지 generation인지에 대한 정보가 있다고 가정
    if received_dict['task'] == 'retrieval':
        print('retrieval start')
        example_text = received_dict['text'] # retrieval: example prompt in 'text'
        summarized_prompt = summarize_texts([example_text], gpt_client, seed_val=123)

        classified_genre = classify_genre(genre_list, summarized_prompt, gpt_client, seed_val=42)
        
        image_tensor_dict, image_path_dict = init_image_tensor_retrieval(clip_model, preprocess,
                                                                         [classified_genre], device)
        
        topk_img_path_list = retrieve_topk_image_path(example_text, clip_model, image_tensor_dict,
                                                      image_path_dict, classified_genre, device, k_val=100)
        
        loaded_images = load_images_from_path(topk_img_path_list)
        encoded_base64_images = encode_image_to_base64(loaded_images)

        ret_dict = get_dict_for_retrieval(summarized_prompt[0], topk_img_path_list, encoded_base64_images)
        
        ### for debugging
        # for k, v in ret_dict.items():
        #     print(f"key: {k}")
        #     if k =='summarizedExampleText':
        #         print(v)
        #     print(f"type of val: {type(v)}")
        ###
        
        return ret_dict

    elif received_dict['task'] == 'generation':
        print('generation start')
        ref_image_path = received_dict['id']

        project_id = received_dict['projectId']
        
        ref_prompt = get_paired_prompt(ref_image_path)

        item_dict_list = received_dict['basicItems']
        input_text_list = []
        for item_dict in item_dict_list:
            input_text_list.append(item_dict['promptText'])

        summarized_text_list = summarize_texts(input_text_list, gpt_client, seed_val=123)

        zT, inversion_callback = inverse_reference_image(diffusion_pipeline, ref_prompt, ref_image_path)

        generated_images = []
        for summarized_prompt in summarized_text_list:
            gen_img = generate_image(diffusion_pipeline, [summarized_prompt], ref_prompt, zT,
                                     inversion_callback, device)
            
            generated_images.append(gen_img[1])

        encoded_base64_images = encode_image_to_base64(generated_images)

        ret_dict = get_dict_for_generation(project_id, input_text_list, encoded_base64_images)

        return ret_dict

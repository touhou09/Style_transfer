from all_func import *
import json
def ai(get_data):

    received_dict = get_data
    ret_dict = {}
    
    if received_dict['task'] == 'retrieval':
        print('retrieval start')

        with open('retrieval_output_example.json', 'r') as f:
            ret_dict = json.load(f)

        return ret_dict
    elif received_dict['task'] == 'generation':
        print('generation start')

        with open('generated_output_example.json', 'r') as f:
            ret_dict = json.load(f)

        return ret_dict

        

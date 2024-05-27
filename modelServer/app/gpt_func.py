def summarize_texts(input_text_list:list, gpt_client, gpt_model_name='gpt-3.5-turbo', seed_val=123)-> list: 
    
    if type(input_text_list) != list:
        input_text_list = list(input_text_list)
    
    
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

from fastapi import APIRouter
from typing import List
from models import GeneratedImageResponseDto, PromptRequestDto, basicItem, generatedItem
import json
import ai_dummy
# 라우터 인스턴스 생성
router = APIRouter()


@router.post("/generate-images", response_model=GeneratedImageResponseDto)
async def generate_images(request: PromptRequestDto) -> GeneratedImageResponseDto:
    data = request.dict()
    data['task'] = 'generation'
    
    # ai 함수 호출
    result = ai_dummy.ai(data)

    # with open('generated_output_example.json', 'w') as f:
    #     json.dump(result, f)

    # 생성된 이미지와 함께 응답 객체 생성
    response = GeneratedImageResponseDto(
        projectId=result['projectId'],
        generatedItems=[
            generatedItem(
                index=item['index'],
                promptText=item['originalPrompt'],
                generatedImage=item['generatedImage']
            ) for item in result['generatedItems']
        ]
    )
    
    print(response.projectId)
    for gen_item in response.generatedItems:
        print(f'type of index: {type(gen_item.index)}, val: {gen_item.index}')
        print(gen_item.promptText)
        print(gen_item.generatedImage[:10])
    
    return response
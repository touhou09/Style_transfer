from fastapi import APIRouter
from typing import List
from models import GeneratedImageResponseDto, PromptRequestDto, basicItem, generatedItem
import ai

# 라우터 인스턴스 생성
router = APIRouter()


@router.post("/generate-images", response_model=GeneratedImageResponseDto)
async def generate_images(request: PromptRequestDto) -> GeneratedImageResponseDto:
    data = request.dict()
    data['task'] = 'generation'
    
    # ai 함수 호출
    result = ai(data)
    
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
    
    return response
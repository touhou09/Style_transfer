from fastapi import APIRouter
from typing import List
from models import GeneratedImageResponseDto, PromptRequestDto, basicItem, generatedItem
from modelServer.app import ai

# 라우터 인스턴스 생성
router = APIRouter()

@router.post("/generate-images", response_model=GeneratedImageResponseDto)
async def generate_images(request: PromptRequestDto) -> GeneratedImageResponseDto:
    
    data = request.model_dump(dict)
    # task: generation 추가
    data['task'] = 'generation'
    result = ai(data)
    
    # 생성된 이미지와 함께 응답 객체 생성
    # projectId, 예시 이미지, [index, 프롬프트, 생성된 이미지]
    response = GeneratedImageResponseDto(
        projectId=request.projectId,
        exampleImage=request.id, # 이 부분을 인코딩 이미지로
        generatedItems=[
            generatedItem(
                index=idx,
                promptText=item.promptText,  # 기본 프롬프트 텍스트를 그대로 사용
                generatedImage=generated_image
            ) for idx, (item, generated_image) in enumerate(zip(request.basicItems, result['generatedImages']))
        ]
    )
    
    return response

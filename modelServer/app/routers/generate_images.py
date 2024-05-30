from fastapi import APIRouter
from typing import List
from models import GeneratedImageResponseDto, PromptRequestDto, basicItem, generatedItem
from modelServer.app import ai

# 라우터 인스턴스 생성
router = APIRouter()

@router.post("/generate-images", response_model=GeneratedImageResponseDto)
async def generate_images(request: PromptRequestDto) -> GeneratedImageResponseDto:
    
    data = request.model_dump(dict)
    # 여기도 task: generation 추가

    data = ai(data)

    
    # 생성된 이미지와 함께 응답 객체 생성
    response = GeneratedImageResponseDto(projectId=data, exampleImage=data, generatedItems=data)
    
    return response
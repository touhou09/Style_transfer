from models import Image, ImageResponseDto, TokenRequestDto
from fastapi import APIRouter
from typing import List
from modelServer.app import ai

router = APIRouter()

@router.post("/exampleImages", response_model=ImageResponseDto)
async def example_images(request: TokenRequestDto) -> ImageResponseDto:

    data = request.model_dump(dict)
    # data에서 task : retrieval 추가해야함
    tmp = ai(data)

    # 응답 객체 생성
    response = ImageResponseDto(summaryText=tmp, path=tmp, images=tmp)
    return response
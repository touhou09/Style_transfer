from models import Image, ImageResponseDto, TokenRequestDto
from fastapi import APIRouter
from typing import List

def searchExamples(text: str):
    # 탐색 구현
    # 아래는 예제 파일이므로 comment or delete
    
    data = [
        Image(id="1", data="임시이미지데이터1(Base64)"),
        Image(id="2", data="임시이미지데이터2(Base64)")
    ]

    return data

router = APIRouter()

@router.post("/exampleImages", response_model=ImageResponseDto)
async def example_images(request: TokenRequestDto) -> ImageResponseDto:
    # 실제 이미지 처리 로직을 여기에 구현
    # 예시로, 임시 이미지 데이터를 생성 -> 필요 없어지면 comment or delete
    data = searchExamples(request.text)

    # 응답 객체 생성
    # response = ImageResponseDto(token=request.token, images=fake_images)
    response = ImageResponseDto(images=data)
    
    return response
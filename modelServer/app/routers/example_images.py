from models import Image, ImageResponseDto, TokenRequestDto
from fastapi import APIRouter
from typing import List
from modelServer.app import ai

router = APIRouter()

@router.post("/exampleImages", response_model=ImageResponseDto)
async def example_images(request: TokenRequestDto) -> ImageResponseDto:

    # request 데이터를 dictionary로 변환
    data = request.model_dump(dict)
    
    # task : retrieval을 data에 추가
    data['task'] = 'retrieval'
    
    # ai 함수 호출
    tmp = ai(data)
    
    # 응답 객체 생성
    # summury한 text, [id(path), 인코딩 이미지 string]
    response = ImageResponseDto(
        summaryText=tmp.get("summarizedExampleText"),
        images=[
            Image(
                id=str(item.get("path")),
                data=item.get("data")
            ) for item in tmp.get("content", [])
        ]
    )
    
    return response
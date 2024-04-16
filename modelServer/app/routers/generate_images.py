from fastapi import APIRouter
from typing import List
from models import GeneratedImageResponseDto, PromptRequestDto, exampleItem, generatedItem

# 라우터 인스턴스 생성
router = APIRouter()

# image_generation.py 파일 내에 이미지 생성 로직을 포함한 함수를 정의
def create_generated_images(items: List[exampleItem]) -> List[generatedItem]:
    # 입력받은 exampleItems를 기반으로 새로운 이미지 데이터를 생성합니다.
    
    return [
        generatedItem(
            id=item.id,
            promptText=item.promptText,
            generatedImage=f"Base64_Encoded_Generated_Image_Data_For_{item.promptText}"
        ) for item in items
    ]


@router.post("/generate-images", response_model=GeneratedImageResponseDto)
async def generate_images(request: PromptRequestDto) -> GeneratedImageResponseDto:
    # 입력 받은 exampleItems를 기반으로 새로운 이미지 데이터 생성
    data = create_generated_images(request.exampleItems)
    
    # 생성된 이미지와 함께 응답 객체 생성
    response = GeneratedImageResponseDto(id=request.projectId, generatedItems=data)
    
    return response


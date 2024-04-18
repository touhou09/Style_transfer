from fastapi import APIRouter
from typing import List
from models import GeneratedImageResponseDto, PromptRequestDto, basicItem, generatedItem

# 라우터 인스턴스 생성
router = APIRouter()

def create_generated_images(items: List[basicItem], common_image: str) -> List[generatedItem]:
    """ 
    입력받은 exampleItems를 기반으로 새로운 이미지 데이터를 생성합니다.
    각 항목의 index를 이미지 데이터에 추가하여 반환합니다.
    """
    return [
        generatedItem(
            index=item.index,  # 'id' 대신 'index' 필드 사용
            promptText=item.promptText,
            generatedImage=common_image + f"_generated_{item.index}"  # 각 항목의 index를 이미지 데이터에 추가
        ) for item in items
    ]

@router.post("/generate-images", response_model=GeneratedImageResponseDto)
async def generate_images(request: PromptRequestDto) -> GeneratedImageResponseDto:
    # 입력 받은 exampleItems를 기반으로 새로운 이미지 데이터 생성
    # request.exampleImage를 공통 이미지 데이터로 사용
    data = create_generated_images(request.basicItems, request.exampleImage)
    
    # 생성된 이미지와 함께 응답 객체 생성
    response = GeneratedImageResponseDto(projectId=request.projectId, generatedItems=data)
    
    return response
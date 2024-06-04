from models import Image, ImageResponseDto, exampleRequestDto
from fastapi import APIRouter, HTTPException
from typing import List
import dummy.ai_dummy
import ai

router = APIRouter()

@router.post("/exampleImages", response_model=ImageResponseDto)
async def example_images(request: exampleRequestDto) -> ImageResponseDto:

    # request 데이터를 dictionary로 변환
    data = request.dict()
    
    # task : retrieval을 data에 추가
    data['task'] = 'retrieval'
    
    # ai 함수 호출
    try:
        # tmp = ai_dummy.ai(data)
        tmp = ai.ai(data)
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    
    # 응답 객체 생성
    try:
        summary_text = tmp["summarizedExampleText"]
        if isinstance(summary_text, list):
            summary_text = " ".join(summary_text)  # 리스트인 경우 문자열로 변환
        
        images_list = tmp["content"]
        images = [
            Image(
                id=str(item["path"]),
                data=item["data"]
            ) for item in images_list
        ]
        
        response = ImageResponseDto(
            summaryText=summary_text,
            images=images
        )
        
    except KeyError as e:
        raise HTTPException(status_code=500, detail=f"Missing expected data in response: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Unexpected error occurred: {str(e)}")
    
    # print(response.summaryText)
    # for image in response.images:
    #     print(image.id)
    #     print(image.data[:10])

    return response

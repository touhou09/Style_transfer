from pydantic import BaseModel
from typing import List, Optional

# 예시 이미지 생성 model
class Image(BaseModel):
    id: str
    data: str  # Base64 인코딩된 이미지 데이터

# 프롬프트 항목 모델
class basicItem(BaseModel):
    index: int  # 각 PromptItem의 고유 Index, 변경: 'id'에서 'index'로 변경
    promptText: str  # 프롬프트 텍스트

# 프롬프트와 생성된 이미지 전송 시 model
class generatedItem(BaseModel):
    index: str  # 각 PromptItem의 고유 ID
    promptText: str  # 프롬프트 텍스트
    generatedImage: str  # Base64 인코딩된 이미지 데이터

# 최초 예시이미지 제시 시 사용하는 prompt 전달 dto 
class TokenRequestDto(BaseModel):
    text: str

# 예시 이미지 전달 dto
class ImageResponseDto(BaseModel):
    summaryText : str
    images: List[Image]

# 프롬프트 요청 DTO
class PromptRequestDto(BaseModel):
    projectId: str  # 프로젝트 ID
    id: str
    basicItems: List[basicItem]  # 해당 프로젝트와 관련된 프롬프트 항목들 # 해당 프로젝트와 관련된 프롬프트 항목들

# 생성한 이미지를 prompt와 함께 전달하는 dto
class GeneratedImageResponseDto(BaseModel):
    projectId: str
    exampleImage: str  # 해당 프로젝트와 관련된 공통 이미지 데이터 (Base64 인코딩된 이미지 데이터)
    generatedItems: List[generatedItem]
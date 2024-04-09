from pydantic import BaseModel
from typing import List, Optional

# 예시 이미지 생성 model
class Image(BaseModel):
    id: str
    data: str  # Base64 인코딩된 이미지 데이터

# 프롬프트와 예시 이미지 전송 시 model
class exampleItem(BaseModel):
    id: str  # 각 PromptItem의 고유 ID
    promptText: str  # 프롬프트 텍스트
    exampleImage: str  # Base64 인코딩된 이미지 데이터

# 프롬프트와 생성된 이미지 전송 시 model
class generatedItem(BaseModel):
    id: str  # 각 PromptItem의 고유 ID
    promptText: str  # 프롬프트 텍스트
    generatedImage: str  # Base64 인코딩된 이미지 데이터

# 최초 예시이미지 제시 시 사용하는 prompt 전달 dto 
class TokenRequestDto(BaseModel):
    token: str
    text: str

# 예시 이미지 전달 dto
class ImageResponseDto(BaseModel):
    token: str
    images: List[Image]

# 예시 이미지와 프롬프트를 전달하는 dto
class PromptRequestDto(BaseModel):
    projectId: str  # 프로젝트 ID
    exampleItems: List[exampleItem]  # 해당 프로젝트와 관련된 프롬프트 항목들

# 생성한 이미지를 prompt와 함께 전달하는 dto
class GeneratedImageResponseDto(BaseModel):
    id: str
    generatedItems: List[generatedItem]
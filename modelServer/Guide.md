# Model server guide
```　bash
pip install -r requirements.txt
```

requirements.txt 추후 업데이트 예정

## Environment Setup

```　bash
conda create -n visual_fable python=3.9
conda activate visual_fable
pip install torch==2.0.1 torchvision==0.15.2 torchaudio==2.0.2 --index-url https://download.pytorch.org/whl/cu118
pip install diffusers mediapy einops transformers accelerate ftfy regex tqdm pandas
pip install git+https://github.com/openai/CLIP.git
pip install openai
pip install pyarrow
```


## retrieval
**input from service**
```json
{
  "text": "retrieval 용 예시 텍스트"
}

```

**output to service**
```json
{
  "summarizedExampleText": "요약된 텍스트",
  "content": [
    {
      "path": "이미지 데이터 0에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
      "data": "이미지 데이터 0 (Base64 인코딩)"
    },
    {
      "path": "이미지 데이터 1에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
      "data": "이미지 데이터 1 (Base64 인코딩)"
    },
    {
      "path": "이미지 데이터 2에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
      "data": "이미지 데이터 2 (Base64 인코딩)"
    }
  ],
}
```

## generation

**input from service**
```json
{
  "projectId" : "id",
  "id": "이미지 데이터에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
  "basicItems": [
    {
      "index": 0,
      "promptText": "Example prompt text 0"
    },
    {
      "index": 1,
      "promptText": "Example prompt text 1"
    },
    {
      "index": 2,
      "promptText": "Example prompt text 2"
    }
  ]
}
```

**output to service**
```json
{
  "projectId" : "id",
  "generatedItems": [
    {
      "index": 0,
      "originalPrompt": "원래 텍스트 0",
      "generatedImage": "이미지 데이터 0 (Base64 인코딩)"
    },
    {
      "index": 1,
      "originalPrompt": "원래 텍스트 1",
      "generatedImage": "이미지 데이터 1 (Base64 인코딩)"
    },
    {
      "index": 2,
      "originalPrompt": "원래 텍스트 2",
      "generatedImage": "이미지 데이터 2 (Base64 인코딩)"
    }
  ]
}
```

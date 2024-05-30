# model server guide
```　bash
pip install -r requirements.txt
```

requirements.txt 추후 업데이트 예정

# Model Server

## retrieval
**input from service**
```json
{
  "task": "retrieval or generation",
  "text": "retrieval 용 예시 텍스트"
}

```

**output to service**
```json
{
  "summarizedExampleText": "요약된 텍스트",
  "content": [
    {
      "id": "0",
      "path": "이미지 데이터 0에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
      "data": "이미지 데이터 0 (Base64 인코딩)"
    },
    {
      "id": "1",
      "path": "이미지 데이터 1에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
      "data": "이미지 데이터 1 (Base64 인코딩)"
    },
    {
      "id": "2",
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
  "task": "retrieval or generation",
  "exampleImagePath": "이미지 데이터에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
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
  "generatedItems": [
    {
      "index": 0,
      "summarizedText": "요약된 텍스트 0",
      "generatedImage": "이미지 데이터 0 (Base64 인코딩)"
    },
    {
      "index": 1,
      "summarizedText": "요약된 텍스트 1",
      "generatedImage": "이미지 데이터 1 (Base64 인코딩)"
    },
    {
      "index": 2,
      "summarizedText": "요약된 텍스트 2",
      "generatedImage": "이미지 데이터 2 (Base64 인코딩)"
    }
  ]
}
```

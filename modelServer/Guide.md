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
  "task": retrieval or generation,
  "text": "retrieval 용 예시 텍스트"
}

```

**output to service**
```json
{
  "summarizedExampleText": "요약된 텍스트",
  "content": [
    {
      "id": "1",
      "path": "이미지 데이터에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
      "data": "이미지 데이터 (Base64 인코딩)"
    },
    {
      "id": "2",
      "path": "이미지 데이터에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
      "data": "이미지 데이터 (Base64 인코딩)"
    }
  ],
}
```

## generation

**input from service**
```json
{
  "task": retrieval or generation,
  "exampleImagePath": "이미지 데이터에 대한 경로 (로컬 머신에서 전송할 이미지를 저장한 경로)",
  "basicItems": [
    {
      "index": 1,
      "promptText": "Example prompt text 1"
    },
    {
      "index": 2,
      "promptText": "Example prompt text 2"
    },
    {
      "index": 3,
      "promptText": "Example prompt text 3"
    }
  ]
}
```

**output to service**
```json
{
  "generatedItems": [
    {
      "index": 1,
      "summarizedText": "요약된 텍스트 1",
      "generatedImage": 이미지 데이터 1 (Base64 인코딩)
    },
    {
      "index": 2,
      "summarizedText": "요약된 텍스트 2",
      "generatedImage": 이미지 데이터 2 (Base64 인코딩)
    },
    {
      "index": 3,
      "summarizedText": "요약된 텍스트 3",
      "generatedImage": 이미지 데이터 3 (Base64 인코딩)
    }
  ]
}
```

# 고려 사항
다수의 요청이 들어올 때는 어떻게?

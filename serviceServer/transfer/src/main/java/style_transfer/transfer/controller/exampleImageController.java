package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import style_transfer.transfer.service.exampleImageServe;
import style_transfer.transfer.repository.image;
import style_transfer.transfer.repository.tokenRequestDto; // 요청 DTO의 정확한 위치에 따라 import 경로를 조정해야 합니다.
import java.util.List;

@RestController
public class exampleImageController {
    private final exampleImageServe imageService;

    @Autowired
    public exampleImageController(exampleImageServe imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/api/images")
    public List<image> createImages(@RequestBody tokenRequestDto requestDto) {
        // 서비스 계층에 텍스트 데이터를 전달하고, 처리된 이미지 데이터를 받아 반환
        // 예제에서는 서비스 계층의 메소드가 수정되어야 함을 가정합니다.
        // 예: return imageService.processTextAndGetImages(requestDto.getText());
        return null; // 실제 구현에 따라 변경
    }
}

package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
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

    /*
    @PostMapping("/api/images")
    public Mono<ResponseEntity<List<image>>> createImages(@RequestBody tokenRequestDto requestDto) {
        // 서비스 계층에서 이미지 응답을 비동기적으로 가져옵니다.
        return imageService.getImageResponse(requestDto.getText(), requestDto.getToken())
                .map(imageResponseDto -> ResponseEntity.ok().body(imageResponseDto.getImages()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    */

    @PostMapping("/api/images")
    public Mono<ResponseEntity<List<image>>> createImages(@RequestBody tokenRequestDto requestDto) {
        // 토큰을 제외하고 텍스트 정보만 서비스 계층에 전달
        return imageService.getImageResponse(requestDto.getText())
                .map(imageResponseDto -> ResponseEntity.ok().body(imageResponseDto.getImages()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
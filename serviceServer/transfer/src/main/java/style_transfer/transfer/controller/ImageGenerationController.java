package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;
import style_transfer.transfer.service.ImageGenerationService;
import style_transfer.transfer.service.TokenValidationService;

@RestController
public class ImageGenerationController {

    private final ImageGenerationService imageService;
    private final TokenValidationService tokenService;  // 토큰 검증 서비스 추가

    @Autowired
    public ImageGenerationController(ImageGenerationService imageService, TokenValidationService tokenService) {
        this.imageService = imageService;
        this.tokenService = tokenService;  // 의존성 주입
    }

    @PostMapping("/api/generate-images")
    public Mono<generatedImageResponseDto> generateImages(@RequestBody promptRequestDto request) {
        // basicItems에서 하나라도 index가 2 이상인 경우 토큰 검증
        boolean shouldValidateToken = request.getBasicItems().stream()
                .anyMatch(item -> item.getIndex() >= 2);

        if (shouldValidateToken) {
            boolean isValidToken = tokenService.validateToken(request.getToken());
            if (!isValidToken) {
                // 토큰이 유효하지 않을 경우 에러 응답
                return Mono.error(new RuntimeException("Invalid token"));
            }
        }

        // 토큰이 유효하면 이미지 생성 서비스 호출
        return imageService.generateImages(request);
    }
}

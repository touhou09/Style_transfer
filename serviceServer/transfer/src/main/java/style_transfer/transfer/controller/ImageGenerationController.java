package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;
import style_transfer.transfer.service.ImageGenerationService;
import style_transfer.transfer.service.TokenValidationService;
import style_transfer.transfer.service.UserService;

@RestController
public class ImageGenerationController {

    private final ImageGenerationService imageService;
    private final TokenValidationService tokenService;  // 토큰 검증 서비스 추가
    private final UserService userService;

    @Autowired
    public ImageGenerationController(ImageGenerationService imageService, TokenValidationService tokenService, UserService userService) {
        this.imageService = imageService;
        this.tokenService = tokenService;  // 의존성 주입
        this.userService = userService;
    }

    @PostMapping("/generate-images")
    public Mono<ResponseEntity<generatedImageResponseDto>> generateImages(@RequestBody promptRequestDto request) {
        // index 2이상인지 확인
        boolean shouldValidateToken = request.getBasicItems().stream()
                .anyMatch(item -> item.getIndex() >= 2);
        // 토큰이 정상적인지 확인
        boolean isValidToken = tokenService.validateToken(request.getToken());

        if ((request.getToken() == null || request.getToken().isEmpty()) && !shouldValidateToken) {
            return imageService.generateImages(request)
                    .map(response -> ResponseEntity.ok(response));
        }

        if (shouldValidateToken && !isValidToken) {
            return Mono.error(new InvalidTokenException("Invalid token"));
        }

        return imageService.generateImages(request)
                .flatMap(response -> {
                    if (isValidToken) {
                        return userService.saveProject(request.getToken(), response)
                                .thenReturn(response)
                                .map(res -> ResponseEntity.ok(res));
                    } else {
                        return Mono.just(ResponseEntity.ok(response));
                    }
                });
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }
}

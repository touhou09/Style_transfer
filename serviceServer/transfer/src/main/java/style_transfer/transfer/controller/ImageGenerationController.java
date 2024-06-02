package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;
import style_transfer.transfer.service.ImageGenerationService;
import style_transfer.transfer.service.TokenValidationService;
import style_transfer.transfer.service.UserService;

@RestController
@RequestMapping("/api")
public class ImageGenerationController {

    private final ImageGenerationService imageService;
    private final TokenValidationService tokenService;
    private final UserService userService;

    @Autowired
    public ImageGenerationController(ImageGenerationService imageService, TokenValidationService tokenService, UserService userService) {
        this.imageService = imageService;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/generate-images")
    public Mono<ResponseEntity<generatedImageResponseDto>> generateImages(@RequestBody promptRequestDto request) {
        boolean shouldValidateToken = request.getBasicItems().size() >= 2;

        if (shouldValidateToken) {
            if (request.getToken() == null || request.getToken().isEmpty() || !tokenService.validateToken(request.getToken())) {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token"));
            }
        }

        // Token이 없고 validate 조건도 필요 없는 경우, 이미지 생성
        if ((request.getToken() == null || request.getToken().isEmpty()) && !shouldValidateToken) {
            return imageService.generateImages(request)
                    .map(ResponseEntity::ok);
        }

        // 유효한 토큰이 있는 경우 이미지 생성 및 프로젝트 저장
        return imageService.generateImages(request)
                .flatMap(response -> {
                    if (shouldValidateToken) {
                        return userService.saveProject(request.getToken(), response)
                                .thenReturn(response)
                                .map(ResponseEntity::ok);
                    } else {
                        return Mono.just(ResponseEntity.ok(response));
                    }
                })
                .onErrorResume(e -> {
                    if (e instanceof ResponseStatusException) {
                        return Mono.just(ResponseEntity.status(((ResponseStatusException) e).getStatusCode()).body(null));
                    } else if (e instanceof WebClientRequestException) {
                        // WebClientRequestException 예외 처리 추가
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                    }
                });
    }
}

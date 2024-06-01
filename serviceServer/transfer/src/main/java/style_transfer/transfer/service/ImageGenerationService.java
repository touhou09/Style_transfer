package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImageGenerationService {

    private final WebClient webClient;
    private final UserService userService; // UserService 주입

    @Autowired
    public ImageGenerationService(WebClient.Builder webClientBuilder, UserService userService, @Value("${fastapi.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.userService = userService;
    }

    public Mono<generatedImageResponseDto> generateImages(promptRequestDto request) {
        // projectId가 없다면 생성 및 설정
        if (request.getProjectId() == null || request.getProjectId().isEmpty()) {
            String projectId = UUID.randomUUID().toString();
            request.setProjectId(projectId);
        }

        // 외부 API로 POST 요청을 보내고 응답을 처리
        return webClient.post()
                .uri("/generate-images")
                .body(Mono.just(request), promptRequestDto.class)
                .retrieve()
                .bodyToMono(generatedImageResponseDto.class)
                .flatMap(response -> {
                    response.setTime(LocalDateTime.now());
                    response.setProjectId(request.getProjectId());
                    return userService.saveProject(request.getToken(), response)
                            .thenReturn(response);
                })
                .onErrorMap(WebClientResponseException.class, ex -> new Exception("API call failed: " + ex.getMessage()));
    }
}

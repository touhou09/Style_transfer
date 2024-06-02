package style_transfer.transfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImageGenerationService {

    private final WebClient webClient;
    private final UserService userService; // UserService 주입

    private static final Logger log = LoggerFactory.getLogger(ImageGenerationService.class);

    @Autowired
    public ImageGenerationService(WebClient.Builder webClientBuilder, UserService userService, @Value("${fastapi.url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB로 설정
                        .build())
                .build();
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
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new RuntimeException("API call failed: " + errorBody))))
                .bodyToMono(generatedImageResponseDto.class)
                .flatMap(response -> {
                    response.setTime(LocalDateTime.now());
                    response.setProjectId(request.getProjectId());

                    // 토큰이 있는 경우에만 saveProject 함수 실행
                    if (request.getToken() != null && !request.getToken().isEmpty()) {
                        return userService.saveProject(request.getToken(), response)
                                .thenReturn(response);
                    } else {
                        return Mono.just(response);
                    }
                })
                .doOnError(error -> {
                    // Log error
                    log.error("Error: ", error);
                });
    }
}
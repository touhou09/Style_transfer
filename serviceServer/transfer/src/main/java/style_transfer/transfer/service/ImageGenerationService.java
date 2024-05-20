package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;

@Service
public class ImageGenerationService {

    private final WebClient webClient;

    @Autowired
    public ImageGenerationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
    }

    public Mono<generatedImageResponseDto> generateImages(promptRequestDto request) {
        // 외부 API로 POST 요청을 보내고 응답을 처리
        return webClient.post()
                .uri("/generate-images")
                .body(Mono.just(request), promptRequestDto.class) // 직접 받은 promptRequestDto를 사용
                .retrieve()
                .bodyToMono(generatedImageResponseDto.class)
                .onErrorMap(WebClientResponseException.class, ex -> new Exception("API call failed: " + ex.getMessage()));
    }
}
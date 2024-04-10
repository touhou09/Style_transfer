package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.imageResponseDto;
import style_transfer.transfer.repository.tokenRequestDto;

@Service
public class exampleImageServe {
    private final WebClient webClient;

    @Autowired
    public exampleImageServe(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
    }
    public Mono<imageResponseDto> getImageResponse(String text, String token) {
        tokenRequestDto requestDto = new tokenRequestDto(token, text);

        return this.webClient.post()
                .uri("/api/images") // url을 modelServer쪽 api에 맞춰서 처리
                .body(Mono.just(requestDto), tokenRequestDto.class)
                .retrieve()
                .bodyToMono(imageResponseDto.class);
    }
}

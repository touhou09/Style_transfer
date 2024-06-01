package style_transfer.transfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.image;
import style_transfer.transfer.repository.imageResponseDto;
import style_transfer.transfer.repository.tokenRequestDto;

import java.util.List;

@Service
public class exampleImageServe {
    private final WebClient webClient;

    @Autowired
    public exampleImageServe(WebClient.Builder webClientBuilder, @Value("${fastapi.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl)
                .build();
    }

    public Mono<PageImpl<image>> getImageResponse(String text, int page, int size) {
        tokenRequestDto requestDto = new tokenRequestDto(text);
        Logger logger = LoggerFactory.getLogger(exampleImageServe.class);

        return this.webClient.post()
                .uri("/exampleImages")
                .body(Mono.just(requestDto), tokenRequestDto.class)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            String errorMessage = "Error: " + clientResponse.statusCode() + " - " + errorBody;
                            return Mono.error(new RuntimeException(errorMessage));
                        }))
                .bodyToMono(imageResponseDto.class)
                .map(responseDto -> {
                    Pageable pageable = PageRequest.of(page, size);
                    List<image> images = responseDto.getImages().getContent();
                    int start = (int) pageable.getOffset();
                    int end = Math.min((start + pageable.getPageSize()), images.size());
                    List<image> subList = images.subList(start, end);
                    return new PageImpl<>(subList, pageable, images.size());
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    String errorMessage = "WebClient error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
                    return Mono.error(new RuntimeException(errorMessage));
                })
                .onErrorResume(Exception.class, e -> {
                    String errorMessage = "General error: " + e.getMessage();
                    return Mono.error(new RuntimeException(errorMessage));
                });
    }
}
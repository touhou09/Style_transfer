package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.image;
import style_transfer.transfer.repository.imageResponseDto;
import style_transfer.transfer.repository.tokenRequestDto;

import java.util.List;

@Service
public class exampleImageServe {
    private final WebClient webClient;

    @Autowired
    public exampleImageServe(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
    }

    public Mono<Page<image>> getImageResponse(String text, int page, int size) {
        tokenRequestDto requestDto = new tokenRequestDto(text);

        return this.webClient.post()
                .uri("/exampleImages")
                .body(Mono.just(requestDto), tokenRequestDto.class)
                .retrieve()
                .bodyToMono(imageResponseDto.class)
                .map(responseDto -> {
                    Pageable pageable = PageRequest.of(page, size);
                    List<image> images = (List<image>) responseDto.getImages();
                    int start = (int) pageable.getOffset();
                    int end = Math.min((start + pageable.getPageSize()), images.size());
                    List<image> subList = images.subList(start, end);
                    return new PageImpl<>(subList, pageable, images.size());
                });
    }
}

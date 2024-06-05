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
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.exampleRequestDto;
import style_transfer.transfer.repository.image;
import style_transfer.transfer.repository.imageResponseDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class exampleImageServe {
    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(exampleImageServe.class);
    private List<image> cachedImages = new ArrayList<>();

    @Autowired
    public exampleImageServe(WebClient.Builder webClientBuilder, @Value("${fastapi.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024)) // 16MB로 설정
                .build();
    }

    public Mono<PageImpl<image>> getImageResponse(String text, int page, int size) {
        // 캐시에 충분한 이미지가 있는지 확인
        if (cachedImages.size() < (page + 1) * size) {
            return fetchImagesFromModelServer(text)
                    .then(Mono.defer(() -> getPagedImages(page, size)));
        } else {
            return getPagedImages(page, size);
        }
    }

    private Mono<Void> fetchImagesFromModelServer(String text) {
        exampleRequestDto requestDto = new exampleRequestDto(text);

        return this.webClient.post()
                .uri("/exampleImages")
                .body(Mono.just(requestDto), exampleRequestDto.class)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            String errorMessage = "Error: " + clientResponse.statusCode() + " - " + errorBody;
                            logger.error(errorMessage);
                            return Mono.error(new RuntimeException(errorMessage));
                        }))
                .bodyToMono(imageResponseDto.class)
                .doOnNext(responseDto -> {
                    cachedImages.addAll(responseDto.getImages());
                })
                .then();
    }

    private Mono<PageImpl<image>> getPagedImages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), cachedImages.size());

        if (start >= cachedImages.size()) {
            return Mono.just(createEmptyPage(page, size));
        } else {
            List<image> subList = new ArrayList<>(cachedImages.subList(start, end));
            return Mono.just(new PageImpl<>(subList, pageable, cachedImages.size()));
        }
    }

    private PageImpl<image> createEmptyPage(int page, int size) {
        return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
    }
}


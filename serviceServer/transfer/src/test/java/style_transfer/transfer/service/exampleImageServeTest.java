package style_transfer.transfer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import style_transfer.transfer.repository.exampleRequestDto;
import style_transfer.transfer.repository.image;
import style_transfer.transfer.repository.imageResponseDto;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class, SpringExtension.class})
class exampleImageServeTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Value("${fastapi.url}")
    private String baseUrl;

    private exampleImageServe exampleImageServe;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        initMocks(this);

        // WebClient.Builder 설정
        lenient().when(webClientBuilder.baseUrl(any(String.class))).thenReturn(webClientBuilder);
        lenient().when(webClientBuilder.exchangeStrategies(any(ExchangeStrategies.class))).thenReturn(webClientBuilder);
        lenient().when(webClientBuilder.codecs(any(Consumer.class))).thenReturn(webClientBuilder);
        lenient().when(webClientBuilder.build()).thenReturn(webClient);

        exampleImageServe = new exampleImageServe(webClientBuilder, baseUrl);
    }

    @Test
    void getImageResponse_Success() {
        exampleRequestDto requestDto = new exampleRequestDto("test");

        image img1 = new image();
        imageResponseDto responseDto = new imageResponseDto();
        responseDto.setImages(Collections.singletonList(img1));

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/exampleImages")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // onStatus 메서드 추가
        when(responseSpec.onStatus(any(), any(Function.class))).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(imageResponseDto.class)).thenReturn(Mono.just(responseDto));

        Mono<PageImpl<image>> result = exampleImageServe.getImageResponse("test", 0, 10);

        StepVerifier.create(result)
                .expectNextMatches(page -> page.getContent().contains(img1) && page.getTotalElements() == 1)
                .verifyComplete();
    }

    @Test
    void getImageResponse_Error() {
        exampleRequestDto requestDto = new exampleRequestDto("test");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/exampleImages")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // onStatus 메서드 추가
        when(responseSpec.onStatus(any(), any(Function.class))).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(imageResponseDto.class)).thenReturn(Mono.error(new RuntimeException("Error")));

        Mono<PageImpl<image>> result = exampleImageServe.getImageResponse("test", 0, 10);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}

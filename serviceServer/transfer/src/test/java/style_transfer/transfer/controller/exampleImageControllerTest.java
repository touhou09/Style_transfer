package style_transfer.transfer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.exampleRequestDto;
import style_transfer.transfer.repository.image;
import style_transfer.transfer.service.exampleImageServe;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@ActiveProfiles("test")
class exampleImageControllerTest {

    @MockBean
    private exampleImageServe imageService;

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void testHandleRequest() {
        image img1 = new image();
        PageImpl<image> pageResponse = new PageImpl<>(Collections.singletonList(img1), PageRequest.of(0, 10), 1);

        when(imageService.getImageResponse(any(String.class), anyInt(), anyInt())).thenReturn(Mono.just(pageResponse));

        exampleRequestDto requestDto = new exampleRequestDto("test");

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/images").queryParam("page", "0").queryParam("size", "10").build())
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content[0]").exists()
                .consumeWith(document("example-image")); // REST Docs 문서화
    }
}

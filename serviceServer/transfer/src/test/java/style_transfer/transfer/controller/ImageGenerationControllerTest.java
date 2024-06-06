package style_transfer.transfer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;
import style_transfer.transfer.service.ImageGenerationService;
import style_transfer.transfer.service.ProjectService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest(controllers = ImageGenerationController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import({ImageGenerationService.class, ProjectService.class})
class ImageGenerationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ImageGenerationService imageService;

    @MockBean
    private ProjectService projectService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToController(new ImageGenerationController(imageService, projectService))
                .configureClient()
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void testGenerateImages() {
        promptRequestDto request = new promptRequestDto();
        request.setProjectId("1234");
        request.setBasicItems(Collections.singletonList(new promptRequestDto.basicItem(0, "Generate this image")));

        generatedImageResponseDto responseDto = new generatedImageResponseDto();
        responseDto.setProjectId("1234");
        responseDto.setTime(LocalDateTime.now());

        when(imageService.generateImages(any(promptRequestDto.class))).thenReturn(Mono.just(responseDto));

        webTestClient.post()
                .uri("/api/generate-images")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.projectId").isEqualTo("1234")
                .consumeWith(document("generate-images"));
    }
}

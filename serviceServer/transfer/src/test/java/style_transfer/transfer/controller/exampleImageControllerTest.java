package style_transfer.transfer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.image;
import style_transfer.transfer.repository.tokenRequestDto;
import style_transfer.transfer.service.exampleImageServe;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@WebFluxTest(controllers = exampleImageController.class)
public class exampleImageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private exampleImageServe imageService;

    private tokenRequestDto requestDto;
    private image exampleImage;

    @BeforeEach
    public void setUp() {
        requestDto = new tokenRequestDto("example_text");
        exampleImage = new image("1", "base64_encoded_image_data");

        List<image> images = Collections.singletonList(exampleImage);
        Page<image> imagePage = new PageImpl<>(images, PageRequest.of(0, 1), 1);

        Mockito.when(imageService.getImageResponse(anyString(), anyInt(), anyInt()))
                .thenReturn(Mono.just(imagePage));
    }

    @Test
    @WithMockUser
    public void testCreateImages() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("/images?page=0&size=1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), tokenRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].id").isEqualTo(exampleImage.getId())
                .jsonPath("$.content[0].data").isEqualTo(exampleImage.getData());
    }
}

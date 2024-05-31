package style_transfer.transfer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;
import style_transfer.transfer.repository.promptRequestDto.basicItem;
import style_transfer.transfer.service.ImageGenerationService;
import style_transfer.transfer.service.TokenValidationService;
import style_transfer.transfer.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@WebFluxTest(ImageGenerationController.class)
public class ImageGenerationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ImageGenerationService imageService;

    @MockBean
    private TokenValidationService tokenService;

    @MockBean
    private UserService userService;

    private promptRequestDto requestDto;
    private generatedImageResponseDto responseDto;

    @BeforeEach
    public void setUp() {
        basicItem item1 = new basicItem(1, "promptText1");
        basicItem item2 = new basicItem(2, "promptText2");
        List<basicItem> items = List.of(item1, item2);

        requestDto = new promptRequestDto("token", "projectId", "summary", "path", "base64Image", items);
        responseDto = new generatedImageResponseDto("projectId", "summary", "base64Image", Collections.emptyList(), LocalDateTime.now());

        Mockito.when(imageService.generateImages(any(promptRequestDto.class)))
                .thenReturn(Mono.just(responseDto));
        Mockito.when(tokenService.validateToken(anyString()))
                .thenReturn(true);
        Mockito.when(userService.saveProject(anyString(), any(generatedImageResponseDto.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    @WithMockUser
    public void testGenerateImagesValidToken() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("/generate-images")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), promptRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.projectId").isEqualTo(responseDto.getProjectId())
                .jsonPath("$.summary").isEqualTo(responseDto.getSummary())
                .jsonPath("$.exampleImage").isEqualTo(responseDto.getExampleImage());
    }

    @Test
    @WithMockUser
    public void testGenerateImagesInvalidToken() {
        Mockito.when(tokenService.validateToken(anyString()))
                .thenReturn(false);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("/generate-images")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), promptRequestDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("Invalid token");
    }

    @Test
    @WithMockUser
    public void testGenerateImagesWithoutTokenValidation() {
        basicItem item0 = new basicItem(0, "promptText0");
        List<basicItem> items = List.of(item0);

        promptRequestDto requestDtoWithoutValidation = new promptRequestDto("", "projectId", "summary", "path", "base64Image", items);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("/generate-images")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDtoWithoutValidation), promptRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.projectId").isEqualTo(responseDto.getProjectId())
                .jsonPath("$.exampleImage").isEqualTo(responseDto.getExampleImage());
    }
}

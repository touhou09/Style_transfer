package style_transfer.transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.controller.ImageGenerationController;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.basicItem;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;
import style_transfer.transfer.service.ImageGenerationService;
import style_transfer.transfer.service.TokenValidationService;
import style_transfer.transfer.service.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

public class ImageGenerationControllerTest {

    @InjectMocks
    private ImageGenerationController controller;

    @Mock
    private ImageGenerationService imageGenerationService;

    @Mock
    private TokenValidationService tokenValidationService;

    @Mock
    private UserService userService;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    public void testGenerateImagesWithValidToken() {
        // Arrange
        promptRequestDto requestDto = new promptRequestDto();
        requestDto.setToken("valid_token");
        requestDto.setBasicItems(Collections.singletonList(new basicItem(2, "example")));

        generatedImageResponseDto responseDto = new generatedImageResponseDto();
        given(tokenValidationService.validateToken(anyString())).willReturn(true);
        given(tokenValidationService.extractEmailFromToken(anyString())).willReturn("test@example.com");
        given(imageGenerationService.generateImages(any(promptRequestDto.class))).willReturn(Mono.just(responseDto));
        given(userService.addProjectToUser(anyString(), any(generatedImageResponseDto.class))).willReturn(Mono.just(new User()));

        // Act & Assert
        webTestClient.post()
                .uri("/api/generate-images")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(generatedImageResponseDto.class)
                .isEqualTo(responseDto);
    }

    @Test
    public void testGenerateImagesWithInvalidToken() {
        // Arrange
        promptRequestDto requestDto = new promptRequestDto();
        requestDto.setToken("invalid_token");
        requestDto.setBasicItems(Collections.singletonList(new basicItem(2, "example")));

        given(tokenValidationService.validateToken(anyString())).willReturn(false);

        // Act & Assert
        webTestClient.post()
                .uri("/api/generate-images")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(requestDto))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid token");
    }

    @Test
    public void testGenerateImagesWithoutToken() {
        // Arrange
        promptRequestDto requestDto = new promptRequestDto();
        requestDto.setToken(null);
        requestDto.setBasicItems(Collections.singletonList(new basicItem(2, "example")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/generate-images")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(requestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Token not provided");
    }
}

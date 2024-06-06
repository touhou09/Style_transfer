package style_transfer.transfer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import style_transfer.transfer.SecurityConfig;
import style_transfer.transfer.service.exampleImageServe;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = exampleImageController.class)
@Import(SecurityConfig.class)  // SecurityConfig 클래스를 명시적으로 가져오기
public class exampleImageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private exampleImageServe imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // WebTestClient에 CSRF 비활성화를 적용하기 위한 설정
        this.webTestClient = webTestClient.mutateWith(csrf());
    }
/*
    @Test
    @WithMockUser // Spring Security 컨텍스트에 인증된 사용자를 추가
    void testHandleRequest() {
        exampleRequestDto requestDto = new exampleRequestDto();
        requestDto.setText("test");

        PageImpl<image> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        Mono<PageImpl<image>> pageMono = Mono.just(page);

        Mockito.when(imageService.getImageResponse(Mockito.any(ServerWebExchange.class), Mockito.eq("test"), Mockito.eq(0), Mockito.eq(10)))
                .thenReturn(pageMono);

        webTestClient
                .mutateWith(csrf())
                .post().uri("/api/images?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content").isEmpty();
    }*/
}

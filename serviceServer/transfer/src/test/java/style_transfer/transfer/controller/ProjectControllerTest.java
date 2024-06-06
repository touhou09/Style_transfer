package style_transfer.transfer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.service.ProjectService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest(controllers = ProjectController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(ProjectService.class)
class ProjectControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProjectService projectService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToController(new ProjectController(projectService))
                .configureClient()
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void testHandleRequest() {
        generatedImageResponseDto responseDto = new generatedImageResponseDto();
        responseDto.setProjectId("1234");
        responseDto.setTime(LocalDateTime.now());

        when(projectService.getProjects(any(Pageable.class)))
                .thenReturn(Flux.just(responseDto));
        when(projectService.countProjects()).thenReturn(Mono.just(1L));

        webTestClient.get()
                .uri("/api/projects?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].projectId").isEqualTo("1234")
                .consumeWith(document("get-projects"));
    }

    @Test
    void testGetUserProject() {
        generatedImageResponseDto responseDto = new generatedImageResponseDto();
        responseDto.setProjectId("1234");
        responseDto.setTime(LocalDateTime.now());

        when(projectService.getProjectById("1234")).thenReturn(Mono.just(responseDto));

        webTestClient.get()
                .uri("/api/project/1234")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.projectId").isEqualTo("1234")
                .consumeWith(document("get-project"));
    }
}

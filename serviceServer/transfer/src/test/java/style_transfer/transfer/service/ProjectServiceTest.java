package style_transfer.transfer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import style_transfer.transfer.repository.ProjectRepository;
import style_transfer.transfer.repository.generatedImageResponseDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProjectServiceTest {

    @MockBean
    private ProjectRepository projectRepository;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        projectService = new ProjectService(projectRepository);
    }

    @Test
    void testSaveProject() {
        generatedImageResponseDto responseDto = new generatedImageResponseDto();
        responseDto.setProjectId("1234");
        responseDto.setTime(LocalDateTime.now());

        when(projectRepository.findById("1234")).thenReturn(Mono.empty());
        when(projectRepository.save(any(generatedImageResponseDto.class))).thenReturn(Mono.just(responseDto));

        Mono<Void> result = projectService.saveProject("1234", responseDto);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testGetProjectById() {
        generatedImageResponseDto responseDto = new generatedImageResponseDto();
        responseDto.setProjectId("1234");
        responseDto.setTime(LocalDateTime.now());

        when(projectRepository.findByProjectId("1234")).thenReturn(Mono.just(responseDto));

        Mono<generatedImageResponseDto> result = projectService.getProjectById("1234");

        StepVerifier.create(result)
                .expectNext(responseDto)
                .verifyComplete();
    }

    @Test
    void testGetProjects() {
        generatedImageResponseDto responseDto = new generatedImageResponseDto();
        responseDto.setProjectId("1234");
        responseDto.setTime(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);

        when(projectRepository.findAllBy(pageable)).thenReturn(Flux.just(responseDto));

        Flux<generatedImageResponseDto> result = projectService.getProjects(pageable);

        StepVerifier.create(result)
                .expectNext(responseDto)
                .verifyComplete();
    }

    @Test
    void testCountProjects() {
        when(projectRepository.count()).thenReturn(Mono.just(1L));

        Mono<Long> result = projectService.countProjects();

        StepVerifier.create(result)
                .expectNext(1L)
                .verifyComplete();
    }
}
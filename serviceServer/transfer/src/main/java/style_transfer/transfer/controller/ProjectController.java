package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.service.ProjectService;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public Mono<ResponseEntity<PageImpl<generatedImageResponseDto>>> handleRequest(@RequestParam int page, @RequestParam int size) {
        // 페이지 요청 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 프로젝트 목록을 페이지네이션하여 가져오고, 전체 프로젝트 수와 함께 반환합니다.
        return projectService.getProjects(pageable)
                .collectList() // Flux를 List로 수집
                .zipWith(projectService.countProjects()) // 프로젝트 수와 함께 반환
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2())) // 페이지 구현 객체 생성
                .map(ResponseEntity::ok); // ResponseEntity로 반환
    }


    @GetMapping("/project/{projectId}")
    public Mono<ResponseEntity<generatedImageResponseDto>> getUserProject(@PathVariable String projectId) {
        return projectService.getProjectById(projectId)
                .map(ResponseEntity::ok) // 프로젝트가 존재하면 OK 응답
                .defaultIfEmpty(ResponseEntity.notFound().build()); // 존재하지 않으면 404 응답
    }

}
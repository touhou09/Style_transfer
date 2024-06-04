package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.ProjectRepository;
import style_transfer.transfer.repository.generatedImageResponseDto;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Mono<Void> saveProject(String projectId, generatedImageResponseDto response) {
        return projectRepository.findById(projectId)
                .flatMap(project -> {
                    project.getGeneratedItems().addAll(response.getGeneratedItems());
                    return projectRepository.save(project);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    response.setProjectId(projectId);
                    return projectRepository.save(response);
                }))
                .then();
    }

    // 특정 프로젝트 ID로 프로젝트를 조회하는 메서드
    public Mono<generatedImageResponseDto> getProjectById(String projectId) {
        return projectRepository.findByProjectId(projectId);
    }

    // 페이지네이션을 사용하여 프로젝트 목록을 조회하는 메서드
    public Flux<generatedImageResponseDto> getProjects(Pageable pageable) {
        return projectRepository.findAllBy(pageable);
    }

    // 전체 프로젝트 수를 반환하는 메서드
    public Mono<Long> countProjects() {
        return projectRepository.count();
    }


}

package style_transfer.transfer.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectRepository extends ReactiveMongoRepository<generatedImageResponseDto, String> {
    Mono<generatedImageResponseDto> findByProjectId(String projectId);
    Flux<generatedImageResponseDto> findAllBy(Pageable pageable);
}

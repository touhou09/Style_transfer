package style_transfer.transfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImageGenerationService {

    private final WebClient webClient;
    private final ProjectService projectService;

    private static final Logger log = LoggerFactory.getLogger(ImageGenerationService.class);

    @Autowired
    public ImageGenerationService(WebClient.Builder webClientBuilder, ProjectService projectService, @Value("${fastapi.url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .build();
        this.projectService = projectService;
    }

        public Mono<generatedImageResponseDto> generateImages(promptRequestDto request) {
            if (request.getProjectId() == null || request.getProjectId().isEmpty()) {
                String projectId = UUID.randomUUID().toString();
                request.setProjectId(projectId);
            }

            return webClient.post()
                    .uri("/generate-images")
                    .body(Mono.just(request), promptRequestDto.class)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("API call failed: " + errorBody))))
                    .bodyToMono(generatedImageResponseDto.class)
                    .flatMap(response -> {
                        response.setTime(LocalDateTime.now());
                        response.setProjectId(request.getProjectId());
                        return projectService.saveProject(request.getProjectId(), response)
                                .thenReturn(response);
                    })
                    .doOnError(error -> log.error("Error: ", error));
        }
    }

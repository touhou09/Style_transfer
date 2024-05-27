package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.UserRepository;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;

@Service
public class ImageGenerationService {

    private final WebClient webClient;
    private final UserRepository userRepository;

    @Autowired
    public ImageGenerationService(WebClient.Builder webClientBuilder, UserRepository userRepository) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
        this.userRepository = userRepository;
    }

    public Mono<generatedImageResponseDto> generateImages(promptRequestDto request) {
        // 외부 API로 POST 요청을 보내고 응답을 처리
        return webClient.post()
                .uri("/generate-images")
                .body(Mono.just(request), promptRequestDto.class) // 직접 받은 promptRequestDto를 사용
                .retrieve()
                .bodyToMono(generatedImageResponseDto.class)
                .flatMap(response -> {
                    // 응답을 DB에 저장
                    User user = userRepository.findByToken(request.getToken()).block();
                    if (user != null) {
                        user.getProjects().add(response);
                        userRepository.save(user);
                    }
                    return Mono.just(response);
                })
                .onErrorMap(WebClientResponseException.class, ex -> new Exception("API call failed: " + ex.getMessage()));
    }
}
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ImageGenerationService {


    private final WebClient webClient;
    private final UserRepository userRepository;
    private final UserService userService; // UserService 주입

    @Autowired
    public ImageGenerationService(WebClient.Builder webClientBuilder, UserRepository userRepository, UserService userService) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /*
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
    */
    /*
    public Mono<generatedImageResponseDto> generateImages(promptRequestDto request) {
        // 외부 API로 POST 요청을 보내고 응답을 처리
        return webClient.post()
                .uri("/generate-images")
                .body(Mono.just(request), promptRequestDto.class) // 직접 받은 promptRequestDto를 사용
                .retrieve()
                .bodyToMono(generatedImageResponseDto.class)
                .flatMap(response -> {
                    // 현재 서버 시간을 설정
                    response.setTime(LocalDateTime.now());
                    // 응답을 DB에 저장
                    User user = userRepository.findByToken(request.getToken()).block();
                    if (user != null) {
                        user.getProjects().add(response);
                        userRepository.save(user).block(); // Mono 반환 후 즉시 완료
                    }
                    return Mono.just(response);
                })
                .onErrorMap(WebClientResponseException.class, ex -> new Exception("API call failed: " + ex.getMessage()));
    }
    */

    public Mono<generatedImageResponseDto> generateImages(promptRequestDto request) {
        // projectId가 없다면 생성 및 설정
        if (request.getProjectId() == null || request.getProjectId().isEmpty()) {
            String projectId = UUID.randomUUID().toString();
            request.setProjectId(projectId);
        }

        // 외부 API로 POST 요청을 보내고 응답을 처리
        return webClient.post()
                .uri("/generate-images")
                .body(Mono.just(request), promptRequestDto.class) // 직접 받은 promptRequestDto를 사용
                .retrieve()
                .bodyToMono(generatedImageResponseDto.class)
                .flatMap(response -> {
                    // 현재 서버 시간을 설정
                    response.setTime(LocalDateTime.now());
                    response.setProjectId(request.getProjectId()); // 응답에도 projectId 설정
                    // 이메일 기반으로 사용자 찾기 및 프로젝트 저장
                    return userService.getEmailFromToken(request.getToken()) // UserService 사용
                            .flatMap(email -> userRepository.findByEmail(email)
                                    .flatMap(user -> {
                                        user.getProjects().add(response);
                                        return userRepository.save(user);
                                    })
                                    .switchIfEmpty(Mono.defer(() -> {
                                                User newUser = User.builder()
                                                        .email(email)
                                                        .projects(List.of(response))
                                                        .build();
                                                return userRepository.save(newUser);
                                            })
                                    ))
                            .thenReturn(response);
                })
                .onErrorMap(WebClientResponseException.class, ex -> new Exception("API call failed: " + ex.getMessage()));
    }
}
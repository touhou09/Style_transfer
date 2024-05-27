package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.UserRepository;
import style_transfer.transfer.repository.generatedImageResponseDto;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private final WebClient webClient;
    private final UserRepository userRepository;

    public UserService(WebClient.Builder webClientBuilder, UserRepository userRepository) {
        this.webClient = webClientBuilder.baseUrl("https://oauth2.googleapis.com").build();
        this.userRepository = userRepository;
    }

    public String authenticateWithGoogle(String code) {
        return webClient.post()
                .uri("/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("code", code)
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", redirectUri)
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(Map.class)
                .<String>handle((responseBody, sink) -> {
                    if (responseBody.containsKey("access_token")) {
                        sink.next((String) responseBody.get("access_token"));
                    } else {
                        sink.error(new RuntimeException("Failed to authenticate with Google"));
                    }
                })
                .block();  // 비동기 호출을 동기식으로 변환
    }

    public Mono<Void> saveProject(String token, generatedImageResponseDto project) {
        return userRepository.findByToken(token)
                .flatMap(user -> {
                    user.getProjects().add(project);
                    return userRepository.save(user);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    User newUser = User.builder()
                            .token(token)
                            .projects(List.of(project))
                            .build();
                    return userRepository.save(newUser);
                }))
                .then();
    }

}
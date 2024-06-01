package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.UserInfo;
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

    public Mono<String> authenticateWithGoogle(String code) {
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
                .flatMap(responseBody -> {
                    if (responseBody.containsKey("access_token")) {
                        String accessToken = (String) responseBody.get("access_token");
                        return Mono.just(accessToken);
                    } else {
                        return Mono.error(new RuntimeException("Failed to authenticate with Google"));
                    }
                });
    }

    public Mono<String> getEmailFromToken(String token) {
        return webClient.get()
                .uri("/oauth2/v1/userinfo?alt=json")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(userInfo -> {
                    if (userInfo.containsKey("email")) {
                        return Mono.just((String) userInfo.get("email"));
                    } else {
                        return Mono.error(new RuntimeException("Failed to retrieve email from token"));
                    }
                });
    }

    public Mono<Void> saveProject(String token, generatedImageResponseDto response) {
        return  getEmailFromToken(token)
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
                        }))
                ).then();
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<List<generatedImageResponseDto>> getProjectsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getProjects);
    }

    public Mono<generatedImageResponseDto> getProjectByEmailAndProjectId(String email, String projectId) {
        return userRepository.findByEmail(email)
                .flatMap(user -> {
                    return Mono.justOrEmpty(
                            user.getProjects().stream()
                                    .filter(project -> project.getProjectId().equals(projectId))
                                    .findFirst()
                    );
                });
    }

    private UserInfo toDto(User user) {
        return new UserInfo(user.getId(), user.getEmail(), user.getName(), user.getProfileImage());
    }

    public Mono<UserInfo> getUserInfo(String token) {
        return getEmailFromToken(token)
                .flatMap(email -> userRepository.findByEmail(email)
                        .map(this::toDto)
                        .switchIfEmpty(Mono.error(new RuntimeException("User not found"))));
    }

}

package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.service.TokenValidationService;
import style_transfer.transfer.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${google.client-id}")
    private String clientId;
    @Value("${google.client-secret}")
    private String clientSecret;
    @Value("${google.callback-url}")
    private String callbackUrl;

    @Autowired
    private UserService userService;
    @Autowired
    private TokenValidationService tokenValidationService;

    @PostMapping("/google")
    public ResponseEntity<Map<String, String>> authenticateWithGoogle(@RequestParam String code) {
        String accessToken = userService.authenticateWithGoogle(code);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public Mono<ResponseEntity<User>> getUserInfo(@RequestHeader("Authorization") String token) {
        return userService.getUserByToken(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/projects")
    public Mono<ResponseEntity<List<generatedImageResponseDto>>> getUserProjects(@RequestHeader("Authorization") String token) {
        return userService.getProjectsByToken(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
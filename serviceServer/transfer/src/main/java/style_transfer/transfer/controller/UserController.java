package style_transfer.transfer.controller;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.service.TokenValidationService;
import style_transfer.transfer.service.UserService;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback(callbackUrl)
                .defaultScope("profile email") // scope 설정
                .build(GoogleApi20.instance());
        String authorizationUrl = service.getAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) throws IOException, ExecutionException, InterruptedException {
        OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback(callbackUrl)
                .defaultScope("profile email") // scope 설정
                .build(GoogleApi20.instance());
        OAuth2AccessToken accessToken;
        try {
            accessToken = service.getAccessToken(code);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error retrieving access token: " + e.getMessage());
        }

        String token = accessToken.getAccessToken();
        boolean isValid = tokenValidationService.validateToken(token);

        if (isValid) {
            String userInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + token;
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> userInfo = restTemplate.getForObject(userInfoUrl, Map.class);

            String email = userInfo.get("email");
            String name = userInfo.get("name");

            User existingUser = userService.getUserByEmail(email).block();
            if (existingUser == null) {
                existingUser = User.builder()
                        .email(email)
                        .name(name)
                        .projects(Collections.emptyList()) // 프로젝트 리스트 초기화
                        .build();
            } else {
                existingUser.setName(name);
            }

            existingUser.setToken(token);
            userService.saveOrUpdateUser(existingUser);

            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid Token");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        User existingUser = userService.getUserByEmail(user.getEmail()).block();
        if (existingUser != null) {
            existingUser.setName(user.getName());
            if (user.getProjects() != null) {
                existingUser.setProjects(user.getProjects());
            }
            userService.saveOrUpdateUser(existingUser);
            return ResponseEntity.ok("User updated successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
}

package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import style_transfer.transfer.service.TokenValidationService;
import style_transfer.transfer.service.UserService;

import java.util.HashMap;
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

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> authenticateWithGoogle(@RequestParam String code) {
        String accessToken = userService.authenticateWithGoogle(code);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        return ResponseEntity.ok(response);
    }

}
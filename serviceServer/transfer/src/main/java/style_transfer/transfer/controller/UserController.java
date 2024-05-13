package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.UserRepository;
import style_transfer.transfer.repository.generatedImageResponseDto;

import java.util.ArrayList;
import java.util.List;

@Controller

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/signup")
    public String signup(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        if (userRepository.findByEmail(email) == null) {
            String name = principal.getAttribute("name");
            List<generatedImageResponseDto> initialProjects = new ArrayList<>();  // 초기 프로젝트 목록 생성
            User user = new User(null, email, name, initialProjects);  // 프로젝트 목록 포함하여 사용자 객체 생성
            userRepository.save(user);
        }
        return "redirect:/user-profile";
    }

    @GetMapping("/user-profile")
    public ResponseEntity<User> userProfile(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
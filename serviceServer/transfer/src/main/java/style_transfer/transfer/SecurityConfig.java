package style_transfer.transfer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/login", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // 사용자 지정 로그인 페이지
                        .defaultSuccessUrl("/home", true)  // 로그인 성공 후 리다이렉트 페이지
                );

        return http.build();
    }
}
/*
authorizeRequests(): HTTP 요청에 대한 보안을 설정합니다. 특정 요청 패턴을 지정하고, 이에 대한 접근 제어를 설정할 수 있습니다.
    .requestMatchers("/").permitAll(): 루트 URL(/)에 대한 요청은 인증 없이 허용됩니다.
    .requestMatchers("/login", "/error").permitAll(): /login, /error 경로에 대한 요청도 인증 없이 접근할 수 있습니다.
    .anyRequest().authenticated(): 위에서 명시하지 않은 모든 요청은 인증을 요구합니다.

oauth2Login(): OAuth2 로그인 메커니즘을 설정합니다.
    .loginPage("/login"): 사용자 정의 로그인 페이지의 URL을 설정합니다. OAuth2 로그인 프로세스를 시작하는 페이지로 사용자가 이 페이지로 리다이렉트됩니다.
    .defaultSuccessUrl("/home", true): 로그인 성공 후 사용자를 리다이렉트할 기본 URL을 설정합니다. true는 성공적인 로그인 이후에 항상 이 URL로 리다이렉트되어야 함을 의미합니다.
 */
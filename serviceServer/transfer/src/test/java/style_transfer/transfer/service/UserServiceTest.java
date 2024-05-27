package style_transfer.transfer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.UserRepository;

import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private UserRepository userRepository;

    private WebClient webClient;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        webClient = mock(WebClient.class, Mockito.RETURNS_DEEP_STUBS);
        given(webClientBuilder.baseUrl(ArgumentMatchers.anyString())).willReturn(webClientBuilder);
        given(webClientBuilder.build()).willReturn(webClient);
        userService = new UserService(webClientBuilder, userRepository);
    }

    @Test
    public void testAuthenticateWithGoogle() {
        String code = "test_code";
        String accessToken = "test_access_token";
        Map<String, String> response = new HashMap<>();
        response.put("access_token", accessToken);

        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        given(webClient.post()).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.uri("/token")).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.header(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).willReturn(requestBodyUriSpec);
        given(requestBodyUriSpec.body(ArgumentMatchers.any(BodyInserters.FormInserter.class))).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(ArgumentMatchers.eq(Map.class))).willReturn(Mono.just(response));

        String result = userService.authenticateWithGoogle(code);

        assertEquals(accessToken, result);
    }

    // Additional test for saveProject method can be added here
}

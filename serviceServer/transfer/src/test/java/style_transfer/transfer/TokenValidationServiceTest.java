package style_transfer.transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import style_transfer.transfer.service.TokenValidationService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class TokenValidationServiceTest {

    @InjectMocks
    private TokenValidationService tokenValidationService;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateToken() {
        // Arrange
        String token = "valid_token";
        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("aud", "client_id");

        given(restTemplate.getForObject(anyString(), any(Class.class))).willReturn(tokenInfo);

        // Act
        boolean isValid = tokenValidationService.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void testExtractEmailFromToken() {
        // Arrange
        String token = "valid_token";
        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("email", "test@example.com");

        given(restTemplate.getForObject(anyString(), any(Class.class))).willReturn(tokenInfo);

        // Act
        String email = tokenValidationService.extractEmailFromToken(token);

        // Assert
        assertEquals("test@example.com", email);
    }
}

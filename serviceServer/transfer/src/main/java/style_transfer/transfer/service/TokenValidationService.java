package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.util.Map;

@Service
public class TokenValidationService {

    @Value("${google.clientId}")
    private String clientId;

    private final RestTemplate restTemplate;

    public TokenValidationService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean validateToken(String token) {
        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + token;
            Map tokenInfo = restTemplate.getForObject(url, Map.class);

            return tokenInfo != null && this.clientId.equals(tokenInfo.get("aud"));
        } catch (HttpClientErrorException e) {
            // 오류 처리, 토큰이 유효하지 않은 경우
            return false;
        }
    }

    public String extractEmailFromToken(String token) {
        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + token;
            Map tokenInfo = restTemplate.getForObject(url, Map.class);
            return tokenInfo != null ? (String) tokenInfo.get("email") : null;
        } catch (HttpClientErrorException e) {
            // 오류 처리, 토큰에서 이메일 추출 실패
            return null;
        }
    }
}
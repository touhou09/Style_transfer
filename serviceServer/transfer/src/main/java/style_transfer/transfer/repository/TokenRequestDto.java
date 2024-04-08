package style_transfer.transfer.repository;
import lombok.Getter;
import lombok.Setter;
/**
 * 최초 요청에서 요청하는 데이터 dto
 */
@Getter
@Setter
public class TokenRequestDto {
    private String token;
    private String text;
}

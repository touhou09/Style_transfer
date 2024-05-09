package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 예시 이미지 생성시 보내는 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class tokenRequestDto {
    // private String token;
    private String text;
}

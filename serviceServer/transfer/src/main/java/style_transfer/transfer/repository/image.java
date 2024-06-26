package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 예시 이미지 model
@Data
@NoArgsConstructor
@AllArgsConstructor
public class image {
    private String id;
    private String data; // Base64 인코딩된 이미지 데이터
}

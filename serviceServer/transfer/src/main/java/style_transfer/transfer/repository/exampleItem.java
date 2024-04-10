package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 프롬프트와 예시 이미지를 묶는 model
@Data
@NoArgsConstructor
@AllArgsConstructor
public class exampleItem {
    private String id;
    private String promptText;
    private String exampleImage; // Base64 인코딩된 이미지 데이터
}

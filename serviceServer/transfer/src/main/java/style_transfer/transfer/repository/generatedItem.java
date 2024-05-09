package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 프롬프트와 생성된 이미지 전달 model
@Data
@NoArgsConstructor
@AllArgsConstructor
public class generatedItem {
    private String index;
    private String promptText;
    private String generatedImage; // Base64 인코딩된 이미지 데이터
}

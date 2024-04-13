package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class generatedItem {
    private String id;
    private String promptText;
    private String generatedImage; // Base64 인코딩된 이미지 데이터
}
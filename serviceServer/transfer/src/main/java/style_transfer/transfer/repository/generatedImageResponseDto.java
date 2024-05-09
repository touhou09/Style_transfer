package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 프롬프트와 생성된 이미지 전달 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class generatedImageResponseDto {
    private String projectId;
    private List<generatedItem> generatedItems;
}

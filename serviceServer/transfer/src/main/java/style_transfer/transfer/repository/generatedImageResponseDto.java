package style_transfer.transfer.repository;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

// 프롬프트와 생성된 이미지 전달 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class generatedImageResponseDto {
    private String projectId;
    private String exampleImage;
    private List<generatedItem> generatedItems;

    @Setter
    @Getter
    private LocalDateTime time;

}

package style_transfer.transfer.repository;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "projects")
public class generatedImageResponseDto {
    @Id // db에서는 "_id" : "value" 구조로 저장됨
    private String projectId;

    private List<generatedItem> generatedItems;
    private LocalDateTime time;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class generatedItem {
        private int index;
        private String promptText;
        private String generatedImage; // Base64 인코딩된 이미지 데이터
    }
}

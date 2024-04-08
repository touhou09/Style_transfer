package style_transfer.transfer.repository;
import lombok.*;

import java.util.List;

/**
 * 예시 이미지와 프롬프트를 함께 fastAPI에 전달하는 dto
 */
@Getter
@Setter
public class PromptImageRequestDto {
    private String id; // 프롬프트 입력 순번
    private List<PromptItem> items; // 텍스트와 이미지 묶음

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromptItem {
        private String promptText; // 프롬프트 텍스트
        private String exampleImage; // 예시 이미지(Base64 인코딩된 데이터)
    }
}

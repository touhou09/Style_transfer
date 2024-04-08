package style_transfer.transfer.repository;
import lombok.*;

import java.util.List;

/**
 * fastAPI에서 생성한 이미지와 프롬프트를 전달받는 dto
 */
@Getter
@Setter
public class PromptImageGenerateDto {
    private String id; // 프롬프트 입력 순번
    private List<PromptImageRequestDto.PromptItem> items; // 텍스트와 이미지 묶음

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromptItem {
        private String promptText; // 프롬프트 텍스트
        private String generatedImage; // 예시 이미지(Base64 인코딩된 데이터)
    }

}

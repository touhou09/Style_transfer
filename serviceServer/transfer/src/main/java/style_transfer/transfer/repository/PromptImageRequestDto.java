package style_transfer.transfer.repository;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 예시 이미지와 프롬프트를 함께 fastAPI에 전달하는 dto
 */
@Getter
@Setter
public class PromptImageRequestDto {
    private String id; // 프롬프트 입력 순번
    private List<AbstractPromptItem> items; // 추상화된 텍스트와 이미지 묶음

    public abstract static class AbstractPromptItem {
        private String promptText; // 프롬프트 텍스트

        // 추상 메서드로 이미지 데이터에 대한 접근자를 정의
        public abstract String getImageData();
        public abstract void setImageData(String imageData);
    }
}

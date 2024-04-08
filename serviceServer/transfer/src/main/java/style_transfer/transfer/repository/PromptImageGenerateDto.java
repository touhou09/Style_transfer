package style_transfer.transfer.repository;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * fastAPI에서 생성한 이미지와 프롬프트를 전달받는 dto
 */
@Getter
@Setter
public class PromptImageGenerateDto extends PromptImageRequestDto.AbstractPromptItem {

    @Setter
    private String generatedImage;

    @Override
    public String getImageData() {
        return generatedImage;
    }

    @Override
    public void setImageData(String imageData) {
        this.generatedImage = imageData;
    }

}

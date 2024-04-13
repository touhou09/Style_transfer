package style_transfer.transfer.service;

import org.springframework.stereotype.Service;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.generatedItem;
import style_transfer.transfer.repository.promptRequestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageGenerationService {

    public generatedImageResponseDto generateImages(promptRequestDto request) {
        List<generatedItem> items = new ArrayList<>();
        request.getExampleItems().forEach(item -> {
            generatedItem generated = new generatedItem(
                    item.getId(), // 사용자로부터 받은 id를 그대로 사용
                    item.getPromptText(),
                    "Base64_Encoded_Generated_Image_Data_For_" + item.getPromptText()
            );
            items.add(generated);
        });

        return new generatedImageResponseDto(request.getProjectId(), items);
    }

}

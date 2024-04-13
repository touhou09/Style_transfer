package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;
import style_transfer.transfer.service.ImageGenerationService;

@RestController
public class ImageGenerationController {

    private final ImageGenerationService imageService;

    @Autowired
    public ImageGenerationController(ImageGenerationService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/api/generate-images")
    public generatedImageResponseDto generateImages(@RequestBody promptRequestDto request) {
        return imageService.generateImages(request);
    }
}

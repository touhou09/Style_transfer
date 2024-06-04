package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.promptRequestDto;
import style_transfer.transfer.service.ImageGenerationService;
import style_transfer.transfer.service.ProjectService;

@RestController
@RequestMapping("/api")
public class ImageGenerationController {

    private final ImageGenerationService imageService;
    private final ProjectService projectService;

    @Autowired
    public ImageGenerationController(ImageGenerationService imageService, ProjectService projectService) {
        this.imageService = imageService;
        this.projectService = projectService;
    }

    @PostMapping("/generate-images")
    public Mono<ResponseEntity<generatedImageResponseDto>> generateImages(@RequestBody promptRequestDto request) {
        return imageService.generateImages(request)
                .map(ResponseEntity::ok);
    }
}

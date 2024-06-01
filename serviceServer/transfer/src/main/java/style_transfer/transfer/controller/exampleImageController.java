package style_transfer.transfer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.image;
import style_transfer.transfer.repository.tokenRequestDto;
import style_transfer.transfer.service.exampleImageServe;

import java.util.Collections;

@RestController
@RequestMapping("/api")
public class exampleImageController {
    private final exampleImageServe imageService;

    private static final Logger logger = LoggerFactory.getLogger(exampleImageController.class);

    @Autowired
    public exampleImageController(exampleImageServe imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/images")
    public Mono<ResponseEntity<PageImpl<image>>> handleRequest(@RequestBody tokenRequestDto requestDto,
                                                               @RequestParam int page,
                                                               @RequestParam int size) {
        return imageService.getImageResponse(requestDto.getText(), page, size)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Error processing request to /images", e);

                    // 빈 페이지를 생성하여 반환
                    PageImpl<image> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
                    return Mono.just(ResponseEntity.ok(emptyPage));
                });
    }
}

package style_transfer.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.image;
import style_transfer.transfer.repository.tokenRequestDto;
import style_transfer.transfer.service.exampleImageServe;

@RestController
public class exampleImageController {
    private final exampleImageServe imageService;

    @Autowired
    public exampleImageController(exampleImageServe imageService) {
        this.imageService = imageService;
    }

    public Mono<ResponseEntity<Page<image>>> createImages(@RequestBody tokenRequestDto requestDto,
                                                          @RequestParam int page,
                                                          @RequestParam int size) {
        return imageService.getImageResponse(requestDto.getText(), page, size)
                .map(pageImages -> ResponseEntity.ok().body(pageImages))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

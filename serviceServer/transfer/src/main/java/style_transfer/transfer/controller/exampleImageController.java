package style_transfer.transfer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.exampleRequestDto;
import style_transfer.transfer.service.exampleImageServe;

import java.awt.*;
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
    public Mono<ResponseEntity<? extends PageImpl<? extends Object>>> handleRequest(@RequestBody exampleRequestDto requestDto,
                                                                                        @RequestParam int page,
                                                                                        @RequestParam int size,
                                                                                        ServerWebExchange exchange) {
            return imageService.getImageResponse(exchange, requestDto.getText(), page, size)
                    .doOnNext(response -> logger.info("Received response: {}", response))
                    .map(response -> {
                        if (response != null && !response.isEmpty()) {
                            return ResponseEntity.ok(response);
                        } else {
                            logger.warn("Received empty response from imageService");
                            PageImpl<Image> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
                            return ResponseEntity.ok(emptyPage);
                        }
                    });
        }

/*

    @PostMapping("/images")
    public Mono<ResponseEntity<? extends PageImpl<? extends Object>>> handleRequest(@RequestBody exampleRequestDto requestDto,
                                                                                    @RequestParam int page,
                                                                                    @RequestParam int size,
                                                                                    HttpServletRequest request) {
        return imageService.getImageResponse((ServerWebExchange) request, requestDto.getText(), page, size)
                .doOnNext(response -> logger.info("Received response: {}", response))
                .map(response -> {
                    if (response != null && !response.isEmpty()) {
                        return ResponseEntity.ok(response);
                    } else {
                        logger.warn("Received empty response from imageService");
                        PageImpl<Image> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
                        return ResponseEntity.ok(emptyPage);
                    }
                });
    }
*/

}

package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.repository.generatedItem;
import style_transfer.transfer.repository.promptRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageGenerationService {

    private final WebClient webClient;

    @Autowired
    public ImageGenerationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
    }

    public Mono<generatedImageResponseDto> generateImages(promptRequestDto request) {
        // 입력받은 ExampleItems를 기반으로 GeneratedItem 목록을 생성
        List<generatedItem> generatedItems = request.getExampleItems().stream()
                .map(item -> new generatedItem(
                        item.getIndex(), // 'id' 대신 'index' 필드를 사용
                        item.getPromptText(),
                        request.getExampleImage())) // 요청에서 받은 공통 이미지 데이터 사용
                .collect(Collectors.toList());

        // POST 요청을 보내기 위해 새로운 GeneratedImageResponseDto를 생성
        generatedImageResponseDto responseDto = new generatedImageResponseDto(request.getProjectId(), generatedItems);

        // 외부 API로 요청을 보내고 Mono<GeneratedImageResponseDto> 형태로 응답 받음
        return webClient.post()
                .uri("/generate-images")
                .body(Mono.just(responseDto), generatedImageResponseDto.class)
                .retrieve()
                .bodyToMono(generatedImageResponseDto.class);
    }
}

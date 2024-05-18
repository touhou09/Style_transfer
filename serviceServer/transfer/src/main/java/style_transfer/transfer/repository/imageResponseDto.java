package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

// 예시 이미지 가져오는 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class imageResponseDto {
    private String summary;
    private Page<image> images;
}

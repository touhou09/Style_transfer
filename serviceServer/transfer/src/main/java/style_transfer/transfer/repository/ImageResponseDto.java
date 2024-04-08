package style_transfer.transfer.repository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 최초 요청에서 반환하는 데이터 dto
 */
@Data
public class ImageResponseDto {
    private String token;
    private List<Image> images;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        private String id;
        private String data; // Base64 인코딩된 이미지 데이터
    }
}

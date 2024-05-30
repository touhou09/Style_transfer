package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 프롬프트와 예시 이미지를 보내는 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class promptRequestDto {
    private String token;
    private String projectId; // 프로젝트 ID
    private String summary;
    private String id;
    private String exampleImage; // 해당 프로젝트와 관련된 공통 이미지 데이터 (Base64 인코딩된 이미지 데이터)
    private List<basicItem> basicItems; // 해당 프로젝트와 관련된 프롬프트 항목들

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class basicItem {
        private int index; // 각 항목의 고유 Index
        private String promptText; // 프롬프트 텍스트
    }
}

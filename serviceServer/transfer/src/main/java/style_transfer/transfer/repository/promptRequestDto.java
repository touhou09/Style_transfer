package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class promptRequestDto {
    private String projectId; // 프로젝트 ID
    private String exampleImage; // 해당 프로젝트와 관련된 공통 이미지 데이터 (Base64 인코딩된 이미지 데이터)
    private List<basicItem> basicItems; // 해당 프로젝트와 관련된 프롬프트 항목들
}

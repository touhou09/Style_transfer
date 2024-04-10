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
    private String projectId; // 프로젝트 ID
    private List<exampleItem> exampleItems; // 해당 프로젝트와 관련된 프롬프트 항목들
}

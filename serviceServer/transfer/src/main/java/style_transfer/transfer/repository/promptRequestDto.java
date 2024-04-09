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
    private List<exampleItem> exampleItems; // 해당 프로젝트와 관련된 프롬프트 항목들
}

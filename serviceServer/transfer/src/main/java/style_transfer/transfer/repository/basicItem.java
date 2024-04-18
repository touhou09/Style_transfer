package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class basicItem {
    private String index; // 각 항목의 고유 Index
    private String promptText; // 프롬프트 텍스트
}

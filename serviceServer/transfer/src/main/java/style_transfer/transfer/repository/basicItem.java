package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 프롬프트와 예시 이미지를 묶는 model
@Data
@NoArgsConstructor
@AllArgsConstructor
public class basicItem {
    private int index; // 각 항목의 고유 Index
    private String promptText; // 프롬프트 텍스트
}

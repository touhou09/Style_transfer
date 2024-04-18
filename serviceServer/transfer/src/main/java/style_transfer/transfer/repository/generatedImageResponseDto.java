package style_transfer.transfer.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class generatedImageResponseDto {
    private String projectId;
    private List<generatedItem> generatedItems;
}

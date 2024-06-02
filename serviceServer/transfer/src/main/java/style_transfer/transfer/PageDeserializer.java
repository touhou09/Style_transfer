package style_transfer.transfer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

public class PageDeserializer extends JsonDeserializer<PageImpl<?>> {

    @Override
    public PageImpl<?> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        JsonNode contentNode = node.get("content");
        JsonNode pageableNode = node.get("pageable");
        JsonNode totalNode = node.get("total");

        List<?> content = mapper.convertValue(contentNode, List.class);
        PageRequest pageable = mapper.convertValue(pageableNode, PageRequest.class);
        long total = totalNode.asLong();

        return new PageImpl<>(content, pageable, total);
    }
}

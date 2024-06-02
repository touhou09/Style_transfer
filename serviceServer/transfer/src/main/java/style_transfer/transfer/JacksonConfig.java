package style_transfer.transfer;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule customPageModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(PageImpl.class, new PageDeserializer());
        return module;
    }
}

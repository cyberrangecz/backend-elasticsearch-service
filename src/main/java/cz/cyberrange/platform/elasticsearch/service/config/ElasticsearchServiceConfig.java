package cz.cyberrange.platform.elasticsearch.service.config;

import cz.cyberrange.platform.elasticsearch.data.config.ElasticsearchDataConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ElasticsearchDataConfig.class})
public class ElasticsearchServiceConfig {

}

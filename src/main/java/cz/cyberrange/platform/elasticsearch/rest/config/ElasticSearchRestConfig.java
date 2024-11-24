package cz.cyberrange.platform.elasticsearch.rest.config;

import cz.cyberrange.platform.elasticsearch.service.config.ElasticsearchServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ElasticsearchServiceConfig.class})
public class ElasticSearchRestConfig {
}

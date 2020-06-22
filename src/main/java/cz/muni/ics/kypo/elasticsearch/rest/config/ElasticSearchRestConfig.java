package cz.muni.ics.kypo.elasticsearch.rest.config;

import cz.muni.ics.kypo.elasticsearch.service.config.ElasticsearchServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ElasticsearchServiceConfig.class})
public class ElasticSearchRestConfig {
}

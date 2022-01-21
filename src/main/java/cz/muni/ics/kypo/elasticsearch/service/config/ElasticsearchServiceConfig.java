package cz.muni.ics.kypo.elasticsearch.service.config;

import cz.muni.ics.kypo.elasticsearch.data.config.ElasticsearchDataConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ElasticsearchDataConfig.class})
public class ElasticsearchServiceConfig {

}

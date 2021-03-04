package cz.muni.ics.kypo.elasticsearch.service.config;

import cz.muni.ics.kypo.commons.security.config.ResourceServerSecurityConfig;
import cz.muni.ics.kypo.commons.startup.config.MicroserviceRegistrationConfiguration;
import cz.muni.ics.kypo.elasticsearch.data.config.ElasticsearchDataConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ElasticsearchDataConfig.class, ResourceServerSecurityConfig.class})
public class ElasticsearchServiceConfig {

}

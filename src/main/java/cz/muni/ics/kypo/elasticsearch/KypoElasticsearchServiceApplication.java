package cz.muni.ics.kypo.elasticsearch;

import cz.muni.ics.kypo.commons.startup.config.MicroserviceRegistrationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = MicroserviceRegistrationConfiguration.class)
public class KypoElasticsearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KypoElasticsearchServiceApplication.class, args);
    }

}

package org.gamedo.demo.configuration;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

@Configuration
public class AppConfig {

    @Bean
    public MongoTemplate mongoTemplate(final MongoDatabaseFactory databaseFactory, final MongoConverter mongoConverter) {
        return new MongoTemplate(databaseFactory, mongoConverter);
    }

    @Bean
    InitializingBean forcePrometheusPostProcessor(BeanPostProcessor meterRegistryPostProcessor, PrometheusMeterRegistry registry) {
        return () -> meterRegistryPostProcessor.postProcessAfterInitialization(registry, "");
    }

//    @Bean
//    public GamedoMongoTemplate gamedoMongoTemplate(MongoTemplate mongoTemplate) {
//        return new GamedoMongoTemplate(mongoTemplate);
//    }

}

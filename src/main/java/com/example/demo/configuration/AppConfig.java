package com.example.demo.configuration;

import org.gamedo.persistence.GamedoMongoTemplate;
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
    public GamedoMongoTemplate gamedoMongoTemplate(MongoTemplate mongoTemplate) {
        return new GamedoMongoTemplate(mongoTemplate);
    }
}

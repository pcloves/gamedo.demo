package com.example.demo.persistence;

import org.gamedo.persistence.config.MongoConfiguration;
import org.gamedo.persistence.converter.AbstractEntityDbDataReadingConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class EntityDbPlayerReadingConverter extends AbstractEntityDbDataReadingConverter<EntityDbPlayer> {
    public EntityDbPlayerReadingConverter(MongoConfiguration configuration) {
        super(configuration);
    }
}

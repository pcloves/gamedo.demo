package com.example.demo.persistence;

import org.gamedo.persistence.config.MongoConfiguration;
import org.gamedo.persistence.converter.AbstractEntityDbDataWritingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class EntityDbPlayerWriterConverter extends AbstractEntityDbDataWritingConverter<EntityDbPlayer> {
    public EntityDbPlayerWriterConverter(MongoConfiguration configuration) {
        super(configuration);
    }
}
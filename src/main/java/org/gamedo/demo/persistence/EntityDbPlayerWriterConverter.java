package org.gamedo.demo.persistence;

import org.bson.Document;
import org.gamedo.demo.configuration.AppConfig;
import org.gamedo.persistence.config.MongoConfiguration;
import org.gamedo.persistence.converter.AbstractEntityDbDataWritingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class EntityDbPlayerWriterConverter extends AbstractEntityDbDataWritingConverter<EntityDbPlayer> {
    public EntityDbPlayerWriterConverter(MongoConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Document convert(EntityDbPlayer source) {
        final Document document = super.convert(source);

        if (document != null) {
            document.remove(DefaultMongoTypeMapper.DEFAULT_TYPE_KEY);
            document.forEach((key, nestedDocument) -> {
                if (nestedDocument instanceof Document) {
                    Document componentDbData = (Document) nestedDocument;
                    if (AppConfig.classMetaIgnoreMap.containsKey(key)) {
                        componentDbData.remove(DefaultMongoTypeMapper.DEFAULT_TYPE_KEY);
                    }
                }
            });
        }

        return document;
    }
}
package org.gamedo.demo.persistence;

import org.bson.Document;
import org.gamedo.demo.configuration.AppConfig;
import org.gamedo.persistence.config.MongoConfiguration;
import org.gamedo.persistence.converter.AbstractEntityDbDataReadingConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class EntityDbPlayerReadingConverter extends AbstractEntityDbDataReadingConverter<EntityDbPlayer> {

    public EntityDbPlayerReadingConverter(MongoConfiguration configuration) {
        super(configuration);
    }


    @Override
    public EntityDbPlayer convert(Document source) {

        source.put(DefaultMongoTypeMapper.DEFAULT_TYPE_KEY, EntityDbPlayer.class.getName());

        source.forEach((key, nestedDocument) -> {
            if (nestedDocument instanceof Document) {
                Document componentDbData = (Document) nestedDocument;
                if (AppConfig.classMetaIgnoreMap.containsKey(key)) {
                    componentDbData.put(DefaultMongoTypeMapper.DEFAULT_TYPE_KEY, AppConfig.classMetaIgnoreMap.get(key).getName());
                }
            }
        });

        return super.convert(source);
    }
}

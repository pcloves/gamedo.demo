package org.gamedo.demo.configuration;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.gamedo.demo.annotation.ClassMetaIgnore;
import org.gamedo.demo.persistence.ComponentDbPosition;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.util.AnnotatedTypeScanner;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    public static volatile Map<String, Class<?>> classMetaIgnoreMap = Collections.emptyMap();

    private final ApplicationContext applicationContext;

    public AppConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        scanClassMetaAnnotation();
    }

    private void scanClassMetaAnnotation() {

        final AnnotatedTypeScanner scanner = new AnnotatedTypeScanner(ClassMetaIgnore.class);
        scanner.setEnvironment(applicationContext.getEnvironment());
        scanner.setResourceLoader(applicationContext);

        classMetaIgnoreMap = scanner.findTypes(ComponentDbPosition.class.getPackageName()).stream()
                .collect(Collectors.toMap(Class::getSimpleName, Function.identity()));
    }

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

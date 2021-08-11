package org.gamedo.demo;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
//@EnableLoadTimeWeaving
public class Application {

    public static void main(String[] args) {

//        DynamicInstrumentationLoader.waitForInitialized(); //dynamically attach java agent to jvm if not already present
//        DynamicInstrumentationLoader.initLoadTimeWeavingContext(); //weave all classes before they are loaded as beans

        SpringApplication.run(Application.class, args);
    }
}

package com.acenexus.tata.configservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableConfigServer
public class ConfigserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigserviceApplication.class, args);
    }

    @Component
    static class EnvironmentReporter implements ApplicationRunner {

        private static final Logger logger = LoggerFactory.getLogger(EnvironmentReporter.class);

        @Autowired
        private Environment env;

        @Override
        public void run(ApplicationArguments args) {
            String[] activeProfiles = env.getActiveProfiles();
            String activeProfile = activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "default";
            logger.info("==================================================");
            logger.info("Current active environment: {}", activeProfile);
            logger.info("Server port: {}", env.getProperty("server.port"));
            logger.info("==================================================");
        }
    }

}

package com.msvcnotifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MsvcNotificationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsvcNotificationsApplication.class, args);
    }

}

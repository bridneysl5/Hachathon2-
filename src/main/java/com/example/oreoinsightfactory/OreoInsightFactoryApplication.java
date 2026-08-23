package com.example.oreoinsightfactory;

import jdk.jfr.Enabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OreoInsightFactoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(OreoInsightFactoryApplication.class, args);
    }

}

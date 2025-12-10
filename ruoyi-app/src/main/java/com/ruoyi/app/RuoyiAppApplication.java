package com.ruoyi.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.ruoyi", exclude = { DataSourceAutoConfiguration.class })
public class RuoyiAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuoyiAppApplication.class, args);
    }

}

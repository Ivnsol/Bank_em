package ru.minusd.security;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringSecurityApplication {

    public static void main(String[] args) {
        PropertyConfigurator.configure("C:\\Users\\Иван Александрович\\dev\\EM\\src\\main\\resources\\log4j.properties");
        SpringApplication.run(SpringSecurityApplication.class, args);
    }

}
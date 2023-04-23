package com.webfluxtdd;

import com.github.javafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringWebfluxTddApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxTddApplication.class, args);
    }

    @Bean
    public Faker faker() {
        return new Faker();
    }
}

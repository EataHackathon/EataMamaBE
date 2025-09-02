package com.eata.eatamamabe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EataMamaBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EataMamaBeApplication.class, args);
    }

}

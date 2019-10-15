package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository repository) {
        return (args) -> {
            repository.save(new Player("JackBauer", "Jack", "Bauer"));
            repository.save(new Player("Chloe O ", "Chloe", "O'Brian"));
            repository.save(new Player("KimB  ", "Kim", "Bauer"));
        };

    }
}

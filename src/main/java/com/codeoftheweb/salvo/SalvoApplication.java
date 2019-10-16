package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
        return (args) -> {
            Player jackBauer = new Player("j.bauer", "Jack", "Bauer", "j.bauer@ctu.gov");
            Player cObrian = new Player("c.obrian ", "Chloe", "O'Brian", "c.obrian@ctu.gov");
            Player kimBauer = new Player("kim_bauer", "Kim", "Bauer", "kim_bauer@ctu.gov");
            Player tony = new Player("t.almeida", "Tony", "Almeida", "t.almeida@ctu.gov");
            LocalDateTime fechaGame = LocalDateTime.of(2019, 10, 07, 01, 50);
            Game game1 = new Game(fechaGame);
            Game game2 = new Game(fechaGame.plusHours(1));
            Game game3 = new Game(fechaGame.plusHours(2));
            playerRepository.save(jackBauer);
            playerRepository.save(cObrian);
            playerRepository.save(kimBauer);
            playerRepository.save(tony);
            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            /*joinDate se crea inicializa sola cuando se crear un gamePleyer con LocalDateTime.now()*/
            gamePlayerRepository.save(new GamePlayer(jackBauer, game1));
            gamePlayerRepository.save(new GamePlayer(cObrian, game1));
            gamePlayerRepository.save(new GamePlayer(tony, game2));
            gamePlayerRepository.save(new GamePlayer(kimBauer, game2));
        };

    }
}

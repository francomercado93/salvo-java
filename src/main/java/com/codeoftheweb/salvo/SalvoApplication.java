package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
public class SalvoApplication {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData() {
        return (args) -> {

            /*Players*/
            Player jackBauer = new Player("j.bauer", "Jack", "Bauer", "j.bauer@ctu.gov");
            Player cObrian = new Player("c.obrian ", "Chloe", "O'Brian", "c.obrian@ctu.gov");
            Player kimBauer = new Player("kim_bauer", "Kim", "Bauer", "kim_bauer@ctu.gov");
            Player tony = new Player("t.almeida", "Tony", "Almeida", "t.almeida@ctu.gov");

            LocalDateTime fechaGame = LocalDateTime.of(2019, 10, 07, 01, 50);

            /*Games*/

            Game game1 = new Game(fechaGame);
            Game game2 = new Game(fechaGame.plusHours(1));
            Game game3 = new Game(fechaGame.plusHours(2));
            Game game4 = new Game(fechaGame.plusHours(3));
            Game game5 = new Game(fechaGame.plusHours(4));
            Game game6 = new Game(fechaGame.plusHours(5));
            Game game7 = new Game(fechaGame.plusHours(6));
            Game game8 = new Game(fechaGame.plusHours(7));

            playerRepository.save(jackBauer);
            playerRepository.save(cObrian);
            playerRepository.save(kimBauer);
            playerRepository.save(tony);
            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);
            gameRepository.save(game5);
            gameRepository.save(game6);
            gameRepository.save(game7);
            gameRepository.save(game8);

            /*Game players*/
            /*joinDate se crea inicializa sola cuando se crear un gamePlayer con LocalDateTime.now()*/
            GamePlayer gamePlayerJG1 = new GamePlayer(jackBauer, game1);
            GamePlayer gamePlayerCG1 = new GamePlayer(cObrian, game1);
            GamePlayer gamePlayerJG2 = new GamePlayer(jackBauer, game2);
            GamePlayer gamePlayerCG2 = new GamePlayer(cObrian, game2);
            GamePlayer gamePlayerCG3 = new GamePlayer(cObrian, game3);
            GamePlayer gamePlayerTG3 = new GamePlayer(tony, game3);
            GamePlayer gamePlayerCG4 = new GamePlayer(cObrian, game4);
            GamePlayer gamePlayerJG4 = new GamePlayer(jackBauer, game4);
            GamePlayer gamePlayerTG5 = new GamePlayer(tony, game5);
            GamePlayer gamePlayerJG5 = new GamePlayer(jackBauer, game5);
            GamePlayer gamePlayerKG6 = new GamePlayer(kimBauer, game6);
            GamePlayer gamePlayerTG8 = new GamePlayer(tony, game8);
            GamePlayer gamePlayerKG8 = new GamePlayer(kimBauer, game8);

            /* SHIPS */

            Ship carrierJGP1 = new Ship("Carrier");
            Ship submarineJGP1 = new Ship("Submarine");
            Ship patrolBoatJGP1 = new Ship("Patrol Boat");
            Ship destroyerCGP1 = new Ship("Destroyer");
            Ship patrolBoatCGP1 = new Ship("Patrol Boat");
            Ship destroyerJGP2 = new Ship("Destroyer");
            Ship patrolBoatJGP2 = new Ship("Patrol Boat");
            Ship submarineCGP2 = new Ship("Submarine");
            Ship patrolBoatCGP2 = new Ship("Patrol Boat");
            Ship destroyerCGP3 = new Ship("Destroyer");
            Ship patrolBoatCGP3 = new Ship("Patrol Boat");
            Ship submarineTGP3 = new Ship("Submarine");
            Ship patrolBoatTGP3 = new Ship("Patrol Boat");
            Ship destroyerCGP4 = new Ship("Destroyer");
            Ship patrolBeatCGP4 = new Ship("Patrol Boat");
            Ship submarineJGP4 = new Ship("Submarine");
            Ship patrolBoatJGP4 = new Ship("Patrol Boat");
            Ship destroyerTGP5 = new Ship("Submarine");
            Ship patrolBoatTGP5 = new Ship("Patrol Boat");
            Ship submarineJGP5 = new Ship("Submarine");
            Ship patrolBoatJGP5 = new Ship("Patrol Boat");
            Ship destroyerKGP6 = new Ship("Destroyer");
            Ship patrolBoatKGP6 = new Ship("Patrol Boat");
            Ship destroyerTGP8 = new Ship("Destroyer");
            Ship submarineTGP8 = new Ship("Submarine");
            Ship patrolBoatTGP8 = new Ship("Patrol Boat");
            Ship patrolBoatKGP8 = new Ship("Patrol Boat");

            /*LOCACIONES*/

            carrierJGP1.addLocations(Arrays.asList("H2", "H3", "H4"));
            submarineJGP1.addLocations(Arrays.asList("E1", "F1", "G1"));
            patrolBoatJGP1.addLocations(Arrays.asList("B4", "B5"));
            destroyerCGP1.addLocations(Arrays.asList("B5", "C5", "D5"));
            patrolBoatCGP1.addLocations(Arrays.asList("F1", "F2"));
            destroyerJGP2.addLocations(Arrays.asList("B5", "C5", "D5"));
            patrolBoatJGP2.addLocations(Arrays.asList("C6", "C7"));
            submarineCGP2.addLocations(Arrays.asList("A2", "A3", "A4"));
            patrolBoatCGP2.addLocations(Arrays.asList("G6", "H6"));
            destroyerCGP3.addLocations(Arrays.asList("B5", "C5", "D5"));
            patrolBoatCGP3.addLocations(Arrays.asList("C6", "C7"));
            submarineTGP3.addLocations(Arrays.asList("A2", "A3", "A4"));
            patrolBoatTGP3.addLocations(Arrays.asList("G6", "H6"));
            destroyerCGP4.addLocations(Arrays.asList("B5", "C5", "D5"));
            patrolBeatCGP4.addLocations(Arrays.asList("C6", "C7"));
            submarineJGP4.addLocations(Arrays.asList("A2", "A3", "A4"));
            patrolBoatJGP4.addLocations(Arrays.asList("G6", "H6"));
            destroyerTGP5.addLocations(Arrays.asList("B5", "C5", "D5"));
            patrolBoatTGP5.addLocations(Arrays.asList("G6", "H6"));
            submarineJGP5.addLocations(Arrays.asList("A2", "A3", "A4"));
            patrolBoatJGP5.addLocations(Arrays.asList("G6", "H6"));
            destroyerKGP6.addLocations(Arrays.asList("B5", "C5", "D5"));
            patrolBoatKGP6.addLocations(Arrays.asList("C6", "C7"));
            destroyerTGP8.addLocations(Arrays.asList("B5", "C5", "D5"));
            submarineTGP8.addLocations(Arrays.asList("A2", "A3", "A4"));
            patrolBoatTGP8.addLocations(Arrays.asList("G6", "H6"));
            patrolBoatKGP8.addLocations(Arrays.asList("C6", "C7"));

            gamePlayerJG1.addShip(carrierJGP1);
            gamePlayerJG1.addShip(submarineJGP1);
            gamePlayerJG1.addShip(patrolBoatJGP1);
            gamePlayerCG1.addShip(destroyerCGP1);
            gamePlayerCG1.addShip(patrolBoatCGP1);
            gamePlayerJG2.addShip(destroyerJGP2);
            gamePlayerJG2.addShip(patrolBoatJGP2);
            gamePlayerJG2.addShip(patrolBoatCGP2);
            gamePlayerCG2.addShip(submarineCGP2);
            gamePlayerCG3.addShip(destroyerCGP3);
            gamePlayerCG3.addShip(patrolBoatCGP3);
            gamePlayerTG3.addShip(submarineTGP3);
            gamePlayerTG3.addShip(patrolBoatTGP3);
            gamePlayerCG4.addShip(destroyerCGP4);
            gamePlayerCG4.addShip(patrolBeatCGP4);
            gamePlayerJG4.addShip(submarineJGP4);
            gamePlayerJG4.addShip(patrolBoatJGP4);
            gamePlayerTG5.addShip(destroyerTGP5);
            gamePlayerTG5.addShip(patrolBoatTGP5);
            gamePlayerJG5.addShip(submarineJGP5);
            gamePlayerJG5.addShip(patrolBoatJGP5);
            gamePlayerKG6.addShip(destroyerKGP6);
            gamePlayerKG6.addShip(patrolBoatKGP6);
            gamePlayerKG8.addShip(destroyerKGP6);
            gamePlayerTG8.addShip(destroyerTGP8);
            gamePlayerTG8.addShip(submarineTGP8);
            gamePlayerTG8.addShip(patrolBoatTGP8);
            gamePlayerKG8.addShip(patrolBoatKGP8);

            gamePlayerRepository.save(gamePlayerJG1);
            gamePlayerRepository.save(gamePlayerCG1);
            gamePlayerRepository.save(gamePlayerJG2);
            gamePlayerRepository.save(gamePlayerCG2);
            gamePlayerRepository.save(gamePlayerCG3);
            gamePlayerRepository.save(gamePlayerTG3);
            gamePlayerRepository.save(gamePlayerCG4);
            gamePlayerRepository.save(gamePlayerJG4);
            gamePlayerRepository.save(gamePlayerTG5);
            gamePlayerRepository.save(gamePlayerJG5);
            gamePlayerRepository.save(gamePlayerKG6);
            gamePlayerRepository.save(gamePlayerTG8);
            gamePlayerRepository.save(gamePlayerKG8);

            /*Guardo los gamePlayers primero para que se creen los ids  y luego puedo guardar las claves foraneas de
            los gamePlayers en la tabla ship*/

            shipRepository.save(carrierJGP1);
            shipRepository.save(submarineJGP1);
            shipRepository.save(patrolBoatJGP1);
            shipRepository.save(destroyerCGP1);
            shipRepository.save(patrolBoatCGP1);
            shipRepository.save(destroyerJGP2);
            shipRepository.save(patrolBoatJGP2);
            shipRepository.save(submarineCGP2);
            shipRepository.save(patrolBoatCGP2);
            shipRepository.save(destroyerCGP3);
            shipRepository.save(patrolBoatCGP3);
            shipRepository.save(submarineTGP3);
            shipRepository.save(patrolBoatTGP3);
            shipRepository.save(destroyerCGP4);
            shipRepository.save(patrolBeatCGP4);
            shipRepository.save(submarineJGP4);
            shipRepository.save(patrolBeatCGP4);
            shipRepository.save(destroyerTGP5);
            shipRepository.save(patrolBoatTGP5);
            shipRepository.save(submarineJGP5);
            shipRepository.save(patrolBoatJGP5);
            shipRepository.save(destroyerKGP6);
            shipRepository.save(patrolBoatKGP6);
            shipRepository.save(destroyerTGP8);
            shipRepository.save(submarineTGP8);
            shipRepository.save(patrolBoatTGP8);
            shipRepository.save(patrolBoatKGP8);

            Salvo salvo1 = new Salvo(1, new HashSet<>(Arrays.asList("B5", "C5", "F1")));
            Salvo salvo3 = new Salvo(2, new HashSet<>(Arrays.asList("F2", "D5")));
            Salvo salvo2 = new Salvo(1, new HashSet<>(Arrays.asList("B4", "B5", "B6")));
            Salvo salvo4 = new Salvo(2, new HashSet<>(Arrays.asList("E1", "H3", "A2")));
            Salvo salvo5 = new Salvo(1, new HashSet<>(Arrays.asList("A2", "A4", "A6")));
            Salvo salvo7 = new Salvo(2, new HashSet<>(Arrays.asList("A3", "H6")));
            Salvo salvo6 = new Salvo(1, new HashSet<>(Arrays.asList("B5", "D5", "C7")));
            Salvo salvo8 = new Salvo(2, new HashSet<>(Arrays.asList("C5", "C6")));
            Salvo salvo9 = new Salvo(1, new HashSet<>(Arrays.asList("G6", "H6", "A4")));
            Salvo salvo11 = new Salvo(2, new HashSet<>(Arrays.asList("A2", "A3", "D8")));
            Salvo salvo10 = new Salvo(1, new HashSet<>(Arrays.asList("H1", "H2", "H3")));
            Salvo salvo12 = new Salvo(2, new HashSet<>(Arrays.asList("E1", "F2", "G3")));

            gamePlayerJG1.addSalvoes(new HashSet<>(Arrays.asList(salvo1, salvo3)));
            gamePlayerCG1.addSalvoes(new HashSet<>(Arrays.asList(salvo2, salvo4)));
            gamePlayerJG2.addSalvoes(new HashSet<>(Arrays.asList(salvo5, salvo7)));
            gamePlayerCG2.addSalvoes(new HashSet<>(Arrays.asList(salvo6, salvo8)));
            gamePlayerTG3.addSalvoes(new HashSet<>(Arrays.asList(salvo10, salvo12)));
            gamePlayerCG3.addSalvoes(new HashSet<>(Arrays.asList(salvo9, salvo11)));
            salvoRepository.save(salvo1);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            salvoRepository.save(salvo4);
            salvoRepository.save(salvo5);
            salvoRepository.save(salvo6);
            salvoRepository.save(salvo7);
            salvoRepository.save(salvo8);
            salvoRepository.save(salvo9);
            salvoRepository.save(salvo10);
            salvoRepository.save(salvo11);
            salvoRepository.save(salvo12);
        };
    }
}

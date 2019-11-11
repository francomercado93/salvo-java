package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    private LocalDateTime fechaGame;
    private Player tony;
    private Player jackBauer;
    private Player cObrian;
    private Player kimBauer;
    private Game game1;
    private Game game2;
    private Game game3;
    private Game game4;
    private Game game8;
    private Game game5;
    private Game game6;
    private Game game7;
    private GamePlayer gamePlayerJG1;
    private GamePlayer gamePlayerCG1;
    private GamePlayer gamePlayerJG2;
    private GamePlayer gamePlayerCG2;
    private GamePlayer gamePlayerCG3;
    private GamePlayer gamePlayerTG3;
    private GamePlayer gamePlayerCG4;
    private GamePlayer gamePlayerJG4;
    private GamePlayer gamePlayerTG5;
    private GamePlayer gamePlayerJG5;
    private GamePlayer gamePlayerKG6;
    private GamePlayer gamePlayerTG8;
    private GamePlayer gamePlayerKG8;
    private GamePlayer gamePlayerTG7;
    private Ship carrierJGP1;
    private Ship submarineJGP1;
    private Ship patrolBoatJGP1;
    private Ship destroyerCGP1;
    private Ship patrolBoatCGP1;
    private Ship destroyerJGP2;
    private Ship patrolBoatJGP2;
    private Ship submarineCGP2;
    private Ship patrolBoatCGP2;
    private Ship destroyerCGP3;
    private Ship patrolBoatCGP3;
    private Ship submarineTGP3;
    private Ship patrolBoatTGP3;
    private Ship destroyerCGP4;
    private Ship patrolBeatCGP4;
    private Ship submarineJGP4;
    private Ship patrolBoatJGP4;
    private Ship destroyerTGP5;
    private Ship patrolBoatTGP5;
    private Ship submarineJGP5;
    private Ship patrolBoatJGP5;
    private Ship destroyerKGP6;
    private Ship patrolBoatKGP6;
    private Ship destroyerTGP8;
    private Ship submarineTGP8;
    private Ship patrolBoatTGP8;
    private Ship patrolBoatKGP8;
    private Salvo salvo1;
    private Salvo salvo3;
    private Salvo salvo2;
    private Salvo salvo4;
    private Salvo salvo5;
    private Salvo salvo7;
    private Salvo salvo6;
    private Salvo salvo8;
    private Salvo salvo9;
    private Salvo salvo11;
    private Salvo salvo10;
    private Salvo salvo12;
    private Salvo salvo13;
    private Salvo salvo15;
    private Salvo salvo14;
    private Salvo salvo16;
    private Salvo salvo17;
    private Salvo salvo19;
    private Salvo salvo18;
    private Salvo salvo20;
    private Salvo salvo21;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

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

    @Autowired
    private ScoreRepository scoreRepository;

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    private void encodePassword(Player player) {
        player.setPassword(passwordEncoder().encode(player.getPassword()));
    }

    @Bean
    public CommandLineRunner initData() {
        return (args) -> {

            fechaGame = LocalDateTime.of(2019, 10, 07, 01, 50);
            this.createPlayers();
            this.createGames();
            this.createGamePlayers();
            this.createShips();
            this.addShipsGamePlayers();
            this.saveGamePlayers();
            /*Guardo los gamePlayers primero para que se creen los ids  y luego puedo guardar las claves foraneas de
            los gamePlayers en la tabla ship*/
            this.saveShips();
            this.createSalvoes();
            this.createScores();
        };
    }

    private void createScores() {
        Score score1 = new Score(game1, jackBauer, new BigDecimal(1));
        Score score2 = new Score(game1, cObrian, new BigDecimal(0));
        Score score3 = new Score(game2, jackBauer, new BigDecimal(0.5));
        Score score4 = new Score(game2, cObrian, new BigDecimal(0.5));
        Score score5 = new Score(game3, cObrian, new BigDecimal(1));
        Score score6 = new Score(game3, tony, new BigDecimal(0));
        Score score7 = new Score(game4, cObrian, new BigDecimal(0.5));
        Score score8 = new Score(game4, jackBauer, new BigDecimal(0.5));
        score1.setScoreGamePlayer();
        score2.setScoreGamePlayer();
        score3.setScoreGamePlayer();
        score4.setScoreGamePlayer();
        score5.setScoreGamePlayer();
        score6.setScoreGamePlayer();
        score7.setScoreGamePlayer();
        score8.setScoreGamePlayer();
//            Score score9 = new Score(game5, tony);
//            Score score10 = new Score(game5, jackBauer);
//            Score score11 = new Score(game6, kimBauer);
//            Score score12 = new Score(game7, tony);
//            Score score13 = new Score(game8, kimBauer);
//            Score score14 = new Score(game8, tony);

        scoreRepository.save(score1);
        scoreRepository.save(score2);
        scoreRepository.save(score3);
        scoreRepository.save(score4);
        scoreRepository.save(score5);
        scoreRepository.save(score6);
        scoreRepository.save(score7);
        scoreRepository.save(score8);
//            scoreRepository.save(score9);
//            scoreRepository.save(score10);
//            scoreRepository.save(score11);
//            scoreRepository.save(score12);
//            scoreRepository.save(score13);
//            scoreRepository.save(score14);
    }


    private void createSalvoes() {
        salvo1 = new Salvo(1, new HashSet<>(Arrays.asList("B5", "C5", "F1")));
        salvo3 = new Salvo(2, new HashSet<>(Arrays.asList("F2", "D5")));
        salvo2 = new Salvo(1, new HashSet<>(Arrays.asList("B4", "B5", "B6")));
        salvo4 = new Salvo(2, new HashSet<>(Arrays.asList("E1", "H3", "A2")));
        salvo5 = new Salvo(1, new HashSet<>(Arrays.asList("A2", "A4", "G6")));
        salvo7 = new Salvo(2, new HashSet<>(Arrays.asList("A3", "H6")));
        salvo6 = new Salvo(1, new HashSet<>(Arrays.asList("B5", "D5", "C7")));
        salvo8 = new Salvo(2, new HashSet<>(Arrays.asList("C5", "C6")));
        salvo9 = new Salvo(1, new HashSet<>(Arrays.asList("G6", "H6", "A4")));
        salvo11 = new Salvo(2, new HashSet<>(Arrays.asList("A2", "A3", "D8")));
        salvo10 = new Salvo(1, new HashSet<>(Arrays.asList("H1", "H2", "H3")));
        salvo12 = new Salvo(2, new HashSet<>(Arrays.asList("E1", "F2", "G3")));
        salvo13 = new Salvo(1, new HashSet<>(Arrays.asList("A3", "A4", "F7")));
        salvo15 = new Salvo(2, new HashSet<>(Arrays.asList("A2", "G6", "H6")));
        salvo14 = new Salvo(1, new HashSet<>(Arrays.asList("B5", "C6", "H1")));
        salvo16 = new Salvo(2, new HashSet<>(Arrays.asList("C5", "C7", "D5")));
        salvo17 = new Salvo(1, new HashSet<>(Arrays.asList("A1", "A2", "A3")));
        salvo19 = new Salvo(2, new HashSet<>(Arrays.asList("G6", "G7", "G8")));
        salvo18 = new Salvo(1, new HashSet<>(Arrays.asList("B5", "B6", "C7")));
        salvo20 = new Salvo(2, new HashSet<>(Arrays.asList("C6", "D6", "E6")));
        salvo21 = new Salvo(3, new HashSet<>(Arrays.asList("H1", "H8")));

        gamePlayerJG1.addSalvoes(new HashSet<>(Arrays.asList(salvo1, salvo3)));
        gamePlayerCG1.addSalvoes(new HashSet<>(Arrays.asList(salvo2, salvo4)));
        gamePlayerJG2.addSalvoes(new HashSet<>(Arrays.asList(salvo5, salvo7)));
        gamePlayerCG2.addSalvoes(new HashSet<>(Arrays.asList(salvo6, salvo8)));
        gamePlayerTG3.addSalvoes(new HashSet<>(Arrays.asList(salvo10, salvo12)));
        gamePlayerCG3.addSalvoes(new HashSet<>(Arrays.asList(salvo9, salvo11)));
        gamePlayerCG4.addSalvoes(new HashSet<>(Arrays.asList(salvo13, salvo15)));
        gamePlayerJG4.addSalvoes(new HashSet<>(Arrays.asList(salvo14, salvo16)));
        gamePlayerTG5.addSalvoes(new HashSet<>(Arrays.asList(salvo17, salvo19)));
        gamePlayerJG5.addSalvoes(new HashSet<>(Arrays.asList(salvo18, salvo20, salvo21)));

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
    }

    private void saveShips() {
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
    }

    private void saveGamePlayers() {
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
        gamePlayerRepository.save(gamePlayerTG7);
    }

    private void addShipsGamePlayers() {
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
    }

    private void createShips() {

        carrierJGP1 = new Ship("carrier");
        submarineJGP1 = new Ship("submarine");
        patrolBoatJGP1 = new Ship("patrolboat");
        destroyerCGP1 = new Ship("destroyer");
        patrolBoatCGP1 = new Ship("patrolboat");
        destroyerJGP2 = new Ship("destroyer");
        patrolBoatJGP2 = new Ship("patrolboat");
        submarineCGP2 = new Ship("submarine");
        patrolBoatCGP2 = new Ship("patrolboat");
        destroyerCGP3 = new Ship("destroyer");
        patrolBoatCGP3 = new Ship("patrolboat");
        submarineTGP3 = new Ship("submarine");
        patrolBoatTGP3 = new Ship("patrolboat");
        destroyerCGP4 = new Ship("destroyer");
        patrolBeatCGP4 = new Ship("patrolboat");
        submarineJGP4 = new Ship("submarine");
        patrolBoatJGP4 = new Ship("patrolboat");
        destroyerTGP5 = new Ship("submarine");
        patrolBoatTGP5 = new Ship("patrolboat");
        submarineJGP5 = new Ship("submarine");
        patrolBoatJGP5 = new Ship("patrolboat");
        destroyerKGP6 = new Ship("destroyer");
        patrolBoatKGP6 = new Ship("patrolboat");
        destroyerTGP8 = new Ship("destroyer");
        submarineTGP8 = new Ship("submarine");
        patrolBoatTGP8 = new Ship("patrolboat");
        patrolBoatKGP8 = new Ship("patrolboat");

        carrierJGP1.addLocations(Arrays.asList("H2", "H3", "H4", "H5", "H6"));
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
    }

    private void createGamePlayers() {
        /*joinDate se crea inicializa sola cuando se crear un gamePlayer con LocalDateTime.now()*/
        gamePlayerJG1 = new GamePlayer(jackBauer, game1);
        gamePlayerCG1 = new GamePlayer(cObrian, game1);
        gamePlayerJG2 = new GamePlayer(jackBauer, game2);
        gamePlayerCG2 = new GamePlayer(cObrian, game2);
        gamePlayerCG3 = new GamePlayer(cObrian, game3);
        gamePlayerTG3 = new GamePlayer(tony, game3);
        gamePlayerCG4 = new GamePlayer(cObrian, game4);
        gamePlayerJG4 = new GamePlayer(jackBauer, game4);
        gamePlayerTG5 = new GamePlayer(tony, game5);
        gamePlayerJG5 = new GamePlayer(jackBauer, game5);
        gamePlayerKG6 = new GamePlayer(kimBauer, game6);
        gamePlayerTG8 = new GamePlayer(tony, game8);
        gamePlayerKG8 = new GamePlayer(kimBauer, game8);
        gamePlayerTG7 = new GamePlayer(tony, game7);

    }

    private void createGames() {
        game1 = new Game(fechaGame);
        game2 = new Game(fechaGame.plusHours(1));
        game3 = new Game(fechaGame.plusHours(2));
        game4 = new Game(fechaGame.plusHours(3));
        game5 = new Game(fechaGame.plusHours(4));
        game6 = new Game(fechaGame.plusHours(5));
        game7 = new Game(fechaGame.plusHours(6));
        game8 = new Game(fechaGame.plusHours(7));

        gameRepository.save(game1);
        gameRepository.save(game2);
        gameRepository.save(game3);
        gameRepository.save(game4);
        gameRepository.save(game5);
        gameRepository.save(game6);
        gameRepository.save(game7);
        gameRepository.save(game8);
    }

    private void createPlayers() {
        jackBauer = new Player("j.bauer@ctu.gov", "Jack", "Bauer", "24");
        cObrian = new Player("c.obrian@ctu.gov", "Chloe", "O'Brian", "42");
        kimBauer = new Player("kim_bauer@ctu.gov", "Kim", "Bauer", "kb");
        tony = new Player("t.almeida@ctu.gov", "Tony", "Almeida", "mole");

        this.encodePassword(jackBauer);
        this.encodePassword(cObrian);
        this.encodePassword(kimBauer);
        this.encodePassword(tony);

        playerRepository.save(jackBauer);
        playerRepository.save(cObrian);
        playerRepository.save(kimBauer);
        playerRepository.save(tony);
    }
}

//Revisar import EnableWebSecurity
@Configuration
@EnableWebSecurity
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    //Para dar a spring un metodo para obtener la informacion del playerRepository
    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        //devuelve un objeto userDetailsService. Le pasamos un input y si lo encuentra devuelve el objeto
        //userDetail con el userNam y password, spring puede usar este objeto para ver si son las contraseñas
        // y nombre de usuario correctos
        auth.userDetailsService(inputName -> {
            Player player = playerRepository.findByUserName(inputName);
            if (player != null) {
//                La clase User necesita usar la codificacion de contraseña para comparar las contraseñas de
//                inicio de sesion  con las que estan almacenadas (Para esto se usa el passwordEncoder )
                return new User(player.getUserName(), player.getPassword(),
                        ///Definimos los roles que puede tener los distintos usuarios
                        AuthorityUtils.createAuthorityList("USER"));
//           Preguntar por el mensaje de error que muestra el front cuando se ingresa un usuaario que no existe en la bd
//           o cuando se ingresa una contraseña incorrecta
            } else {
                throw new UsernameNotFoundException("error: " + inputName);
            }
        });
    }
}

//Autorizacion
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
//    Un * para archivos, ** para carpetas y archivos
                //Games tienen que ser accedidos por cualquier usuario
                .antMatchers("/").permitAll()
                .antMatchers("/api/games").permitAll()
                .antMatchers("/api/players").permitAll()
                .antMatchers("/h2-console/**").permitAll()//allow h2 console access to admins only
                .antMatchers("/web/**").permitAll()
//                para acceder a cualquier servicio rest o al game_view se necesita estar logueado
                .antMatchers("/api/game/**").hasAuthority("USER")
                .antMatchers("/api/games/**").hasAuthority("USER")
                .antMatchers("/rest/*").hasAuthority("USER")
                .antMatchers("/api/game_view/*").hasAuthority("USER")
                /*Revisar permisos que tiene un usuario para que otro usuario no pueda ver sus datos*/
                .anyRequest().authenticated()//all other urls can be access by any authenticated role
                .and().csrf().ignoringAntMatchers("/h2-console/**")//don't apply CSRF protection to /h2-console
                .and().headers().frameOptions().sameOrigin()//allow use of frame to same origin urls
//                and() empieza una nueva seccion de reglas
                .and();

//        Para hacer un post en la consola hay que usar esto
//        $.post("/api/login", { name: "j.bauer@ctu.gov", pwd: "24" }).done(function() { console.log("logged in!"); })
//        Crea un controlador de inicio de sesion
        http.formLogin()
                //Tienen que ser los mismos que aparecen en el AJAX
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

//        Configurar seguridad de servicios web con respuestas http
//         desactivar tokens CSRF
        http.csrf().disable();
        http.headers().frameOptions().disable();
//         Si un player no esta autenticado e intenta acceder a un url protegido solo envia authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // Si el login es exitosos limpia los flags pidiendo por la autenticacion
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // Si el login falla solo envia authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // Si el logout es exitoso solo envia success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

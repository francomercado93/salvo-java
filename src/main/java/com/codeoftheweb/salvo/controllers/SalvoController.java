package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Hits;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
public class SalvoController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    //Encriptar pass para el sign up,

    //    Para hacer un post para crear un player los parametros tienen que coincidir con los que recibe el metodo register
    @RequestMapping(path = "/players", method = RequestMethod.POST)
//    Devuelve un ResponseEntity<Map<String, Object>> y no ResponseEntity<Object>
//    ResponseEntity<Object> no es subtipo de ResponseEntity<Map<String, Object>>
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String email, @RequestParam String password) {
//      Si no se llenan los campos de email y password responde con un status FORBIDDEN
        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
//      Si ya existe el usuario con ese email no deja crear otro de nuevo, responde con un status FORBIDDEN
        if (playerRepository.findByUserName(email) != null) {
            return new ResponseEntity<>(makeMap("error", "Username already in use"), HttpStatus.FORBIDDEN);
        }
//      una vez que pasa todas las pruebas puede guardar en la bd
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (this.isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Unauthorized "), HttpStatus.UNAUTHORIZED);
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            Game newGame = new Game(LocalDateTime.now());
            gameRepository.save(newGame);
            GamePlayer newGamePlayer = new GamePlayer(player, newGame);
            gamePlayerRepository.save(newGamePlayer);
            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put(key, value);
        return dto;
    }

    //ids de juegos
    @RequestMapping("/games-ids")
    public List<Long> getAllGamesIds() {
        return gameRepository.findAll()
                .stream()
                .map(game -> game.getId())
                .collect(Collectors.toList());
    }

    //Obtenemos un json con todos los juegos con un formato que elegimos
    @RequestMapping("/games")
    //Map para task5
    public Map<String, Object> getAllGamesDTO(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (this.isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.makeOwnerDTOPlayers());
        }
        dto.put("games", this.getAllGames());
        return dto;
    }

    public List<Object> getAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(owner -> owner.makeOwnerDTOGames())
                .collect(Collectors.toList());
    }

    @RequestMapping("/players")
    public List<Object> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(owner -> owner.makeOwnerDTOPlayers())
                .collect(Collectors.toList());
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    //Le paso el id de gamePlayer y en el json el primer id es el del game
    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> getGamePlayerInformation(@PathVariable Long nn, Authentication authentication) {
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();
        if (this.isPlayerValid(player, gamePlayer)) {
            return new ResponseEntity<>(makeMap("error", "No posee autorizacion"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gamePlayer.getGame();
            //Reutilizo el DTO del game y le agrego los barcos del player y  los salvos
            Map<String, Object> dto = game.makeOwnerDTOGames();
            putShips(gamePlayer, dto);
            putSalvoes(game, dto);
            Hits hits = new Hits();
            dto.put("hits", hits.makeDTO());
            return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
        }
    }

    private boolean isPlayerValid(Player player, GamePlayer gamePlayer) {
        return player.getId() != gamePlayer.getPlayer().getId();
    }

    private void putSalvoes(Game game, Map<String, Object> dto) {
        dto.put("salvoes", game.getGamePlayers()
                .stream()
                .flatMap(gp -> gp.getSalvoes()
                        .stream()
                        .map(salvo -> salvo.makeDTOSalvo())));
    }

    private void putShips(GamePlayer gamePlayer, Map<String, Object> dto) {
        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(ship -> ship.makeDTOShip())
                .collect(Collectors.toList()));
    }
}
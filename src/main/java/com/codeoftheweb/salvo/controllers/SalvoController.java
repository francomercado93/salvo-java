package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    private ShipRepository shipRepository;

    //Encriptar pass para el sign up,
    //    Para hacer un post para crear un player los parametros tienen que coincidir con los que recibe el metodo register
    @RequestMapping(path = "/players", method = RequestMethod.POST)

    //    Devuelve un ResponseEntity<Map<String, Object>> y no ResponseEntity<Object>
//    ResponseEntity<Object> no es subtipo de ResponseEntity<Map<String, Object>>
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String email, @RequestParam String password) {
//      Si no se llenan los campos de email y password responde con un status FORBIDDEN
        ResponseEntity<Map<String, Object>> responseEntity;
        if (email.isEmpty() || password.isEmpty()) {
            responseEntity = new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
//      Si ya existe el usuario con ese email no deja crear otro de nuevo, responde con un status FORBIDDEN
        else if (playerRepository.findByUserName(email) != null) {
            responseEntity = new ResponseEntity<>(makeMap("error", "Username already in use"), HttpStatus.FORBIDDEN);
        } else {
//      una vez que pasa todas las pruebas puede guardar en la bd
            playerRepository.save(new Player(email, passwordEncoder.encode(password)));
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        }
        return responseEntity;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> responseEntity;
        if (this.isGuest(authentication)) {
            responseEntity = new ResponseEntity<>(makeMap("error", "Unauthorized "), HttpStatus.UNAUTHORIZED);
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            Game newGame = gameRepository.save(new Game(LocalDateTime.now()));
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(player, newGame));
            responseEntity = new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
        return responseEntity;
    }

    @RequestMapping(path = "/game/{nn}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable Long nn) {
        ResponseEntity<Map<String, Object>> responseEntity;
        if (this.isGuest(authentication)) {
            responseEntity = new ResponseEntity<>(this.makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            Game game = gameRepository.findById(nn).orElse(null);
            if (game == null) {
                responseEntity = new ResponseEntity<>(this.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
            } else if (game.getGamePlayers().size() > 1) {
                responseEntity = new ResponseEntity<>(this.makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
            } else {
                GamePlayer newGamePlayer = new GamePlayer(player, game);
                gamePlayerRepository.save(newGamePlayer);
                responseEntity = new ResponseEntity<>(this.makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
            }
        }
        return responseEntity;
    }

    @RequestMapping(path = "/game/{nn}/players")
    public ResponseEntity<Map<String, Object>> getPlayersGame(@PathVariable Long nn) {
        ResponseEntity<Map<String, Object>> responseEntity;
        Game game = gameRepository.findById(nn).orElse(null);
        if (game == null) {
            responseEntity = new ResponseEntity<>(this.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        } else {
            responseEntity = new ResponseEntity<>(game.makeOwnerDTOGames(), HttpStatus.ACCEPTED);
        }
        return responseEntity;
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
        ResponseEntity<Map<String, Object>> responseEntity;
        if (this.isPlayerValid(player, gamePlayer)) {
            responseEntity = new ResponseEntity<>(makeMap("error", "No posee autorizacion"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gamePlayer.getGame();
            //Reutilizo el DTO del game y le agrego los barcos del player y  los salvos
            Map<String, Object> dto = game.makeOwnerDTOGames();
            putShips(gamePlayer, dto);
            putSalvoes(game, dto);
            Hits hits = new Hits();
            dto.put("hits", hits.makeDTO());
            responseEntity = new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
        }
        return responseEntity;
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

    @RequestMapping(value = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable Long gamePlayerId, @RequestBody Set<Ship> ships, Authentication authentication) {
        ResponseEntity<Map<String, Object>> responseEntity;
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
//        Revisar ultima condicion
        if (this.isGuest(authentication) || gamePlayer == null ||
                gamePlayer.getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()) {
            responseEntity = new ResponseEntity<>(this.makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } else if (!gamePlayer.getShips().isEmpty()) {
            responseEntity = new ResponseEntity<>(this.makeMap("error", "Barcos ya estan ubicados"), HttpStatus.FORBIDDEN);
        } else {
//            Los nombres de los atributos de la clas tienen que coincidir con el del json que envia el frontend
            ships.forEach(ship -> gamePlayer.addShip(ship));
            gamePlayerRepository.save(gamePlayer);
            ships.forEach(ship -> shipRepository.save(ship));
            responseEntity = new ResponseEntity<>(this.makeMap("OK", "OK"), HttpStatus.CREATED);
        }
        return responseEntity;
    }

    @RequestMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> getPlaceShips(Authentication authentication, @PathVariable Long gamePlayerId) {
        ResponseEntity<Map<String, Object>> responseEntity;
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
        if (this.isPlayerValid(player, gamePlayer)) {
            responseEntity = new ResponseEntity<>(makeMap("error", "No posee autorizacion"), HttpStatus.UNAUTHORIZED);
        } else {
            responseEntity = new ResponseEntity<>(this.makeMap("ships", gamePlayer.getShips().stream().map(ship -> ship.makeDTOShip())), HttpStatus.ACCEPTED);
        }
        return responseEntity;
    }
}
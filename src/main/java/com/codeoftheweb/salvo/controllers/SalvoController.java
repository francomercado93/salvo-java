package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
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

    @Autowired
    private SalvoRepository salvoRepository;

    @RequestMapping(path = "/game/{nn}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable Long nn) {

        if (this.isGuest(authentication)) {
            return getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        Game game = gameRepository.findById(nn).orElse(null);
        if (game == null) {
            return getResponseEntity("error", "No such game", HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().size() > 1) {
            return getResponseEntity("error", "Game is full", HttpStatus.FORBIDDEN);
        }
        GamePlayer newGamePlayer = new GamePlayer(player, game);
        gamePlayerRepository.save(newGamePlayer);
        return this.getResponseEntity("gpid", String.valueOf(newGamePlayer.getId()), HttpStatus.CREATED);
    }

    private ResponseEntity<Map<String, Object>> getResponseEntity(String error, String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(this.makeMap(error, message), httpStatus);
    }

    //Le paso el id de gamePlayer y en el json el primer id es el del game
    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> getGamePlayerInformation(@PathVariable Long nn, Authentication authentication) {

        if (this.isGuest(authentication)) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).orElse(null);
        if (gamePlayer == null) {
            return getResponseEntity("error", "No existe gamePlayer con el id " + nn, HttpStatus.FORBIDDEN);
        }
        if (this.isPlayerNotValid(player, gamePlayer)) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
        Game game = gamePlayer.getGame();
        //Reutilizo el DTO del game y le agrego los barcos del player y  los salvos
        Map<String, Object> dto = game.makeOwnerDTOGames();
        putShips(gamePlayer, dto);
        putSalvoes(game, dto);
        Hits hits = new Hits();
        dto.put("hits", hits.makeDTO());
        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable Long gamePlayerId,
                                                          @RequestBody Set<Ship> ships, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
//        Revisar ultima condicion
        if (gamePlayer == null) {
            return getResponseEntity("error", "No existe game player con el id " + gamePlayerId, HttpStatus.FORBIDDEN);
        }
        if (this.isGuest(authentication)) {
            return getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()) {
            return getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (!gamePlayer.getShips().isEmpty()) {
            return getResponseEntity("error", "Barcos ya estan ubicados", HttpStatus.FORBIDDEN);
        }
//            Los nombres de los atributos de la clas tienen que coincidir con el del json que envia el frontend
        ships.forEach(ship -> gamePlayer.addShip(ship));
        gamePlayerRepository.save(gamePlayer);
        ships.forEach(ship -> shipRepository.save(ship));
        return getResponseEntity("OK", "OK", HttpStatus.CREATED);
    }

    @RequestMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> getPlaceShips(Authentication authentication, @PathVariable Long
            gamePlayerId) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
        if (gamePlayer == null) {
            return getResponseEntity("error", "No existe game player con el id " + gamePlayerId, HttpStatus.FORBIDDEN);
        }
        if (this.isGuest(authentication)) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        if (this.isPlayerNotValid(player, gamePlayer)) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(this.makeMap("ships", gamePlayer.getShips().stream().map(ship -> ship.makeDTOShip())), HttpStatus.ACCEPTED);
    }

    @RequestMapping("/games/players/{nn}/salvoes")
    public ResponseEntity<Map<String, Object>> getPlaceSalvoes(Authentication authentication, @PathVariable Long nn) {

        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).orElse(null);
//      SE REPITE CODIGO
        if (this.isGuest(authentication)) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null) {
            return getResponseEntity("error", "No existe gamePlayer con el id " + nn, HttpStatus.FORBIDDEN);
        }
        if ((this.isPlayerNotValid(playerRepository.findByUserName(authentication.getName()), gamePlayer))) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
//      SE REPITE CODIGO
        return new ResponseEntity<>(this.makeMap("salvoes", this.makeDTOSalvoes(gamePlayer)), HttpStatus.ACCEPTED);
    }

    private Object makeDTOSalvoes(GamePlayer gamePlayer) {
        return gamePlayer.getSalvoes().stream().map(salvo -> salvo.makeDTOSalvo());
    }

    @RequestMapping(value = "/games/players/{nn}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeSalvoes(@PathVariable Long nn,
                                                            @RequestBody Salvo salvo, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).orElse(null);
        if (gamePlayer == null) {
            return getResponseEntity("error", "No existe el game player con el id" + nn, HttpStatus.FORBIDDEN);
        }
        if (this.isGuest(authentication)) {
            return getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()) {
            return getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getSalvoes().stream().anyMatch(salvo1 -> salvo1.getTurn() == salvo.getTurn()) ||
                salvo.getTurn() > 127) {
            return getResponseEntity("error", "No se puede crear un salvo para este turno", HttpStatus.FORBIDDEN);
        }
        if (salvo.getNumberLocations() > 5) {
            return getResponseEntity("error", "Too many shots in salvo", HttpStatus.FORBIDDEN);
        }
        gamePlayer.addSalvo(salvo);
//            gamePlayerRepository.save(gamePlayer);
        salvoRepository.save(salvo);
        return getResponseEntity("OK", "OK", HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put(key, value);
        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private boolean isPlayerNotValid(Player player, GamePlayer gamePlayer) {
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
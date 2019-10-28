package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
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

    @RequestMapping(path = "/game/{nn}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable Long nn) {

        ResponseEntity<Map<String, Object>> responseEntity;

        if (this.isGuest(authentication)) {
            responseEntity = getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            Game game = gameRepository.findById(nn).orElse(null);
            if (game == null) {
                responseEntity = getResponseEntity("error", "No such game", HttpStatus.FORBIDDEN);
            } else if (game.getGamePlayers().size() > 1) {
                responseEntity = getResponseEntity("error", "Game is full", HttpStatus.FORBIDDEN);
            } else {
                GamePlayer newGamePlayer = new GamePlayer(player, game);
                gamePlayerRepository.save(newGamePlayer);
                responseEntity = this.getResponseEntity("gpid", String.valueOf(newGamePlayer.getId()), HttpStatus.CREATED);
            }
        }
        return responseEntity;
    }

    private ResponseEntity<Map<String, Object>> getResponseEntity(String error, String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(this.makeMap(error, message), httpStatus);
    }

    //Le paso el id de gamePlayer y en el json el primer id es el del game
    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> getGamePlayerInformation(@PathVariable Long nn, Authentication authentication) {

        ResponseEntity<Map<String, Object>> responseEntity;
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();
        if (this.isPlayerValid(player, gamePlayer)) {
            responseEntity = getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
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

    @RequestMapping(value = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable Long gamePlayerId,
                                                          @RequestBody Set<Ship> ships, Authentication authentication) {
        ResponseEntity<Map<String, Object>> responseEntity;
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
//        Revisar ultima condicion
        if (this.isGuest(authentication) || gamePlayer == null ||
                gamePlayer.getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()) {
            responseEntity = getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        } else if (!gamePlayer.getShips().isEmpty()) {
            responseEntity = getResponseEntity("error", "Barcos ya estan ubicados", HttpStatus.FORBIDDEN);
        } else {
//            Los nombres de los atributos de la clas tienen que coincidir con el del json que envia el frontend
            ships.forEach(ship -> gamePlayer.addShip(ship));
            gamePlayerRepository.save(gamePlayer);
            ships.forEach(ship -> shipRepository.save(ship));
            responseEntity = getResponseEntity("OK", "OK", HttpStatus.CREATED);
        }
        return responseEntity;
    }

    @RequestMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> getPlaceShips(Authentication authentication, @PathVariable Long gamePlayerId) {
        ResponseEntity<Map<String, Object>> responseEntity;
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
        if (this.isPlayerValid(player, gamePlayer)) {
            responseEntity = getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        } else {
            responseEntity = new ResponseEntity<>(this.makeMap("ships", gamePlayer.getShips().stream().map(ship -> ship.makeDTOShip())), HttpStatus.ACCEPTED);
        }
        return responseEntity;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put(key, value);
        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
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
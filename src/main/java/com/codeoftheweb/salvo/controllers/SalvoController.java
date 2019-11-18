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

import java.math.BigDecimal;
import java.util.*;
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
    private ScoreRepository scoreRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable Long gameId) {

        ResponseEntity<Map<String, Object>> responseEntity;
        if (this.isGuest(authentication)) {
            responseEntity = getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            responseEntity = getResponseEntity("error", "No such game", HttpStatus.FORBIDDEN);
        }
        if (game.getNumberGamePlayers() > 1) {
            return getResponseEntity("error", "Game is full", HttpStatus.FORBIDDEN);
        }
        GamePlayer newGamePlayer = new GamePlayer(player, game);
        gamePlayerRepository.save(newGamePlayer);
        return this.getResponseEntity("gpid", String.valueOf(newGamePlayer.getId()), HttpStatus.CREATED);
    }

    private ResponseEntity<Map<String, Object>> getResponseEntity(String error, String message, HttpStatus
            httpStatus) {
        return new ResponseEntity<>(this.makeMap(error, message), httpStatus);
    }

    //Le paso el id de gamePlayer y en el json el primer id es el del game
    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGamePlayerInformation(@PathVariable Long
                                                                                gamePlayerId, Authentication authentication) {
        if (this.isGuest(authentication)) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
        if (gamePlayer == null) {
            return getResponseEntity("error", "No existe gamePlayer con el id " + gamePlayerId, HttpStatus.FORBIDDEN);
        }
        if (this.isPlayerNotValid(player, gamePlayer)) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
        Game game = gamePlayer.getGame();
        Map<String, Object> dto = game.makeOwnerDTOGames(gamePlayer);
        putShips(gamePlayer, dto);
        putSalvoes(game, dto);
        putHits(gamePlayer, dto);
        createScore(player, game);
        return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
    }

    private void createScore(Player player, Game game) {
        Score newScore = null;
        if (game.getGameState().equals("WON")) {
            newScore = new Score(game, player, new BigDecimal(1));
        }
        if (game.getGameState().equals("LOST")) {
            newScore = new Score(game, player, new BigDecimal(0));
        }
        if (game.getGameState().equals("TIE")) {
            newScore = new Score(game, player, new BigDecimal(0.5));
        }
        if (newScore != null) {
            newScore.setScoreGamePlayer();
            scoreRepository.save(newScore);
        }
    }

    private void putHits(GamePlayer gamePlayerLogged, Map<String, Object> dto) {
        GamePlayer gamePlayerOpponent = gamePlayerLogged.getGamePlayerOpponet();
        Map<String, Object> hits = new LinkedHashMap<>();
        hits.put("self", getHits(gamePlayerLogged, gamePlayerOpponent));
        hits.put("opponent", getHits(gamePlayerOpponent, gamePlayerLogged));
        dto.put("hits", hits);
    }

    private List<Map<String, Object>> getHits(GamePlayer gamePlayer1, GamePlayer gamePlayer2) {
        return gamePlayer2
                .getSalvoes()
                .stream()
                .sorted(Comparator.comparingInt(Salvo::getTurn))
                .map(salvo -> salvo.makeDTOHits(gamePlayer1))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable Long gamePlayerId,
                                                          @RequestBody Set<Ship> ships, Authentication authentication) {

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

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
        return new ResponseEntity<>(this.makeMap("ships", gamePlayer
                .getShips()
                .stream()
                .sorted(Comparator.comparingLong(Ship::getId))
                .map(ship -> ship.makeDTOShip())), HttpStatus.ACCEPTED);
    }

    @RequestMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> getPlaceSalvoes(Authentication authentication, @PathVariable Long
            gamePlayerId) {

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
//      SE REPITE CODIGO
        if (this.isGuest(authentication)) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null) {
            return getResponseEntity("error", "No existe gamePlayer con el id " + gamePlayerId, HttpStatus.FORBIDDEN);
        }
        Game game = gamePlayer.getGame();
        if (game.getGameState().equals("WON") || game.getGameState().equals("LOST") || game.getGameState().equals("TIE")) {
            return getResponseEntity("error", "El juego ha finalizado", HttpStatus.FORBIDDEN);
        }
        if ((this.isPlayerNotValid(playerRepository.findByUserName(authentication.getName()), gamePlayer))) {
            return getResponseEntity("error", "No posee autorizacion", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(this.makeMap("salvoes", this.makeDTOSalvoes(gamePlayer)), HttpStatus.ACCEPTED);
    }

    private Object makeDTOSalvoes(GamePlayer gamePlayer) {
        return gamePlayer
                .getSalvoes()
                .stream()
                .sorted(Comparator.comparingInt(Salvo::getTurn))
                .map(salvo -> salvo.makeDTOSalvo());
    }

    @RequestMapping(value = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeSalvoes(@PathVariable Long gamePlayerId,
                                                            @RequestBody Salvo salvo, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
        if (gamePlayer == null) {
            return getResponseEntity("error", "No existe el game player con el id" + gamePlayerId, HttpStatus.FORBIDDEN);
        }
        if (this.isGuest(authentication)) {
            return getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != playerRepository.findByUserName(authentication.getName()).getId()) {
            return getResponseEntity("error", "Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        GamePlayer opponent = gamePlayer.getGamePlayerOpponet();
        if (opponent.getId() == null) {
            return getResponseEntity("error", "No existe un oponente", HttpStatus.FORBIDDEN);
        }
//        No puede ingresar un salvo hasta que el otro oponente ingrese su salvo
        if (gamePlayer.getNumberOfSalvos() > opponent.getNumberOfSalvos()) {
            return getResponseEntity("error", "No puede ingresar un nuevo salvo hasta que el oponente ingrese su salvo", HttpStatus.FORBIDDEN);
        }

        if (salvo.getNumberLocations() > 5) {
            return getResponseEntity("error", "Too many shots in salvo", HttpStatus.FORBIDDEN);
        }

        salvo.setTurn(gamePlayer.getNumberOfSalvos() + 1);
        gamePlayer.addSalvo(salvo);
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
        dto.put("salvoes", game
                .getGamePlayers()
                .stream()
                .flatMap(gp -> gp.getSalvoes()
                        .stream()
                        .sorted(Comparator.comparingInt(Salvo::getTurn))
                        .map(salvo -> salvo.makeDTOSalvo())));
    }

    private void putShips(GamePlayer gamePlayer, Map<String, Object> dto) {
        dto.put("ships", gamePlayer
                .getShips()
                .stream()
                .sorted(Comparator.comparingLong(Ship::getId))
                .map(ship -> ship.makeDTOShip())
                .collect(Collectors.toList()));
    }
}
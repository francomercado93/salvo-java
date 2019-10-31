package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
public class GameController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put(key, value);
        return dto;
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

    @RequestMapping(path = "/game/{nn}/players")
    public ResponseEntity<Map<String, Object>> getPlayersGame(@PathVariable Long nn) {
        ResponseEntity<Map<String, Object>> responseEntity;
        Game game = gameRepository.findById(nn).orElse(null);
        if (game == null) {
            responseEntity = new ResponseEntity<>(this.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        } else {
//            revisar
            responseEntity = new ResponseEntity<>(game.makeOwnerDTOGames(), HttpStatus.ACCEPTED);
        }
        return responseEntity;
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

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}

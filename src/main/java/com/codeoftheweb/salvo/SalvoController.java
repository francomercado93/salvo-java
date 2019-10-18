package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    //ids de juegos
    @RequestMapping("/games-ids")
    public List<Long> getAllGamesIds() {
        return gameRepository.findAll().stream().map(game -> game.getId()).collect(Collectors.toList());
    }

    //Obtenemos un json con todos los juegos con un formato que elegimos
    @RequestMapping("/games")
    public List<Object> getAllGames() {
        return gameRepository.findAll().stream().map(owner -> owner.makeOwnerDTOGames()).collect(Collectors.toList());
    }

    @RequestMapping("/players")
    public List<Object> getAllPlayers() {
        return playerRepository.findAll().stream().map(owner -> owner.makeOwnerDTOPlayers()).collect(Collectors.toList());
    }

    //Le paso el id de gamePlayer y en el json el primer id es el del game
    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> getGamePlayerInformation(@PathVariable Long nn) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();
        Game game = gamePlayer.getGame();
        //Reutilizo el DTO del game y le agrego los barcos del player
        Map<String, Object> dto = game.makeOwnerDTOGames();
        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(ship -> ship.makeDTOShip())
                .collect(Collectors.toList()));

        dto.put("salvoes", game.getGamePlayers()
                .stream()
                .flatMap(gp -> gp.getSalvoes()
                        .stream()
                        .map(salvo -> salvo.makeDTOSalvo())));
        return dto;
    }
}

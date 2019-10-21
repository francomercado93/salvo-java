package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;

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
        return gameRepository.findAll()
                .stream()
                .map(game -> game.getId())
                .collect(Collectors.toList());
    }

    //Obtenemos un json con todos los juegos con un formato que elegimos
    @RequestMapping("/games")
    public Map getAllGamesDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", "Guest");
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

    //Le paso el id de gamePlayer y en el json el primer id es el del game
    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> getGamePlayerInformation(@PathVariable Long nn) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();
        Game game = gamePlayer.getGame();
        //Reutilizo el DTO del game y le agrego los barcos del player y  los salvos
        Map<String, Object> dto = game.makeOwnerDTOGames();
        putShips(gamePlayer, dto);
        putSalvoes(game, dto);
        return dto;
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

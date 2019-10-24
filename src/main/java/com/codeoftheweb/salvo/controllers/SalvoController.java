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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {
//      Si no se llenan los campos de email y password responde con un status FORBIDDEN
        if (this.emailIsValid(email) || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("Missing data"), HttpStatus.FORBIDDEN);
        }
//      Si ya existe el usuario con ese email no deja crear otro de nuevo, responde con un status FORBIDDEN
        if (playerRepository.findByUserName(email) != null) {
            return new ResponseEntity<>(makeMap("Username already in use"), HttpStatus.FORBIDDEN);
        }
//      una vez que pasa todas las pruebas puede guardar en la bd
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private boolean emailIsValid(@RequestParam String email) {
        return email.isEmpty() || email.contains(" ");
    }

    private Object makeMap(String error) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("error", error);
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

package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //  Encriptar pass para el sign up,
    @RequestMapping(path = "/players", method = RequestMethod.POST)

//    Devuelve un ResponseEntity<Map<String, Object>> y no ResponseEntity<Object>
//    ResponseEntity<Object> no es subtipo de ResponseEntity<Map<String, Object>>
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String email, @RequestParam String password) {
        ResponseEntity<Map<String, Object>> responseEntity;
        if (this.fieldsAreEmpty(email, password)) {
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

    private boolean fieldsAreEmpty(@RequestParam String email, @RequestParam String password) {
        return email.isEmpty() || password.isEmpty();
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put(key, value);
        return dto;
    }

    @RequestMapping("/players")
    public List<Object> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(owner -> owner.makeOwnerDTOPlayers())
                .collect(Collectors.toList());
    }

}

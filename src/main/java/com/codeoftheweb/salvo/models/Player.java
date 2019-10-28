package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;

    private String firstName;

    private String lastName;

    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    List<GamePlayer> gamePlayers = new ArrayList<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private List<Score> scores = new ArrayList<>();

    //constructor vacio

    public Player() {
    }
    //constructor con 3 parametros

    public Player(String _username, String pass) {
        this.userName = _username;
        this.password = pass;
    }

    public Player(String _username, String first, String last, String pass) {
        this.userName = _username;
        this.firstName = first;
        this.lastName = last;
        this.password = pass;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString() {
        return this.firstName + " " + this.lastName;
    }

    @JsonIgnore
    public List<Game> getGames() {
        return gamePlayers.stream().map(gp -> gp.getGame()).collect(Collectors.toList());
    }

    public Map<String, Object> makeOwnerDTOPlayers() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());
        return dto;
    }

    public Map<String, Object> makeDTOPlayer2() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("name", this.getUserName());
        return dto;
    }

    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(List<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public Score getScore(Game game) {
        return this.getScores()
                .stream()
                .filter(score -> score.getGame().getId() == game.getId())
                .findFirst().orElse(null);
    }

    public void addScore(Score score) {
        scores.add(score);
    }
}

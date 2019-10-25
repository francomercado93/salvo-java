package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayerID")
    private GamePlayer gamePlayer;

    private Integer turn;

    @ElementCollection
    @Column(name = "cell")
    private Set<String> cells = new HashSet<>();

    public Salvo() {
    }

    public Salvo(Integer turnGame, Set<String> locations) {
        turn = turnGame;
        cells = locations;
    }

    public Salvo(GamePlayer gameP, Integer turnGame, Set<String> locations) {
        gamePlayer = gameP;
        turn = turnGame;
        cells = locations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Set<String> getCells() {
        return cells;
    }

    public void setCells(Set<String> cells) {
        this.cells = cells;
    }

    public Map<String, Object> makeDTOSalvo() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.getTurn());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("locations", this.getCells());
        return dto;
    }
}

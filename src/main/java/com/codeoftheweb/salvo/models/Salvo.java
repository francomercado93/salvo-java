package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Set<String> salvoLocations = new HashSet<>();

    public Salvo() {
    }

    public Salvo(Integer turnGame, Set<String> locations) {
        turn = turnGame;
        salvoLocations = locations;
    }

    public Salvo(GamePlayer gameP, Integer turnGame, Set<String> locations) {
        gamePlayer = gameP;
        turn = turnGame;
        salvoLocations = locations;
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

    public Set<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(Set<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> makeDTOSalvo() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.getTurn());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("locations", this.getSalvoLocations());
        return dto;
    }

    public Integer getNumberLocations() {
        return this.getSalvoLocations().size();
    }


    public Map<String, Object> makeDTOHits(GamePlayer gamePlayerLogged) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.getTurn());
        dto.put("hitLocations", gamePlayerLogged.getHitsLocations(this));
//        dto.put("damages", this.getDamages(gamePlayerLogged));
        return dto;
    }

    private Map<String, Object> getDamages(GamePlayer gamePlayerLogged) {
        Map<String, Object> damages = new LinkedHashMap<>();
        damages.put("carrierHits", getHitsShip(gamePlayerLogged, "carrier"));
//        damages.put("battleshipHits", getHitsShip(gamePlayerLogged, "battleship"));
        damages.put("submarineHits", getHitsShip(gamePlayerLogged, "submarine"));
        damages.put("destroyerHits", getHitsShip(gamePlayerLogged, "destroyer"));
        damages.put("patrolBoatHits", getHitsShip(gamePlayerLogged, "patrolboat"));
//        damages.put("carrier", getDamageShip(gamePlayerLogged, "carrier"));
        return damages;
    }

    private Long getDamageShip(GamePlayer gamePlayerLogged, String type) {
        return gamePlayerLogged.getShips().stream().filter(ship -> ship.getType().equals(type)).collect(Collectors.toList()).get(0).getTotalDamage();
    }

    private Long getHitsShip(GamePlayer gamePlayerLogged, String type) {
//        Busco el barco que paso como parametro y una vez que lo encuentra obtengo el daño que sufrio el barco
        return gamePlayerLogged.getShips()
                .stream().filter(ship -> ship.getType().equals(type))
                .collect(Collectors.toList()).get(0).getDamage(this);
    }
}

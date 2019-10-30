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
        dto.put("damages", this.getDamages(gamePlayerLogged));
        dto.put("missed", this.getNumberShipsMissed(gamePlayerLogged));
        return dto;
    }

    private Long getNumberShipsMissed(GamePlayer gamePlayerLogged) {
        return getShipsMissed(gamePlayerLogged).stream().count();
    }

    private Set<Ship> getShipsMissed(GamePlayer gamePlayerLogged) {
        return gamePlayerLogged.getShips().stream().filter(ship -> ship.isSunk()).collect(Collectors.toSet());
    }

    private Map<String, Object> getDamages(GamePlayer gamePlayerLogged) {
        Map<String, Object> damages = new LinkedHashMap<>();
//     TODO: refactorizar para usar un forEach
        gamePlayerLogged.getShips().forEach(ship -> {
            damages.put(ship.getType() + "Hits", getHitsShip(gamePlayerLogged, ship.getType()));
        });
//        damages.put("carrierHits", getHitsShip(gamePlayerLogged, "carrier"));
//        damages.put("battleshipHits", getHitsShip(gamePlayerLogged, "battleship"));
//        damages.put("submarineHits", getHitsShip(gamePlayerLogged, "submarine"));
//        damages.put("destroyerHits", getHitsShip(gamePlayerLogged, "destroyer"));
//        damages.put("patrolboatHits", getHitsShip(gamePlayerLogged, "patrolboat"));
        gamePlayerLogged.getShips().forEach(ship -> {
            damages.put(ship.getType(), getTotalDamageShip(gamePlayerLogged, ship.getType()));
        });
//        damages.put("carrier", getTotalDamageShip(gamePlayerLogged, "carrier"));
//        damages.put("battleship", getTotalDamageShip(gamePlayerLogged, "battleship"));
//        damages.put("submarine", getTotalDamageShip(gamePlayerLogged, "submarine"));
//        damages.put("destroyer", getTotalDamageShip(gamePlayerLogged, "destroyer"));
//        damages.put("patrolboat", getTotalDamageShip(gamePlayerLogged, "patrolboat"));
        return damages;
    }

    private Long getTotalDamageShip(GamePlayer gamePlayerLogged, String type) {
        return getShip(gamePlayerLogged, type).getTotalDamage();
    }

    private Long getHitsShip(GamePlayer gamePlayerLogged, String type) {
        return getShip(gamePlayerLogged, type).getDamage(this);
    }

    private Ship getShip(GamePlayer gamePlayerLogged, String type) {
        return gamePlayerLogged.getShips()
                .stream().filter(ship -> ship.getType().equals(type))
                .findFirst().orElse(new Ship(type));
    }
}

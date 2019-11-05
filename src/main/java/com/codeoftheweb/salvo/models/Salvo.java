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
        dto.put("damages", this.getDamages(gamePlayerLogged));
        dto.put("hitLocations", gamePlayerLogged.getHitsLocations(this));
//        TODO: revisar
        dto.put("missed", gamePlayerLogged.getSalvoesMissed(this));
        return dto;
    }

    private Map<String, Object> getDamages(GamePlayer gamePlayerLogged) {
        Map<String, Object> damages = new LinkedHashMap<>();
        damages.put("carrierHits", 0);
        damages.put("battleshipHits", 0);
        damages.put("submarineHits", 0);
        damages.put("destroyerHits", 0);
        damages.put("patrolboatHits", 0);
        damages.put("carrier", 0);
        damages.put("battleship", 0);
        damages.put("submarine", 0);
        damages.put("destroyer", 0);
        damages.put("patrolboat", 0);

        gamePlayerLogged.getShips().forEach(ship -> {
            damages.put(ship.getType() + "Hits", ship.getDamage(this));
        });
        gamePlayerLogged.getShips().forEach(ship -> {
            damages.put(ship.getType(), ship.getTotalDamage());
        });
        return damages;
    }

//    private Long getTotalDamageShip(GamePlayer gamePlayerLogged, String type) {
//        return getShip(gamePlayerLogged, type).getTotalDamage();
//    }
//
//    private Long getHitsShip(GamePlayer gamePlayerLogged, String type) {
//        return getShip(gamePlayerLogged, type).getDamage(this);
//    }

    private Ship getShip(GamePlayer gamePlayerLogged, String type) {
        return gamePlayerLogged.getShips()
                .stream().filter(ship -> ship.getType().equals(type))
                .findFirst().orElse(new Ship(type));
    }
}

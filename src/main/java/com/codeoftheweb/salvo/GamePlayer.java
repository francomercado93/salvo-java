package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    public long id;

    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    //fetchType.EAGER para traerme todos los datos de la tabla GamePlayer y las entradas de la tabla ships que tienen
    //La clave foranea de gamePlayer1
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayerID")
    Set<Ship> ships = new HashSet<>();

    public Player getPlayer() {
        return player;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GamePlayer() {
    }

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.joinDate = LocalDateTime.now();
    }

    public Map<String, Object> makeOwnerDtoGamePlayer() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makeOwnerDTOPlayers());
        return dto;
    }

    public void addShip(Ship ship) {
        ships.add(ship);
    }
}

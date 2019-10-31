package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Salvo> salvoes = new HashSet<>();

    public GamePlayer() {
    }

    public GamePlayer(Game game) {
        this.game = game;
    }

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.joinDate = LocalDateTime.now();
    }

    public Player getPlayer() {
        return player;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Map<String, Object> makeOwnerDtoGamePlayer() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makeOwnerDTOPlayers());
        return dto;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public void addSalvoes(Set<Salvo> newSalvoes) {
        newSalvoes.forEach(salvo -> salvo.setGamePlayer(this));
        salvoes.addAll(newSalvoes);
    }

    public void addSalvo(Salvo newSalvo) {
        newSalvo.setGamePlayer(this);
        salvoes.add(newSalvo);
    }

    public Score getScore() {
        return player.getScore(game);
    }

    public Set<String> getHitsLocations(Salvo salvo) {
        return getShips().stream().flatMap(ship -> ship.getHitsLocationsShip(salvo).stream()).collect(Collectors.toSet());
    }

    public Integer getNumberOfSalvos() {
        return getSalvoes().size();
    }

    public GamePlayer getGamePlayerOpponet() {
//        cuando el gamePlayer es null creo un gamePlayer para que no rompa el frontend pero este nuevo gamePlayer no se guarda en la bd
        return getGame().getGamePlayers().stream().filter(gp -> gp.getId() != this.getId())
                .findFirst().orElse(new GamePlayer(this.getGame()));
    }
}

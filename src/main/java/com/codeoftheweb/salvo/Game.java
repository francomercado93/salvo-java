package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime created;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    public Game() {
    }

    public Game(LocalDateTime created) {
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @JsonIgnore
    public List<Player> getPlayers() {
        return this.gamePlayers.stream().map(gamePlayer -> gamePlayer.getPlayer()).collect(toList());
    }

    //Creamos nuestro propio DTO(Data transfer object)
    public Map<String, Object> makeOwnerDTOGames() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("created", this.convertDateToMilliseconds());
        dto.put("gamePlayers", this.getGamePlayers()
                .stream()
                .map(gp -> gp.makeOwnerDtoGamePlayer())
                .collect(toList()));
        return dto;
    }

    private long convertDateToMilliseconds() {
        return this.getCreated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}

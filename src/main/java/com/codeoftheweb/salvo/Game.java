package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    public long id;

    private LocalDateTime created;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Game() {
    }

    public Game(LocalDateTime created) {
        this.created = created;
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
}

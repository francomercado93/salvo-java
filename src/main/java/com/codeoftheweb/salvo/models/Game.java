package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.Nullable;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private List<Score> scores = new ArrayList<>();

    public Game() {
    }

    public Game(LocalDateTime created) {
        this.created = created;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
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
        return this.gamePlayers
                .stream()
                .map(gamePlayer -> gamePlayer.getPlayer())
                .collect(toList());
    }

    //Creamos nuestro propio DTO(Data transfer object)
    public Map<String, Object> makeOwnerDTOGames(@Nullable GamePlayer logged) {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("created", this.convertDateToMilliseconds());
        if (logged == null) {
            dto.put("gameState", "PLACESHIPS");
        } else {
            dto.put("gameState", this.getGameState(logged));
        }
        dto.put("gamePlayers", gamePlayers
                .stream()
                .map(gp -> gp.makeOwnerDtoGamePlayer())
                .collect(toList()));
//        revisar
        dto.put("scores", gamePlayers
                .stream()
//                REVISAR CUANDO UN SCORE ES NULL
                .filter(gp -> gp.getScore() != null)
//                .sorted(Comparator.comparingLong())
                .map(gp -> gp.getScore().makeDTOScore())
                .collect(toList()));
        return dto;
    }

    private String getGameState(GamePlayer logged) {
        if (logged.noShips()) {
            return "PLACESHIPS";
        }
        if (playerIsMissing()) {
            return "WAITINGFOROPP";
        }
//        Si llega hasta aca es porque tiene un oponente
        GamePlayer opponent = logged.getGamePlayerOpponet();
        if (opponent.noShips() || logged.getNumberOfSalvos() > opponent.getNumberOfSalvos()) {
            return "WAIT";
        }
        if (logged.shipsAreSunk()) {
            return "LOST";
        }
        if (opponent.shipsAreSunk()) {
            return "WON";
        }
        if (logged.shipsAreSunk() && opponent.shipsAreSunk()) {
            return "TIE";
        }
        return "PLAY";
    }

    private boolean playerIsMissing() {
        return getGamePlayers().size() < 2;
    }

    private boolean shipsBothGamePlayerPlaced() {
        return !getGamePlayers().stream().allMatch(gamePlayer -> gamePlayer.getShips().isEmpty());
    }

    private long convertDateToMilliseconds() {
        return this.getCreated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public void addScore(Score score1) {
        scores.add(score1);
    }

    public GamePlayer getGamePlayerOpponet(GamePlayer gamePlayer) {
//        cuando el gamePlayer es null creo un gamePlayer para que no rompa el frontend pero este nuevo gamePlayer no se guarda en la bd
        return this.gamePlayers.stream().filter(gp -> gp.getId() != gamePlayer.getId()).findFirst().orElse(new GamePlayer(gamePlayer.getGame()));
    }

    public Integer getNumberGamePlayers() {
        return getGamePlayers().size();
    }
}

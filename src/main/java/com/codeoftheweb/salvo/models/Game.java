package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.Nullable;
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

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private List<Score> scores = new ArrayList<>();

    @Transient
    private String gameState;

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
        this.setGameState(logged);
        dto.put("gameState", this.getGameState());
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

    public String getGameState() {
        return gameState;
    }

    public String setGameState(GamePlayer logged) {
        if (logged == null) {
            gameState = "PLACESHIPS";
            return gameState;
        }
        if (logged.noShips()) {
            gameState = "PLACESHIPS";
            return gameState;
        }
        if (playerIsMissing()) {
            gameState = "WAITINGFOROPP";
            return gameState;
        }
//        Si llega hasta aca es porque tiene un oponente
        GamePlayer opponent = logged.getGamePlayerOpponet();
        if (opponent.noShips() || logged.getNumberOfSalvos() > opponent.getNumberOfSalvos()) {
            gameState = "WAIT";
            return gameState;
        }
        if (logged.getNumberOfSalvos() == opponent.getNumberOfSalvos()) {
//            TODO: pasarle el turno para contar los salvos
//            if (logged.shipsAreSunk(opponent) && opponent.shipsAreSunk(logged)) {
//                gameState = "TIE";
//                return gameState;
//            }
//            if (logged.shipsAreSunk(opponent)) {
//                gameState = "LOST";
//                return gameState;
//            }
//            if (opponent.shipsAreSunk(logged)) {
//                gameState = "WON";
//                return gameState;
//            }
            if (logged.shipsAreSunk2() && opponent.shipsAreSunk2()) {
                gameState = "TIE";
                return gameState;
            }
            if (logged.shipsAreSunk2() ) {
                gameState = "LOST";
                return gameState;
            }
            if (opponent.shipsAreSunk2()) {
                gameState = "WON";
                return gameState;
            }
            System.out.println("logged " + logged.shipsAreSunk2());
            System.out.println("opponent " + opponent.shipsAreSunk2());
        }
        gameState = "PLAY";
        return gameState;
    }

    private boolean playerIsMissing() {
        return getGamePlayers().size() < 2;
    }

    private boolean shipsBothGamePlayerPlaced() {
        return !getGamePlayers()
                .stream()
                .allMatch(gamePlayer -> gamePlayer.getShips()
                        .isEmpty());
    }

    private long convertDateToMilliseconds() {
        return this.getCreated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public void addScore(Score score1) {
        scores.add(score1);
    }

    public GamePlayer getGamePlayerOpponet(GamePlayer gamePlayer) {
//        cuando el gamePlayer es null creo un gamePlayer para que no rompa el frontend pero este nuevo gamePlayer no se guarda en la bd
        return getGamePlayers()
                .stream()
                .filter(gp -> gp.getId() != gamePlayer.getId())
                .findFirst()
                .orElse(new GamePlayer(gamePlayer.getGame()));
    }

    public Integer getNumberGamePlayers() {
        return getGamePlayers().size();
    }
}

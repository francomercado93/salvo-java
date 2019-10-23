package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    private BigDecimal score;

    private LocalDateTime finishDate;

    public Score() {
    }

    public Score(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.setScoreGamePlayer();
    }

    public Score(Game game, Player player, BigDecimal score) {
        this.game = game;
        this.player = player;
        this.score = score;
        this.setScoreGamePlayer();
        this.finishDate = game.getCreated().plusMinutes(30);
    }

    public void setScoreGamePlayer() {
        game.addScore(this);
        player.addScore(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public Object makeDTOScore() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", player.getId());
        dto.put("score", score);
        dto.put("finishDate", finishDate);
        return dto;
    }
}

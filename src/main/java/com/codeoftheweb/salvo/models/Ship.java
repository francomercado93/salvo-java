package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ElementCollection  //Crea una nueva tabla que tiene las celdas y el id del barco
    @Column(name = "cell")    //Cambia el nombre de la columna de cells a cell
    /*Cambiar por set ?*/
    private List<String> cells = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayerID")
    private GamePlayer gamePlayer;

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    private String type;

    public Ship() {
    }

    public Ship(String shipType) {
        type = shipType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addLocations(List<String> locations) {
        cells.addAll(locations);
    }

    public Object makeDTOShip() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", this.type);
        dto.put("locations", this.cells);
        return dto;
    }

    public long length() {
        return cells.stream().count();
    }

    /*
    public Boolean isSunk(Set<Salvo> salvoes) {
        return this.cells.stream().allMatch(cell -> salvoes.cells)
    }

     */
}
package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    /*ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayerID")
    private GamePlayer gamePlayer;
*/
    @ElementCollection  //Crea una nueva tabla que tiene las celdas y el id del barco
    @Column(name = "cell")    //Cambia el nombre de la columna de cells a cell
    private List<String> cells = new ArrayList<>();

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

    /*public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }*/

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addLocations(List<String> locations) {
        cells.addAll(locations);
    }
}

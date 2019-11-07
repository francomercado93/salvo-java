package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @ElementCollection  //Crea una nueva tabla que tiene las celdas y el id del barco
    @Column(name = "cell")    //Cambia el nombre de la columna de cells a cell
    /*Cambiar por set ?*/
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayerID")
    private GamePlayer gamePlayer;
    private Long totalDamage = new Long(0);

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addLocations(List<String> locations) {
        this.locations.addAll(locations);
    }

    public Map<String, Object> makeDTOShip() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", getType());
        dto.put("locations", getLocations());
        return dto;
    }

    public Long getLengthShip() {
        return new Long(getLocations().size());
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public Long getDamage(Salvo salvo) {
        Long damageTurn = getNumberHitsLocationShip(salvo);
        totalDamage += damageTurn;
        System.out.println("player " + getGamePlayer().getPlayer() + "\nturn " + salvo.getTurn() + " total damage ship " + getType() + " " + getTotalDamage());
        System.out.println("\nis sunk? " + this.isSunk2());
        return damageTurn;
    }

    private Long getNumberHitsLocationShip(Salvo salvo) {
        return new Long(getHitsLocationsShip(salvo).size());
    }

    public Long getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(Long totalDamage) {
        this.totalDamage = totalDamage;
    }

    public Set<String> getHitsLocationsShip(Salvo salvo) {
        return getLocations()
                .stream()
                .filter(location -> salvo.getSalvoLocations()
                        .stream()
                        .anyMatch(salvoLocation -> salvoLocation.equals(location)))
                .collect(Collectors.toSet());
    }

    public Set<String> getMissedSalvoes(Salvo salvo) {
        return getLocations()
                .stream()
                .filter(location -> getHitsLocationsShip(salvo)
                        .stream()
                        .anyMatch(hitLocation -> !hitLocation.equals(location)))
                .collect(Collectors.toSet());
    }

    public boolean isSunk(GamePlayer opponent) {
        List<String> salvoLocation = opponent
                .getSalvoes().stream()
                .map(salvo -> salvo.getSalvoLocations())
                .flatMap(locations -> locations.stream())
                .collect(Collectors.toList());
        Long hitsShip = new Long(0);
        for (String salvoLocatio : salvoLocation) {
            if (this.getLocations().contains(salvoLocatio)) {
                hitsShip++;
            }
        }
        return hitsShip.compareTo(this.getLengthShip()) == 0;
    }

    // TODO: probar de nuevo
    public boolean isSunk2() {
//        System.out.println("total damage " + getTotalDamage());
//        System.out.println("length  " + this.getLengthShip());
        return getTotalDamage().compareTo(this.getLengthShip()) == 0;
    }

}

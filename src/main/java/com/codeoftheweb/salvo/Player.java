package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    List<GamePlayer> gamePlayers;

    //constructor vacio
    public Player() {
    }

    //constructor con 3 parametros
    public Player(String _username, String first, String last) {
        this.userName = _username;
        this.firstName = first;
        this.lastName = last;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString() {
        return this.firstName + " " + this.lastName;
    }

    public List<Game> getGames() {
        return gamePlayers.stream().map(gp -> gp.getGame()).collect(Collectors.toList());
    }


}

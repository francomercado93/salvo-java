package com.codeoftheweb.salvo.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Hits {

    private List<Object> self = new ArrayList<>();

    private List<Object> opponent = new ArrayList<>();

    public List<Object> getSelf() {
        return self;
    }

    public void setSelf(List<Object> self) {
        this.self = self;
    }

    public List<Object> getOpponent() {
        return opponent;
    }

    public void setOpponent(List<Object> opponent) {
        this.opponent = opponent;
    }

    public Object makeDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("self", this.getSelf());
        dto.put("opponent", this.getOpponent());
        return dto;
    }
}

package com.org.insurance.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
public class Derivative implements Serializable {

    private final UUID id = UUID.randomUUID();
    @Setter
    private String name;
    @Setter
    private List<Obligation> obligations;

    public Derivative() {}

    public Derivative(String name) {
        this.name = name;
    }

}

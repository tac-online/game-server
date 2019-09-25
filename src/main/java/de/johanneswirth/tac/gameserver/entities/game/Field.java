package de.johanneswirth.tac.gameserver.entities.game;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class Field implements Serializable {
    //TODO: validate if properties fit together
    @Valid
    private Marble occupier;
    @NotNull
    @Min(0)
    @Max(63)
    private int number;
    @NotNull
    @Min(0)
    @Max(3)
    private int player;
    @NotNull
    private boolean startField = false;
    @NotNull
    private boolean homeField = false;

    public Field() {

    }

    public Field(int number, int player, boolean start, boolean home) {
        this.number = number;
        this.player = player;
        this.startField = start;
        this.homeField = home;
    }

    public Marble getOccupier() {
        return occupier;
    }

    public void setOccupier(Marble occupier) {
        this.occupier = occupier;
    }

    public boolean isStartField() {
        return startField;
    }

    public boolean isHomeField() {
        return homeField;
    }

    public int getNumber() {
        return number;
    }

    public int getPlayer() {
        return player;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void setStartField(boolean startField) {
        this.startField = startField;
    }

    public void setHomeField(boolean homeField) {
        this.homeField = homeField;
    }
}

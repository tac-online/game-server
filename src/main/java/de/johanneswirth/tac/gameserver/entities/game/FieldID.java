package de.johanneswirth.tac.gameserver.entities.game;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

public class FieldID implements Serializable {
    @NotNull
    @Min(0)
    @Max(63)
    private int number;
    @NotNull
    @Min(0)
    @Max(3)
    private int player;
    @NotNull
    private boolean homeField;

    public FieldID() {

    }

    public FieldID(int number, int player, boolean homeField) {
        this.number = number;
        this.player = player;
        this.homeField = homeField;
    }

    public FieldID(Field field) {
        this.number = field.getNumber();
        this.player = field.getPlayer();
        this.homeField = field.isHomeField();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public boolean isHomeField() {
        return homeField;
    }

    public void setHomeField(boolean homeField) {
        this.homeField = homeField;
    }

    public boolean valid() {
        boolean p = player < 4 && player >= 0;
        if (homeField) {
            return p && number >= 0 && number < 4;
        } else {
            return p && number >= 0 && number < 64;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldID)) return false;
        FieldID fieldID = (FieldID) o;
        return number == fieldID.number &&
                player == fieldID.player &&
                homeField == fieldID.homeField;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (homeField) builder.append("Home " + player + " ");
        builder.append("Field " + number);
        return builder.toString();
    }
}

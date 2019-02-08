package de.johanneswirth.tac.gameserver.entities.game;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

public class Marble implements Serializable {
    private int owner;
    private boolean moved;
    private boolean locked;

    public Marble() {

    }

    public Marble(int owner) {
        this.owner = owner;
        this.moved = false;
        this.locked = false;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    @XmlTransient
    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Marble of Player " + owner);
        if (moved) builder.append(" [moved]");
        if (locked) builder.append(" [locked]");
        return builder.toString();
    }
}

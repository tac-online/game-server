package de.johanneswirth.tac.gameserver.entities.game;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

public class Base implements Serializable {
    private List<Marble> occupiers;
    private int player;

    public Base() {

    }

    public Base(List<Marble> occupiers, int player) {
        this.occupiers = occupiers;
        this.player = player;
    }

    public List<Marble> getOccupiers() {
        return occupiers;
    }

    public void addMarble(Marble occupier) {
        occupiers.add(occupier);
        occupier.setMoved(false);
    }

    public Marble removeMarble() {
        return occupiers.remove(occupiers.size() - 1);
    }

    @XmlTransient
    public boolean isEmpty() {
        return occupiers.isEmpty();
    }

    @XmlTransient
    public boolean isFull() {
        return occupiers.size() == 4;
    }

    public int getPlayer() {
        return player;
    }

    public void setOccupiers(List<Marble> occupiers) {
        this.occupiers = occupiers;
    }

    public void setPlayer(int player) {
        this.player = player;
    }
}

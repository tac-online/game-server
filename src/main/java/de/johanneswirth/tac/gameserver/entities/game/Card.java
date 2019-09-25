package de.johanneswirth.tac.gameserver.entities.game;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.function.BiFunction;

public enum Card implements Serializable {
    One(1, Cards::oneAllowed),
    Two(2, Cards::regularAllowed),
    Three(3, Cards::regularAllowed),
    Four(-4, Cards::fourAllowed),
    Five(5, Cards::regularAllowed),
    Six(6, Cards::regularAllowed),
    Seven(7, Cards::sevenAllowed),
    Eight(8, Cards::eightAllowed),
    Nine(9, Cards::regularAllowed),
    Ten(10, Cards::regularAllowed),
    Twelve(12, Cards::regularAllowed),
    Thirteen(13, Cards::thirteenAllowed),
    TAC(0, Cards::tacAllowed),
    Trickster(0, Cards::tricksterAllowed),
    Jester(0, Cards::jesterAllowed),
    Angel(0, Cards::angelAllowed),
    Warrior(0, Cards::warriorAllowed),
    Devil(0, Cards::devilAllowed);

    private int distance;
    private BiFunction<Game, Card, Boolean> isAllowed;

    Card(int distance, BiFunction<Game, Card, Boolean> isAllowed) {
        this.distance = distance;
        this.isAllowed = isAllowed;
    }

    @JsonCreator
    public static Card fromValue(String value) {
        return Card.valueOf(value);
    }

    public boolean isAllowed(Game game) {
        return isAllowed.apply(game, this);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return name();
    }
}

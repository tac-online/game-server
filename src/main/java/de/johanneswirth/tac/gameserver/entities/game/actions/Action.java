package de.johanneswirth.tac.gameserver.entities.game.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.johanneswirth.tac.gameserver.entities.game.*;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AngelMoveAction.class),
        @JsonSubTypes.Type(value = AngelOpenAction.class),
        @JsonSubTypes.Type(value = DevilAction.class),
        @JsonSubTypes.Type(value = DevilCardAction.class),
        @JsonSubTypes.Type(value = DiscardAction.class),
        @JsonSubTypes.Type(value = JesterAction.class),
        @JsonSubTypes.Type(value = MissAction.class),
        @JsonSubTypes.Type(value = MoveAction.class),
        @JsonSubTypes.Type(value = MoveBackAction.class),
        @JsonSubTypes.Type(value = OpenAction.class),
        @JsonSubTypes.Type(value = RegularMoveAction.class),
        @JsonSubTypes.Type(value = RegularOpenAction.class),
        @JsonSubTypes.Type(value = SevenAction.class),
        @JsonSubTypes.Type(value = SevenMovePart.class),
        @JsonSubTypes.Type(value = TACAction.class),
        @JsonSubTypes.Type(value = TricksterAction.class),
        @JsonSubTypes.Type(value = WarriorAction.class)
})
public abstract class Action implements Serializable {
    private Card card;

    public Action() {

    }

    public Action(Card card) {
        this.card = card;
    }

    public abstract boolean isAllowed(Game game);
    public abstract void doAction(Game game);
    public abstract void undoAction(Game game);
    public abstract boolean valid();

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName() + "\n");
        builder.append("Card: " + card + "\n");
        return builder.toString();
    }
}

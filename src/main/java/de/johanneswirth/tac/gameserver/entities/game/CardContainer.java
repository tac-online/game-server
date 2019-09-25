package de.johanneswirth.tac.gameserver.entities.game;

import javax.validation.constraints.NotNull;

public class CardContainer {
    @NotNull
    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}

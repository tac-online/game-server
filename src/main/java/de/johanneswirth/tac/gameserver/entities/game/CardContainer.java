package de.johanneswirth.tac.gameserver.entities.game;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CardContainer {
    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}

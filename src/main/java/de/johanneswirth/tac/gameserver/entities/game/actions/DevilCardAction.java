package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DevilCardAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevilCardAction.class);

    @Valid
    @NotNull
    private Card devilCard;

    public DevilCardAction() {
    }

    public DevilCardAction(Card card) {
        super(card);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.debug("Invalid Action");
            return false;
        }
        if (game.devilCardAllowed(devilCard)) {
            return true;
        } else {
            LOGGER.debug("DevilCardAction not allowed");
            return false;
        }
    }

    @Override
    public void doAction(Game game) {
        game.setDevilCard(devilCard);
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        // TODO
    }

    @Override
    public boolean valid() {
        return getCard() == Card.Devil;
    }

    public void setDevilCard(Card devilCard) {
        this.devilCard = devilCard;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("DevilCard: " + devilCard);
        return builder.toString();
    }
}

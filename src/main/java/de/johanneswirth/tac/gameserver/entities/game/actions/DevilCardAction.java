package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class DevilCardAction extends Action {
    private Card devilCard;

    public DevilCardAction() {
    }

    public DevilCardAction(Card card) {
        super(card);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        if (game.devilCardAllowed(devilCard)) {
            return true;
        } else {
            LOGGER.log(Level.INFO, "DevilCardAction not allowed");
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

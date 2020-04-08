package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscardAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscardAction.class);

    public DiscardAction() {
    }

    public DiscardAction(Card card) {
        super(card);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.info("Invalid Action");
            return false;
        }
        // the current player should have no possible moves
        // exception: he has to miss a turn because of an eight-card
        if (!game.playerHasPossibleMove() || game.isMissTurn()) {
            return true;
        } else {
            LOGGER.info("Discarding not allowed");
            return false;
        }
    }

    @Override
    public void doAction(Game game) {
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
    }

    @Override
    public boolean valid() {
        return true;
    }
}

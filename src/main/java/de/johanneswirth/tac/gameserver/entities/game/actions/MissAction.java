package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MissAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(MissAction.class);

    public MissAction() {
    }

    public MissAction(Card card) {
        super(card);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.debug("Invalid Action");
            return false;
        }
        // Miss at end of a round is not allowed; must be used as move or discarded
        if (game.isEndOfRound()) {
            LOGGER.debug("Cannot use as miss if last card of round");
            return false;
        }
        // player has open marbles on track
        if (game.hasOpenMarbles(game.getTurn(), false)) {
            return true;
        } else {
            LOGGER.debug("Player has no open marbles");
            return false;
        }
    }

    @Override
    public void doAction(Game game) {
        game.setMissTurn(true);
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        game.setMissTurn(false);
    }

    @Override
    public boolean valid() {
        return getCard() == Card.Eight;
    }
}

package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class MissAction extends Action {
    public MissAction() {
    }

    public MissAction(Card card) {
        super(card);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        // Miss at end of a round is not allowed; must be used as move or discarded
        if (game.isEndOfRound()) {
            LOGGER.log(Level.INFO, "Cannot use as miss if last card of round");
            return false;
        }
        // player has open marbles on track
        if (game.hasOpenMarbles(game.getTurn(), false)) {
            return true;
        } else {
            LOGGER.log(Level.INFO, "Player has no open marbles");
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

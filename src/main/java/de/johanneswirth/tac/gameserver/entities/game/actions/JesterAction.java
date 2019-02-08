package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class JesterAction extends Action {
    public JesterAction() {
    }

    public JesterAction(Card card) {
        super(card);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        // jester is always allowed
        return true;
    }

    @Override
    public void doAction(Game game) {
        // change cards
        game.doJester();
    }

    @Override
    public void undoAction(Game game) {
        throw new RuntimeException("Undo should never happen on Jester-Action!");
    }

    @Override
    public boolean valid() {
        return getCard() == Card.Jester;
    }
}

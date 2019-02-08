package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class DevilAction extends Action {
    private Action action;

    public DevilAction() {
    }

    public DevilAction(Card card) {
        super(card);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        // check if action is allowed for the next player
        if (game.devilAllowed(action)) {
            return true;
        } else {
            LOGGER.log(Level.INFO, "Devil Action not allowed");
            return false;
        }
    }

    @Override
    public void doAction(Game game) {
        // execute action; the game.turn will be increased before execution for it to work
        game.doDevilAction(action);
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        // TODO
    }

    @Override
    public boolean valid() {
        return action.valid() && getCard() == Card.Devil;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("Action:\n");
        builder.append(action.toString().replaceAll("(?m)^", "  "));
        return builder.toString();
    }
}

package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class TACAction extends Action {
    private Action action;

    public TACAction() {
    }

    public TACAction(Card card, Action action) {
        super(card);
        this.action = action;
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        if (game.getLastAction() == null) {
            LOGGER.log(Level.INFO, "No Last Action");
            return false;
        }
        // undo last action
        game.rollbackState();
        // check if action matches last played card and action is allowed
        boolean allowed = game.getLastCard() == action.getCard() && action.isAllowed(game);
        // restore gamestate
        game.getLastAction().doAction(game);
        if (allowed) {
            return true;
        } else {
            LOGGER.log(Level.INFO, "Action not allowed");
            return false;
        }
    }

    @Override
    public void doAction(Game game) {
        // undo last action and execute replacement action
        game.rollbackState();
        action.doAction(game);
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        // TODO
    }

    @Override
    public boolean valid() {
        return action.valid() && getCard() == Card.TAC;
    }

    public Action getAction() {
        return action;
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

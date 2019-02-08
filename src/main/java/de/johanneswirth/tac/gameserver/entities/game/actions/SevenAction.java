package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.List;
import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class SevenAction extends Action {
    private List<SevenMovePart> actions;

    public SevenAction() {
    }

    public SevenAction(Card card, List<SevenMovePart> actions) {
        super(card);
        this.actions = actions;
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        // maximum of 7 action-parts
        if (actions.size() > 7) {
            LOGGER.log(Level.INFO, "Too many parts");
            return false;
        }
        // the distance moved till now
        int distance = 0;
        boolean allowed = true;
        // go through all action-parts
        for (int i = 0; i < actions.size(); i++) {
            SevenMovePart action = actions.get(i);
            LOGGER.log(Level.INFO, "Checking part " + action + " from " + action.getSrcID() + " to " + action.getDestID());
            int dist = action.getDistance(game);
            if (action.getSrcID().isHomeField()) {
                LOGGER.log(Level.INFO, "Src is HomeField");
                // cant move out of home
                if (!action.getDestID().isHomeField()) {
                    LOGGER.log(Level.INFO, "Moving out of HomeField is not allowed");
                    allowed = false;
                }
                // cant move locked marble
                if (game.getBoard().getField(action.getDestID()).getOccupier().isLocked()) {
                    LOGGER.log(Level.INFO, "Marble is already locked");
                    allowed = false;
                }
            } else {
                LOGGER.log(Level.INFO, "Src is TrackField");
                // moving back on track not allowed
                if (dist <= 0) {
                    LOGGER.log(Level.INFO, "Only forward moves allowed");
                    allowed = false;
                }
            }
            // add absolute value to total distance
            distance += Math.abs(dist);
            LOGGER.log(Level.INFO, "Total Distance is now " + distance);
            // check if maximum distance was exceeded
            if (distance > 7) {
                LOGGER.log(Level.INFO, "Total Distance cannot be greater than 7");
                allowed = false;
            }
            // check if action is allowed
            if (!action.isAllowed(game)) {
                LOGGER.log(Level.INFO, "Action is not allowed");
                allowed = false;
            }
            if (allowed) {
                LOGGER.log(Level.INFO, "Everything ok, executing part");
                // if still allowed, execute action in preparation for next check
                action.doAction(game);
            } else {
                // if not undo all actions, then leave
                LOGGER.log(Level.INFO, "Not allowed, rolling back previous parts");
                for (int j = 0; j < i; j++) {
                    actions.get(j).undoAction(game);
                }
                return false;
            }
        }
        // rollback all actions
        LOGGER.log(Level.INFO, "Rolling back all parts");
        for (int j = 0; j < actions.size(); j++) {
            actions.get(j).undoAction(game);
        }
        LOGGER.log(Level.INFO, "Total Distance is " + distance);
        return distance == 7;
    }

    @Override
    public void doAction(Game game) {
        // execute all actions
        for (SevenMovePart action : actions) {
            action.doAction(game);
        }
        // update locked-status of all marbles
        for (int i = 3; i >= 0; i--) {
            Field homefield = game.getBoard().getHomes()[game.getTurn()][i];
            if (homefield.getOccupier() != null) {
                homefield.getOccupier().setLocked(true);
            } else {
                break;
            }
        }
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        // undo actions (in reverse order)
        for (int i = actions.size() - 1; i >= 0; i++) {
            actions.get(i).undoAction(game);
        }
    }

    @Override
    public boolean valid() {
        return actions.size() <= 7 && actions.stream().allMatch(action -> action.valid()) && getCard() == Card.Seven;
    }

    public List<SevenMovePart> getActions() {
        return actions;
    }

    public void setActions(List<SevenMovePart> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("Parts:\n");
        actions.forEach(action -> builder.append(action.toString().replaceAll("(?m)^", "  ") + "\n"));
        return builder.toString();
    }
}

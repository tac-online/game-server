package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class RegularMoveAction extends MoveAction {

    public RegularMoveAction() {

    }

    public RegularMoveAction(Card card, FieldID srcID, FieldID destID) {
        super(card, srcID, destID);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        Board board = game.getBoard();
        Field src = board.getField(getSrcID());
        if (src.getOccupier() == null) {
            LOGGER.log(Level.INFO, "No marble on source field");
            return false;
        }
        int player = src.getOccupier().getOwner();
        // check if marble belongs to current player
        if (player != game.getTurn()) {
            LOGGER.log(Level.INFO, "Marble does not belong to player in turn");
            return false;
        }
        return super.allowed(game, getCard().getDistance());
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        super.undoAction(game);
    }

    @Override
    public boolean valid() {
        int dist = getCard().getDistance();
        return super.valid() && dist > 0 && dist <= 13 && dist != 7;
    }
}

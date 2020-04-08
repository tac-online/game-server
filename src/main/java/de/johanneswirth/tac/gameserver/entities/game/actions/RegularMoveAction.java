package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RegularMoveAction extends MoveAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegularMoveAction.class);

    public RegularMoveAction() {

    }

    public RegularMoveAction(Card card, FieldID srcID, FieldID destID) {
        super(card, srcID, destID);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.info("Invalid Action");
            return false;
        }
        Board board = game.getBoard();
        Field src = board.getField(getSrcID());
        if (src.getOccupier() == null) {
            LOGGER.info("No marble on source field");
            return false;
        }
        int player = src.getOccupier().getOwner();
        // check if marble belongs to current player
        if (player != game.getTurn()) {
            LOGGER.info("Marble does not belong to player in turn");
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

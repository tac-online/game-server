package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveBackAction extends MoveAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveBackAction.class);

    public MoveBackAction() {
    }

    public MoveBackAction(Card card, FieldID srcID, FieldID destID) {
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
        Field dest = board.getField(getDestID());
        // cant move out of home
        if (src.isHomeField()) {
            LOGGER.info("Moving out of Home is not allowed");
            return false;
        }
        // check if src field contains marble
        if (src.getOccupier() == null) {
            LOGGER.info("Source does not contain Marble");
            return false;
        }
        int player = src.getOccupier().getOwner();
        // check if marble belongs to current player
        if (player != game.getTurn()) {
            LOGGER.info("Marble does not belong to player in turn");
            return false;
        }
        // only move into house, if marble was already moved before
        if (!src.getOccupier().isMoved() && dest.isHomeField()) {
            LOGGER.info("Only moved Marbles can be moved to House");
            return false;
        }
        // check move
        return allowed(game, -4);
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        super.undoAction(game);
    }

    @Override
    public boolean valid() {
        return super.valid() && getCard() == Card.Four;
    }
}

package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AngelMoveAction extends MoveAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AngelMoveAction.class);

    public AngelMoveAction() {
    }

    public AngelMoveAction(Card card, FieldID srcID, FieldID destID) {
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
        int player = src.getOccupier().getOwner();
        // check if moved player has no marble in base
        if (!board.getBases()[player].isEmpty()) {
            LOGGER.info("Player has a marble in Base");
            return false;
        }
        // check if src field contains marble
        if (src.getOccupier() == null) {
            LOGGER.info("Source does not contain marble");
            return false;
        }
        // check if marble belongs to next player
        if (player != (game.getTurn() + 1) % 4) {
            LOGGER.info("Marble does not belong to next player");
            return false;
        }
        // check if move is correct
        return allowed(game, getDistance(game));
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) {
            return;
        }
        super.undoAction(game);
    }

    @Override
    public boolean valid() {
        return super.valid() && getCard() == Card.Angel;
    }
}

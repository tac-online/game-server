package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class AngelMoveAction extends MoveAction {

    public AngelMoveAction() {
    }

    public AngelMoveAction(Card card, FieldID srcID, FieldID destID) {
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
        int player = src.getOccupier().getOwner();
        // check if moved player has no marble in base
        if (!board.getBases()[player].isEmpty()) {
            LOGGER.log(Level.INFO, "Player has a marble in Base");
            return false;
        }
        // check if src field contains marble
        if (src.getOccupier() == null) {
            LOGGER.log(Level.INFO, "Source does not contain marble");
            return false;
        }
        // check if marble belongs to next player
        if (player != (game.getTurn() + 1) % 4) {
            LOGGER.log(Level.INFO, "Marble does not belong to next player");
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

package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class SevenMovePart extends MoveAction {


    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        Board board = game.getBoard();
        Field src = board.getField(getSrcID());
        // check if src field contains marble
        if (src.getOccupier() == null) {
            LOGGER.log(Level.INFO, "Source contains no Marble");
            return false;
        }
        int player = src.getOccupier().getOwner();
        // check if marble belongs to current player
        if (player != game.getTurn()) {
            LOGGER.log(Level.INFO, "Marble does not belong to player in turn");
            return false;
        }
        // check if move allowed
        return allowed(game, getDistance(game));
    }

    @Override
    public boolean valid() {
        return super.valid() && getCard() == Card.Seven;
    }
}

package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SevenMovePart extends MoveAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(SevenMovePart.class);

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.info("Invalid Action");
            return false;
        }
        Board board = game.getBoard();
        Field src = board.getField(getSrcID());
        // check if src field contains marble
        if (src.getOccupier() == null) {
            LOGGER.info("Source contains no Marble");
            return false;
        }
        int player = src.getOccupier().getOwner();
        // check if marble belongs to current player
        if (player != game.getTurn()) {
            LOGGER.info("Marble does not belong to player in turn");
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

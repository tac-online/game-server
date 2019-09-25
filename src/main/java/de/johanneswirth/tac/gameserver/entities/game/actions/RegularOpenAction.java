package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RegularOpenAction extends OpenAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegularOpenAction.class);

    public RegularOpenAction() {
    }

    public RegularOpenAction(Card card, int baseNumber) {
        super(card, baseNumber);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.debug("Invalid Action");
            return false;
        }
        Base base = game.getBoard().getBases()[getBaseNumber()];
        Field field = game.getBoard().getTrackField(16 * getBaseNumber());
        // check if field is a startfield
        if (!field.isStartField()) {
            LOGGER.debug("Destination is no startfield");
            return false;
        }
        // check if player has marble in base
        if (base.isEmpty()) {
            LOGGER.debug("No marble in base");
            return false;
        }
        // the base and startfield must belong to the current player
        if (base.getPlayer() != game.getTurn() || field.getPlayer() != game.getTurn()) {
            LOGGER.debug("Base or startfield do not belong to player in turn");
            return false;
        }
        return true;
    }

    @Override
    public boolean valid() {
        return super.valid() && (getCard() == Card.One || getCard() == Card.Thirteen);
    }
}

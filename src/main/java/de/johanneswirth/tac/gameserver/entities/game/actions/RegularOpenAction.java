package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class RegularOpenAction extends OpenAction {

    public RegularOpenAction() {
    }

    public RegularOpenAction(Card card, int baseNumber) {
        super(card, baseNumber);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        Base base = game.getBoard().getBases()[getBaseNumber()];
        Field field = game.getBoard().getTrackField(16 * getBaseNumber());
        // check if field is a startfield
        if (!field.isStartField()) {
            LOGGER.log(Level.INFO, "Destination is no startfield");
            return false;
        }
        // check if player has marble in base
        if (base.isEmpty()) {
            LOGGER.log(Level.INFO, "No marble in base");
            return false;
        }
        // the base and startfield must belong to the current player
        if (base.getPlayer() != game.getTurn() || field.getPlayer() != game.getTurn()) {
            LOGGER.log(Level.INFO, "Base or startfield do not belong to player in turn");
            return false;
        }
        return true;
    }

    @Override
    public boolean valid() {
        return super.valid() && (getCard() == Card.One || getCard() == Card.Thirteen);
    }
}

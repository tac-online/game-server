package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.Base;
import de.johanneswirth.tac.gameserver.entities.game.Card;
import de.johanneswirth.tac.gameserver.entities.game.Field;
import de.johanneswirth.tac.gameserver.entities.game.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AngelOpenAction extends OpenAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AngelOpenAction.class);

    public AngelOpenAction() {
    }

    public AngelOpenAction(Card card, int baseNumber) {
        super(card, baseNumber);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.debug("Invalid Action");
            return false;
        }
        Base base = game.getBoard().getBases()[getBaseNumber()];
        Field field = game.getBoard().getTrackField(getBaseNumber() * 16);
        // field should be a startfield
        if (!field.isStartField()) {
            LOGGER.debug("Destination is no StartField");
            return false;
        }
        // there must be a marble in the base
        if (base.isEmpty()) {
            LOGGER.debug("Base is empty");
            return false;
        }
        // the base and startfield must belong to the next player
        if (base.getPlayer() != (game.getTurn() + 1) % 4 || field.getPlayer() != (game.getTurn() + 1) % 4) {
            LOGGER.debug("Base or StartField do not belong to next player");
            return false;
        }
        return true;
    }

    @Override
    public boolean valid() {
        return super.valid() && getCard() == Card.Angel;
    }
}

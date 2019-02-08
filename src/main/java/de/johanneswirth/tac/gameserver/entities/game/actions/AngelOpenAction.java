package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.Base;
import de.johanneswirth.tac.gameserver.entities.game.Card;
import de.johanneswirth.tac.gameserver.entities.game.Field;
import de.johanneswirth.tac.gameserver.entities.game.Game;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class AngelOpenAction extends OpenAction {

    public AngelOpenAction() {
    }

    public AngelOpenAction(Card card, int baseNumber) {
        super(card, baseNumber);
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        Base base = game.getBoard().getBases()[getBaseNumber()];
        Field field = game.getBoard().getTrackField(getBaseNumber() * 16);
        // field should be a startfield
        if (!field.isStartField()) {
            LOGGER.log(Level.INFO, "Destination is no StartField");
            return false;
        }
        // there must be a marble in the base
        if (base.isEmpty()) {
            LOGGER.log(Level.INFO, "Base is empty");
            return false;
        }
        // the base and startfield must belong to the next player
        if (base.getPlayer() != (game.getTurn() + 1) % 4 || field.getPlayer() != (game.getTurn() + 1) % 4) {
            LOGGER.log(Level.INFO, "Base or StartField do not belong to next player");
            return false;
        }
        return true;
    }

    @Override
    public boolean valid() {
        return super.valid() && getCard() == Card.Angel;
    }
}

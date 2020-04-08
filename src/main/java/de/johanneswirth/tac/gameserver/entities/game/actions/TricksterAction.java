package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TricksterAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(TricksterAction.class);

    @Valid
    @NotNull
    private FieldID firstID;
    @Valid
    @NotNull
    private FieldID secondID;

    public TricksterAction() {
    }

    public TricksterAction(Card card, FieldID firstID, FieldID secondID) {
        super(card);
        this.firstID = firstID;
        this.secondID = secondID;
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.info("Invalid Action");
            return false;
        }
        Board board = game.getBoard();
        Field first = board.getField(firstID);
        Field second = board.getField(secondID);
        // check if both field contain marbles
        if (first.getOccupier() == null) {
            LOGGER.info("Source does not contain marble");
            return false;
        }
        if (second.getOccupier() == null) {
            LOGGER.info("Destination does not contain marble");
            return false;
        }
        // check if current player has marble on track
        if (game.hasOpenMarbles(game.getTurn(), false)) {
            return true;
        } else {
            LOGGER.info("Player has no open marbles");
            return false;
        }
    }

    @Override
    public void doAction(Game game) {
        doSwap(game);
    }

    private void doSwap(Game game) {
        Board board = game.getBoard();
        Field first = board.getField(firstID);
        Field second = board.getField(secondID);
        Marble temp = first.getOccupier();
        first.setOccupier(second.getOccupier());
        second.setOccupier(temp);
        first.getOccupier().setMoved(true);
        second.getOccupier().setMoved(true);
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        doSwap(game);
    }

    @Override
    public boolean valid() {
        return firstID.valid() && secondID.valid() && getCard() == Card.Trickster && !firstID.isHomeField() && !secondID.isHomeField() && firstID.getNumber() != secondID.getNumber();
    }

    public FieldID getFirstID() {
        return firstID;
    }

    public void setFirstID(FieldID firstID) {
        this.firstID = firstID;
    }

    public FieldID getSecondID() {
        return secondID;
    }

    public void setSecondID(FieldID secondID) {
        this.secondID = secondID;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("First: ");
        builder.append(firstID);
        builder.append("Second: ");
        builder.append(secondID);
        return builder.toString();
    }
}

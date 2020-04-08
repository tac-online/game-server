package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


public abstract class OpenAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAction.class);

    @NotNull
    @Min(0)
    @Max(3)
    private int baseNumber;
    @Valid
    @NotNull
    private FieldID destID;
    private int playercaptured = -1;

    public OpenAction() {
    }

    public OpenAction(Card card, int baseNumber, FieldID destID) {
        super(card);
        this.baseNumber = baseNumber;
        this.destID = destID;
    }

    public boolean allowed(Game game) {
        Field field = game.getBoard().getField(getDestID());
        return field.isStartField() && field.getPlayer() == baseNumber;
    }

    @Override
    public void doAction(Game game) {
        Board board = game.getBoard();
        Base base = board.getBases()[baseNumber];
        Field field = board.getTrackField(16 * baseNumber);
        LOGGER.info("Moving marble from base " + baseNumber + " to " + field);
        // if a marble is being captured
        if (field.getOccupier() != null) {
            // store owner of marble for undo
            playercaptured = field.getOccupier().getOwner();
            LOGGER.info("Marble of player " + playercaptured + " was captured");
            // add captured marble to correct base
            board.getBases()[field.getOccupier().getOwner()].addMarble(field.getOccupier());
        }
        // move a marble of the player from his base to his startfield
        field.setOccupier(base.removeMarble());
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        Board board = game.getBoard();
        Base base = board.getBases()[baseNumber];
        Field field = board.getTrackField(16 * baseNumber);
        // move marble back to base
        base.addMarble(field.getOccupier());
        // if move was a capture move a marble of the captured player from his base to dest, otherwise leave dest empty
        if (playercaptured >= 0) {
            field.setOccupier(board.getBases()[playercaptured].removeMarble());
        } else {
            field.setOccupier(null);
        }
    }

    @Override
    public boolean valid() {
        return destID.valid();
    }

    public int getBaseNumber() {
        return baseNumber;
    }

    public void setBaseNumber(int baseNumber) {
        this.baseNumber = baseNumber;
    }

    public int getPlayerCaptured() { return playercaptured; }

    public FieldID getDestID() {
        return destID;
    }

    public void setDestID(FieldID destID) {
        this.destID = destID;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("Base: " + baseNumber + "\n");
        builder.append("Dest: ");
        builder.append(destID);
        return builder.toString();
    }
}

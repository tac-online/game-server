package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class WarriorAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarriorAction.class);

    @Valid
    @NotNull
    private FieldID srcID;

    @Valid
    @NotNull
    private FieldID destID;
    private int playerCaptured;

    public WarriorAction() {
    }

    public WarriorAction(Card card, FieldID srcID) {
        super(card);
        this.srcID = srcID;
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.info("Invalid Action");
            return false;
        }
        Board board = game.getBoard();
        Field src = board.getField(srcID);
        Field dest = board.getField(destID);
        // check if field contains a marble of the current player
        if (src.getOccupier() == null) {
            LOGGER.info("Source does not contain a Marble");
            return false;
        }
        if (src.getOccupier().getOwner() != game.getTurn()) {
            LOGGER.info("Source does not contain a Marble of the player in turn");
            return false;
        }
        if (dest.getOccupier() == null) {
            LOGGER.info("Dest does not contain a marble");
        }
        return board.pathFree(src, dest);
    }

    @Override
    public void doAction(Game game) {
        Board board = game.getBoard();
        Field src = board.getField(srcID);
        int dist = 1;
        Field dest = board.getField(destID);
        // store owner of captured marble for possible undo
        playerCaptured = dest.getOccupier().getOwner();
        // add marble to correct base
        board.getBases()[dest.getOccupier().getOwner()].addMarble(dest.getOccupier());
        if (dest.getNumber() == src.getNumber()) {
            // only one marble on track -> remove captured marble from field
            dest.setOccupier(null);
        } else {
            // move marble
            dest.setOccupier(src.getOccupier());
            src.setOccupier(null);
            dest.getOccupier().setMoved(true);
        }

    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        Board board = game.getBoard();
        Field src = board.getField(srcID);
        Field dest = board.getField(destID);
        // check if marble captured itself
        if (dest.getNumber() != src.getNumber()) {
            // move back player
            src.setOccupier(dest.getOccupier());
            // move marble of captured player from base to dest field
            dest.setOccupier(board.getBases()[playerCaptured].removeMarble());
        } else {
            // move marble of captured/capturing player to src field
            src.setOccupier(board.getBases()[playerCaptured].removeMarble());
        }
    }

    @Override
    public boolean valid() {
        return srcID.valid() && destID.valid() && !srcID.isHomeField() && !destID.isHomeField() && getCard() == Card.Warrior;
    }

    public FieldID getSrcID() {
        return srcID;
    }

    public void setSrcID(FieldID srcID) {
        this.srcID = srcID;
    }

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
        builder.append("Src: ");
        builder.append(srcID);
        builder.append("Dest: ");
        builder.append(destID);
        return builder.toString();
    }
}

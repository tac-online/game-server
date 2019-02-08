package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public class WarriorAction extends Action {
    private FieldID srcID;
    private int playerCaptured;
    private int numberCaptured;

    public WarriorAction() {
    }

    public WarriorAction(Card card, FieldID srcID) {
        super(card);
        this.srcID = srcID;
    }

    @Override
    public boolean isAllowed(Game game) {
        if (!valid()) {
            LOGGER.log(Level.INFO, "Invalid Action");
            return false;
        }
        Board board = game.getBoard();
        Field src = board.getField(srcID);
        // check if field contains a marble of the current player
        if (src.getOccupier() != null && src.getOccupier().getOwner() == game.getTurn()) {
            return true;
        } else {
            LOGGER.log(Level.INFO, "Source does not contain a Marble of the player in turn");
            return false;
        }
    }

    @Override
    public void doAction(Game game) {
        Board board = game.getBoard();
        Field src = board.getField(srcID);
        int dist = 1;
        Field dest;
        // find next marble
        while (true) {
            dest = board.getTrackField(src.getNumber() + dist);
            if (dest.getOccupier() != null) {
                // store owner of captured marble for possible undo
                playerCaptured = dest.getOccupier().getOwner();
                // store number of dest field
                numberCaptured = (src.getNumber() + dist) % 64;
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
                break;
            }
            dist++;
        }
    }

    @Override
    public void undoAction(Game game) {
        if (!game.getLastAction().equals(this)) return;
        Board board = game.getBoard();
        Field src = board.getField(srcID);
        Field dest = board.getField(new FieldID(numberCaptured, 0, false));
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
        return srcID.valid() && !srcID.isHomeField() && getCard() == Card.Warrior;
    }

    public FieldID getSrcID() {
        return srcID;
    }

    public void setSrcID(FieldID srcID) {
        this.srcID = srcID;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("Src: ");
        builder.append(srcID);
        return builder.toString();
    }
}

package de.johanneswirth.tac.gameserver.entities.game.actions;

import de.johanneswirth.tac.gameserver.entities.game.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public abstract class MoveAction extends Action {
    private FieldID srcID;
    private FieldID destID;
    private int playercaptured = -1;

    public MoveAction() {
    }

    public MoveAction(Card card, FieldID srcID, FieldID destID) {
        super(card);
        this.srcID = srcID;
        this.destID = destID;
    }

    protected boolean allowed(Game game, int dist) {
        Board board = game.getBoard();
        Field src = board.getField(getSrcID());
        Field dest = board.getField(getDestID());
        // check if src field contains marble
        if (src.getOccupier() == null || src.getOccupier().isLocked()) {
            LOGGER.log(Level.INFO, "Source field does not contain marble or marble is locked");
            return false;
        }
        int player = src.getOccupier().getOwner();
        // two cases: src is homeField or not
        if (src.isHomeField()) {
            LOGGER.log(Level.INFO, "Source is a HomeField");
            // cant move out of home
            if (!dest.isHomeField()) {
                LOGGER.log(Level.INFO, "Destination is no HomeField");
                return false;
            }
            // check if move distance matches card
            if (src.getNumber() + dist != dest.getNumber()) {
                LOGGER.log(Level.INFO, "Distance between Source and Destination does not match card value");
                return false;
            }
            // check if all fields on the way are free (including last)
            for (int field = src.getNumber() + 1; field <= dest.getNumber(); field ++) {
                if (board.getHomes()[player][field].getOccupier() != null) {
                    LOGGER.log(Level.INFO, "There is another marble in the way");
                    return false;
                }
            }
            return true;
        } else {
            LOGGER.log(Level.INFO, "Source is on Track");
            // two cases: dest is homeField or not
            int sign = Integer.signum(dist);
            if (dest.isHomeField()) {
                LOGGER.log(Level.INFO, "Destination is a HomeField");
                // check if the home belongs to the player
                if (dest.getPlayer() != player || !src.getOccupier().isMoved()) {
                    LOGGER.log(Level.INFO, "Destination does not belong to player or marble was not yet moved");
                    return false;
                }
                // player has to enter home through his startfield
                Field start = board.getTrackField(player * 16);
                // the distance left for the track (complete distance minus distance in home)
                int trackdist = sign * (Math.abs(dist) - dest.getNumber() - 1);

                // check if distance on track leads from src to the startfield
                if (board.getTrackField(src.getNumber() + trackdist).getNumber() != start.getNumber()) {
                    LOGGER.log(Level.INFO, "Distance between Source and Destination does not match card value");
                    return false;
                }
                // check if all trackfields on the way are free (including startfield)
                for (int field = sign; field != trackdist + sign; field += sign) {
                    if (board.getTrackField(src.getNumber() + field).getOccupier() != null) {
                        LOGGER.log(Level.INFO, "There is another marble in the way (on Track)");
                        return false;
                    }
                }
                // check if all homefields on the way are free (including last)
                for (int field = 0; field <= dest.getNumber(); field ++) {
                    if (board.getHomes()[player][field].getOccupier() != null) {
                        LOGGER.log(Level.INFO, "There is another marble in the way (on a HomeField)");
                        return false;
                    }
                }
                return true;
            } else {
                LOGGER.log(Level.INFO, "Destination is on Track");
                // check if move distance matches card
                if (board.getTrackField(src.getNumber() + dist).getNumber() != dest.getNumber()) {
                    LOGGER.log(Level.INFO, "Distance between Source and Destination does not match card value");
                    return false;
                }
                // check if all fields on the way are free (excluding last)
                for (int field = sign; field != dist; field += sign) {
                    if (board.getTrackField(src.getNumber() + field).getOccupier() != null) {
                        LOGGER.log(Level.INFO, "There is another marble in the way");
                        return false;
                    }
                }
                return true;
            }
        }
    }

    @Override
    public void doAction(Game game) {
        Board board = game.getBoard();
        Field src = board.getField(srcID);
        Field dest = board.getField(destID);
        LOGGER.log(Level.INFO, "Moving marble from " + src + " to " + dest);
        // if a marble is being captured
        if (dest.getOccupier() != null) {
            // store owner of marble for undo
            playercaptured = dest.getOccupier().getOwner();
            LOGGER.log(Level.INFO, "Marble of player " + playercaptured + " was captured");
            // add captured marble to correct base
            board.getBases()[dest.getOccupier().getOwner()].addMarble(dest.getOccupier());
        }
        // move marble
        dest.setOccupier(src.getOccupier());
        src.setOccupier(null);
        dest.getOccupier().setMoved(true);
        // check if marble is locked now (only in home)
        // dont lock if card is seven (-> will be done after last action)
        if (destID.isHomeField() && getCard() != Card.Seven) {
            boolean locked = true;
            // check if all homefields behind dest are already occupied
            for (int i = destID.getNumber() + 1; i < 4; i++) {
                if (board.getHomes()[dest.getOccupier().getOwner()][i].getOccupier() == null) locked = false;
            }
            dest.getOccupier().setLocked(locked);

            if (locked) LOGGER.log(Level.INFO, "Marble is now locked");
        }
    }

    @Override
    public void undoAction(Game game) {
        Board board = game.getBoard();
        Field src = board.getField(srcID);
        Field dest = board.getField(destID);
        // move marble back
        src.setOccupier(dest.getOccupier());
        // if move was a capture move a marble of the captured player from his base to dest, otherwise leave dest empty
        if (playercaptured >= 0) {
            dest.setOccupier(board.getBases()[playercaptured].removeMarble());
        } else {
            dest.setOccupier(null);
        }
    }

    @Override
    public boolean valid() {
        return srcID.valid() && destID.valid();
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

    public int getPlayerCaptured() { return playercaptured; }

    public int getDistance(Game game) {
        Board board = game.getBoard();
        Field src = board.getField(getSrcID());
        Field dest = board.getField(getDestID());
        int distance;
        if (src.isHomeField()) {
            if (dest.isHomeField()) {
                distance = dest.getNumber() - src.getNumber();
            } else {
                distance = Integer.MIN_VALUE;
            }
        } else {
            if (dest.isHomeField()) {
                int trackdist = src.getOccupier().getOwner() * 16 - src.getNumber();
                trackdist = trackdist < -32 ? trackdist + 64 : trackdist > 32 ? trackdist - 64 : trackdist;
                distance = trackdist + dest.getNumber() + 1;
            } else {
                distance = dest.getNumber() - src.getNumber();
            }
        }
        distance = distance < -32 ? distance + 64 : distance > 32 ? distance - 64 : distance;
        return distance;
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

package de.johanneswirth.tac.gameserver.entities.game;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Board implements Serializable {
    @NotNull
    @Valid
    @Size(min=64,max=64)
    private Field[] track;
    @NotNull
    @Valid
    @Size(min=4,max=4)
    private Field[][] homes;
    @NotNull
    @Valid
    @Size(min=4,max=4)
    private Base[] bases;

    public Board() {
        track = new Field[64];
        homes = new Field[4][];
        bases = new Base[4];
    }

    public static Board createNewBoard() {
        Board board = new Board();
        List<Marble> stones;

        for (int i = 0; i < 64; i++) {
            board.track[i] = new Field(i, 0, false, false);
        }
        for (int player = 0; player < 4; player++) {
            board.homes[player] = new Field[4];
            stones = new LinkedList<>();
            for (int i = 0; i < 4; i++) {
                stones.add(new Marble(player));
                board.homes[player][i] = new Field(i, player, false, true);
            }
            board.bases[player] = new Base(stones, player);
            board.track[16 * player] = new Field(16 * player, player, true, false);
        }



        return board;
    }

    public Field[] getTrack() {
        return track;
    }

    public Field[][] getHomes() {
        return homes;
    }

    public Base[] getBases() {
        return bases;
    }

    public Field getField(FieldID field) {
        if (field.isHomeField()) {
            return homes[field.getPlayer()][field.getNumber()];
        } else {
            return track[field.getNumber()];
        }
    }

    public Field getTrackField(int number) {
        number = number % 64;
        number = number >= 0 ? number : number + 64;
        return track[number];
    }

    public boolean pathFree(Field src, Field dest) {
        if (src.isHomeField() || dest.isHomeField()) throw new RuntimeException("Src or Dest is a HomeField");
        int i = src.getNumber() + 1;
        while (i != dest.getNumber()) {
            if (getTrackField(i).getOccupier() != null) return false;
            i = (i+1) % 64;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder.append("Base " + i + ": " + bases[i].getOccupiers().size() + " Marbles\n");
            for (int j = 0; j < 4; j++) {
                if (homes[i][j].getOccupier() != null) builder.append(homes[i][j].getOccupier() + " on HomeField " + j + "\n");
            }
        }

        for (int i = 0; i < 64; i++) {
            if (track[i].getOccupier() != null) builder.append(track[i].getOccupier() + " on TrackField " + i + "\n");
        }
        return builder.toString();
    }
}

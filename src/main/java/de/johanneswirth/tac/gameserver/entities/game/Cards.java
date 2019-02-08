package de.johanneswirth.tac.gameserver.entities.game;

import de.johanneswirth.tac.gameserver.entities.game.actions.*;

import java.util.logging.Level;
import static de.johanneswirth.tac.common.Utils.LOGGER;

public abstract class Cards {

    public static boolean regularAllowed(Game game, Card card) {
        int distance = card.getDistance();
        Field[] home = game.getBoard().getHomes()[game.getTurn()];
        // Iterate over all Marbles not already locked
        for (Field field : game.activeMarbles(game.getTurn(), true)) {
            if (field.isHomeField()) {
                // Only one possible Action, just try it
                LOGGER.log(Level.INFO, "Trying to move inside home");
                Action action = new RegularMoveAction(card, new FieldID(field), new FieldID(home[field.getNumber() + distance]));
                if (action.isAllowed(game)) return true;
            } else {
                // Either just move the distance on track...
                LOGGER.log(Level.INFO, "Trying to move on track");
                Action action = new RegularMoveAction(card, new FieldID(field), new FieldID(game.getBoard().getTrackField(field.getNumber() + distance)));
                if (action.isAllowed(game)) return true;
                // ... or move to player's start field and into home
                LOGGER.log(Level.INFO, "Trying to move from track to house");
                Field start = game.getBoard().getTrackField(game.getTurn() * 16);
                int inhome = distance - ((start.getNumber() - field.getNumber()) % 64) - 1;
                if (inhome >= 0 && inhome <= 3) {
                    action = new RegularMoveAction(card, new FieldID(field), new FieldID(home[inhome]));
                    if (action.isAllowed(game)) return true;
                }
            }
        }
        // no marble can be moved with this card
        return false;
    }

    public static boolean oneAllowed(Game game, Card card) {
        // The One can always be played
        return true;
    }

    public static boolean thirteenAllowed(Game game, Card card) {
        // test whether the player can do an OpenAction
        Action action = new RegularOpenAction(card, game.getTurn());
        // or move the amount (13) with a marble already on track
        return action.isAllowed(game) || regularAllowed(game, card);
    }

    public static boolean sevenAllowed(Game game, Card card) {
        // if the player has a not-locked marble he can do a seven
        return game.hasOpenMarbles(game.getTurn(), true);
    }

    public static boolean tricksterAllowed(Game game, Card card) {
        // if the player has a marble on the track, he can do a trickster
        return game.hasOpenMarbles(game.getTurn(), false);
    }

    public static boolean tacAllowed(Game game, Card card) {
        if (game.getLastAction() == null) return false;
        // check whether the last played card can can be executed by the player
        game.rollbackState();
        boolean allowed = game.getLastCard().isAllowed(game);
        game.getLastAction().doAction(game);
        return allowed;
    }

    public static boolean jesterAllowed(Game game, Card card) {
        // jester is always possible
        return true;
    }

    public static boolean angelAllowed(Game game, Card card) {
        // angel is always possible
        return true;
    }

    public static boolean devilAllowed(Game game, Card card) {
        // devil is always possible
        return true;
    }

    public static boolean warriorAllowed(Game game, Card card) {
        // check whether the play has a marble on the track
        return game.hasOpenMarbles(game.getTurn(), false);
    }

    public static boolean eightAllowed(Game game, Card card) {
        // check whether the play has a marble on the track
        LOGGER.log(Level.INFO, "Checking for open marbles");
        return game.hasOpenMarbles(game.getTurn(), false);
    }

    public static boolean fourAllowed(Game game, Card card) {
        int distance = card.getDistance();
        Field[] home = game.getBoard().getHomes()[game.getTurn()];
        // Iterate over all Marbles not already locked
        for (Field field : game.activeMarbles(game.getTurn(), false)) {
            if (field.isHomeField()) {
                return false;
            } else {
                // Either just move the distance on track...
                Action action = new MoveBackAction(card, new FieldID(field), new FieldID(game.getBoard().getTrackField(field.getNumber() + distance)));
                if (action.isAllowed(game)) return true;
                // ... or move to player's start field and into home
                Field start = game.getBoard().getTrackField(game.getTurn() * 16);
                int inhome = Math.abs(distance + ((field.getNumber() - start.getNumber()) % 64) + 1);
                if (inhome > 0) {
                    action = new MoveBackAction(card, new FieldID(field), new FieldID(home[inhome]));
                    if (action.isAllowed(game)) return true;
                }
            }
        }
        // no marble can be moved with this card
        return false;
    }
}

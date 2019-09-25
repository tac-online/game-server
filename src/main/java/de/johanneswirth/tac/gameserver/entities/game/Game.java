package de.johanneswirth.tac.gameserver.entities.game;

import de.johanneswirth.tac.gameserver.entities.game.actions.Action;
import de.johanneswirth.tac.gameserver.entities.game.actions.DevilCardAction;
import de.johanneswirth.tac.gameserver.entities.game.actions.DiscardAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Game implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    @NotNull
    @Valid
    private Board board;
    @NotNull
    @Size(min=4,max=4)
    @Valid
    private List<Card>[] cards;
    @NotNull
    @Valid
    private Stack<Card> deck;
    @Valid
    private Card lastCard;
    @Valid
    private Card currentCard;
    @Valid
    private Action lastAction;
    @NotNull
    private boolean missTurn;
    @NotNull
    @Min(0)
    @Max(4)
    private int turn;

    @NotNull
    private boolean devilPlayed;
    @Valid
    private Card devilCard;

    public Game() {
        board = Board.createNewBoard();
        cards = new List[4];
        for (int i = 0; i < 4; i++) {
            cards[i] = new LinkedList<>();
        }
        deck = new Stack<>();
        initCards();
        giveCards();
        turn = 0;
    }

    private void initCards() {
        createCards();
        for (int i = 0; i < 10; i++) {
            Collections.shuffle(deck);
        }
    }

    private void giveCards() {
        int numcards = deck.size() == 24 ? 24 : 20;
        for (int i = 0; i < numcards; i++) {
            cards[i % 4].add(deck.pop());
        }
        lastAction = null;
    }

    private void createCards() {
        deck.push(Card.Angel);
        deck.push(Card.Devil);
        deck.push(Card.Jester);
        deck.push(Card.Warrior);
        for (int i = 0; i < 9; i++) {
            deck.push(Card.One);
            deck.push(Card.Thirteen);
            if (i < 8) {
                deck.push(Card.Seven);
            }
            if (i < 7) {
                deck.push(Card.Two);
                deck.push(Card.Three);
                deck.push(Card.Four);
                deck.push(Card.Five);
                deck.push(Card.Six);
                deck.push(Card.Eight);
                deck.push(Card.Nine);
                deck.push(Card.Ten);
                deck.push(Card.Twelve);
                deck.push(Card.Trickster);
            }
            if (i < 4) {
//                deck.push(Card.TAC);
            }
        }
    }

    public boolean playCard(Card card) {
        LOGGER.debug("Trying to play card " + card);
        if (!cards[getTurn()].contains(card)) {
            LOGGER.debug("Card is not on current players hand");
            return false;
        } else if (currentCard != null) {
            LOGGER.debug("CurrentCard already set");
            return false;
        }
        if (isMissTurn()) {
            LOGGER.debug("MissTurn -> throw away card");
            cards[getTurn()].remove(card);
            setMissTurn(false);
            nextPlayer(true);
            return true;
        }
        if (card.isAllowed(this)) {
            LOGGER.debug("Playing the card is allowed");
            cards[getTurn()].remove(card);
            currentCard = card;
            return true;
        }
        if (!playerHasPossibleMove()) {
            LOGGER.debug("Player has no possible moves -> throw away card");
            cards[getTurn()].remove(card);
            nextPlayer(true);
            return true;
        }
        LOGGER.debug("Playing the card is not allowed and player has possible moves");
        return false;
    }

    public boolean doAction(Action action) {
        LOGGER.debug("Trying to do action " + action);
        if (currentCard != action.getCard()) {
            LOGGER.debug("Action does not match with CurrentCard");
            return false;
        }
        if (isMissTurn() && (action.getCard() != Card.TAC || action.getClass() != DiscardAction.class)) {
            LOGGER.debug("MissTurn -> no Action allowed");
            return false;
        }
        if (!action.isAllowed(this)) {
            LOGGER.debug("Action not allowed");
            return false;
        }
        if (action.getCard() != Card.Jester) {
            LOGGER.debug("Action is no JesterAction -> set LastAction and LastCard");
            lastAction = action;
            lastCard = action.getCard();
        }
        LOGGER.debug("Executing action");
        action.doAction(this);
        if (! (action instanceof DevilCardAction)) {
            LOGGER.debug("Action is no DevilCardAction -> increaseTurn");
            nextPlayer(true);
            currentCard = null;
        }
        return true;
    }

    public void simulateAction(Action action) {
        LOGGER.debug("Simulating action " + action);
        action.doAction(this);
    }

    public void setDevilCard(Card card) {
        LOGGER.debug("Setting DevilCard " + card);
        nextPlayer(false);
        if (card.isAllowed(this)) {
            LOGGER.debug("Playing the card is allowed");
            devilPlayed = true;
            devilCard = card;
            previousPlayer();
        } else if (!playerHasPossibleMove()) {
            LOGGER.debug("Next player has no possible moves -> throw away card");
            cards[getTurn()].remove(card);
        } else {
            LOGGER.debug("Playing the card is not allowed");
            previousPlayer();
        }
    }

    public boolean devilCardAllowed(Card card) {
        LOGGER.debug("Checking if DevilCard is allowed: " + card);
        nextPlayer(false);
        if (!cards[getTurn()].contains(card)) {
            LOGGER.debug("Action is no JesterAction -> set LastAction and LastCard");
            previousPlayer();
            return false;
        }
        if (card.isAllowed(this)) {
            LOGGER.debug("Playing the card is allowed");
            previousPlayer();
            return true;
        }
        if (!playerHasPossibleMove()) {
            LOGGER.debug("Next player has no possible moves -> throwing away card allowed");
            previousPlayer();
            return true;
        }
        LOGGER.debug("Playing the card is not allowed");
        previousPlayer();
        return false;
    }

    public void doDevilAction(Action action) {
        LOGGER.debug("Executing DevilAction " + action);
        nextPlayer(false);
        if (action.getCard() != Card.Jester) {
            LOGGER.debug("Action is no JesterAction -> set LastAction and LastCard");
            lastAction = action;
            lastCard = action.getCard();
        }
        LOGGER.debug("Executing action");
        cards[getTurn()].remove(action.getCard());
        action.doAction(this);
        devilPlayed = false;
    }

    public boolean devilAllowed(Action action) {
        LOGGER.debug("Checking if DevilAction is allowed: " + action);
        nextPlayer(false);
        if (!cards[getTurn()].contains(action.getCard())) {
            LOGGER.debug("Next player does not possess the card");
            previousPlayer();
            return false;
        }
        boolean allowed = action.isAllowed(this);
        previousPlayer();
        LOGGER.debug("Checking if devil is allowed: " + allowed);
        return allowed;
    }

    public void rollbackState() {
        LOGGER.debug("Undoing lastAction: " + lastAction);
        lastAction.undoAction(this);
    }

    public boolean playerHasPossibleMove() {
        for (Card card : cards[getTurn()]) {
            if (card.isAllowed(this)) return true;
        }
        return false;
    }

    public void doJester() {
        if (isEndOfRound()) return;
        LOGGER.debug("Jester: Swapping cards");
        List<Card>[] newcards = new List[4];
        for (int i = 0; i < 4; i++) {
            newcards[(i + 3) % 4] = cards[i];
        }
        cards = newcards;
        previousPlayer();
    }

    public List<Field> activeMarbles(int player, boolean home) {
        List<Field> marbles = new LinkedList<>();
        if (home) {
            for (Field homefield : getBoard().getHomes()[player]) {
                if (homefield.getOccupier() != null && !homefield.getOccupier().isLocked())
                    marbles.add(homefield);
            }
        }
        for (Field trackfield : getBoard().getTrack()) {
            if (trackfield.getOccupier() != null && trackfield.getOccupier().getOwner() == player) marbles.add(trackfield);
        }
        return marbles;
    }

    public boolean hasOpenMarbles(int player, boolean home) {
        int openmarbles = 4 - getBoard().getBases()[player].getOccupiers().size();
        for (Field homefield : getBoard().getHomes()[player]) {
            if (home && homefield.getOccupier() != null && !homefield.getOccupier().isLocked()) return true;
            if (homefield.getOccupier() != null) openmarbles --;
        }
        return openmarbles > 0;
    }

    public Card getLastCard() {
        return lastCard;
    }

    public Action getLastAction() {
        return lastAction;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Card>[] getCards() {
        return cards;
    }

    public void setCards(List<Card>[] cards) {
        this.cards = cards;
    }

    public int getTurn() {
        return turn;
    }

    private void nextPlayer(boolean checkGiveCards) {
        turn = (turn + 1) % 4;
        if (checkGiveCards && isEndOfRound()) giveCards();
    }

    private void previousPlayer() {
        turn = (turn + 3) % 4;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public boolean isMissTurn() {
        return missTurn;
    }

    public void setMissTurn(boolean missTurn) {
        this.missTurn = missTurn;
    }

    public boolean isEndOfRound() {
        return cards[getTurn()].isEmpty();
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Players: \n");
        for (int i = 0; i < 4; i++) {
            builder.append("  ");
            builder.append(turn == i ? "X " : "O ");
            builder.append("Player " + i);
            builder.append(": ");
            cards[i].forEach(card -> builder.append(card + ", "));
            builder.append("\n");
        }

        if (currentCard != null) builder.append("Current Card: " + currentCard + "\n");

        if (missTurn) builder.append("Miss Turn\n");

        if (lastAction != null) builder.append("Last Action:\n" + lastAction.toString().replaceAll("(?m)^", "  ") + "\n");

        builder.append("Last Card: " + lastCard + "\n\n");

        builder.append(board);

        return builder.toString();
    }

    public boolean isDevilPlayed() {
        return devilPlayed;
    }

    public Card getDevilCard() {
        return devilCard;
    }
}

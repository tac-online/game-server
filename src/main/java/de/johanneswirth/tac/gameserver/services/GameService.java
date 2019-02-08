package de.johanneswirth.tac.gameserver.services;

import de.johanneswirth.tac.common.MessagingService;
import de.johanneswirth.tac.common.Secured;
import de.johanneswirth.tac.common.Status;
import de.johanneswirth.tac.gameserver.data.NoSQLDatabase;
import de.johanneswirth.tac.gameserver.entities.game.CardContainer;
import de.johanneswirth.tac.gameserver.entities.game.Game;
import de.johanneswirth.tac.gameserver.entities.game.Player;
import de.johanneswirth.tac.gameserver.entities.game.actions.Action;
import org.apache.commons.lang3.ArrayUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static de.johanneswirth.tac.common.ServiceUtils.getResponse;
import static de.johanneswirth.tac.common.Utils.LOGGER;

@Path("game/{gameID}")
public final class GameService {

    private final String METHODS = "GET, PUT, POST";

    private NoSQLDatabase database;

    public GameService(NoSQLDatabase database) {
        this.database = database;
    }

    @GET
    @Secured
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getBoard(@PathParam("gameID") String gameID, @Context SecurityContext context) {
        Status status;
        if (gameID == null)
            return getResponse(Status.ILLEGAL_PARAMETERS, METHODS);
        Game game = database.loadGame(gameID);
        if (game == null)
            status = Status.UNKOWN_GAME;
        else if (game.hasAccessOnGame(context.getUserPrincipal().getName()))
            status = Status.NO_ACCESS;
        else
            status = Status.OK(game);

        return getResponse(status, METHODS);
    }

    @PUT
    @Secured
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response doAction(@PathParam("gameID") String gameID, Action move, @Context SecurityContext context) {
        Status status;
        if (gameID == null || move == null)
            return getResponse(Status.ILLEGAL_PARAMETERS, METHODS);
        Game game = database.loadGame(gameID);
        if (game == null)
            status = Status.UNKOWN_GAME;
        else if (game.hasAccessOnGame(context.getUserPrincipal().getName()))
            status = Status.NO_ACCESS;
        else {
            LOGGER.log(Level.WARNING, "\nAction: \n" + move.toString().replaceAll("(?m)^", "  ") + "\nGame:\n" + game.toString().replaceAll("(?m)^", "  "));
            if (game.doAction(move)) {
                LOGGER.log(Level.WARNING, "\nGame:\n" + game.toString().replaceAll("(?m)^", "  "));
                status = Status.OK(game);
                database.saveGame(game, gameID);
                sendReloadMessage(gameID, game.getPlayers());
            } else {
                status = Status.MOVE_NOT_ALLOWED;
            }
        }

        return getResponse(status, METHODS);
    }

    @POST
    @Secured
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON})
    public Response playCard(@PathParam("gameID") String gameID, CardContainer card, @Context SecurityContext context) {
        Status status;
        if (gameID == null || card == null)
            return getResponse(Status.ILLEGAL_PARAMETERS, METHODS);
        Game game = database.loadGame(gameID);
        if (game == null)
            status = Status.UNKOWN_GAME;
        else if (game.hasAccessOnGame(context.getUserPrincipal().getName()))
            status = Status.NO_ACCESS;
        else {
            LOGGER.log(Level.WARNING, "\nCard: " + card.getCard() + "\nGame:\n" + game.toString().replaceAll("(?m)^", "  "));
            if (game.playCard(card.getCard())) {
                status = Status.OK(game);
                sendReloadMessage(gameID, game.getPlayers());
            } else {
                status = Status.MOVE_NOT_ALLOWED;
            }
            database.saveGame(game, gameID);
        }

        return getResponse(status, METHODS);
    }

    @OPTIONS
    public Response options() {
        return getResponse(null, METHODS);
    }

    private void sendReloadMessage(String gameID, Player[] players) {
        List<String> recipients = Arrays.stream(players).map(player -> player.getName()).collect(Collectors.toList());
        try {
            MessagingService.sendMessage("tac-game-server", "game/", recipients, gameID);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }
}

package de.johanneswirth.tac.gameserver.services;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import de.johanneswirth.tac.common.IStatus;
import de.johanneswirth.tac.common.MessagingService;
import de.johanneswirth.tac.common.Secured;
import de.johanneswirth.tac.gameserver.data.GameDAO;
import de.johanneswirth.tac.gameserver.data.NoSQLDatabase;
import de.johanneswirth.tac.gameserver.entities.game.CardContainer;
import de.johanneswirth.tac.gameserver.entities.game.Game;
import de.johanneswirth.tac.gameserver.entities.game.actions.Action;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.johanneswirth.tac.common.ErrorStatus.*;
import static de.johanneswirth.tac.common.SuccessStatus.OK;

@Path("game/{gameID}")
public final class GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    private GameDAO dao;

    private NoSQLDatabase database;

    public GameService(NoSQLDatabase database, Jdbi jdbi) {
        this.database = database;
        this.dao = jdbi.onDemand(GameDAO.class);
    }

    @GET
    @Secured
    @Produces({ MediaType.APPLICATION_JSON })
    @Valid
    @NotNull
    @Timed
    @ExceptionMetered
    public IStatus<Game> getBoard(@PathParam("gameID") @NotNull @Min(0) Long gameID, @Context SecurityContext context) {
        Game game = database.loadGame(gameID);
        long playerID = Long.parseLong(context.getUserPrincipal().getName());
        if (game == null)
            return UNKOWN_GAME;
        else if (!dao.hasAccessOnGame(playerID, gameID))
            return NO_ACCESS;
        else
            return OK(game, System.currentTimeMillis());
    }

    @PUT
    @Secured
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Valid
    @NotNull
    @Timed
    @ExceptionMetered
    public IStatus<Game> doAction(@PathParam("gameID") @NotNull @Min(0) Long gameID, @NotNull @Valid Action move, @Context SecurityContext context) {
        Game game = database.loadGame(gameID);
        if (game == null)
            return UNKOWN_GAME;
        else if (!dao.hasAccessOnGame(Long.parseLong(context.getUserPrincipal().getName()), gameID))
            return NO_ACCESS;
        else {
            LOGGER.info("\nAction: \n" + move.toString().replaceAll("(?m)^", "  ") + "\nGame:\n" + game.toString().replaceAll("(?m)^", "  "));
            if (game.doAction(move)) {
                long version = System.currentTimeMillis();
                LOGGER.info("\nGame:\n" + game.toString().replaceAll("(?m)^", "  "));
                database.saveGame(game, gameID);
                sendReloadMessage(gameID, context.getUserPrincipal().getName(), version);
                return OK(game, version);
            } else {
                return MOVE_NOT_ALLOWED;
            }
        }
    }

    @POST
    @Secured
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON})
    @Valid
    @NotNull
    @Timed
    @ExceptionMetered
    public IStatus<Game> playCard(@PathParam("gameID") @NotNull @Min(0) Long gameID, @NotNull @Valid CardContainer card, @Context SecurityContext context) {
        Game game = database.loadGame(gameID);
        if (game == null)
            return UNKOWN_GAME;
        else if (!dao.hasAccessOnGame(Long.parseLong(context.getUserPrincipal().getName()), gameID))
            return NO_ACCESS;
        else {
            LOGGER.info("\nCard: " + card.getCard() + "\nGame:\n" + game.toString().replaceAll("(?m)^", "  "));
            if (game.playCard(card.getCard())) {
                long version = System.currentTimeMillis();
                database.saveGame(game, gameID);
                sendReloadMessage(gameID, context.getUserPrincipal().getName(), version);
                return OK(game, version);
            } else {
                return MOVE_NOT_ALLOWED;
            }
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Valid
    @NotNull
    @Timed
    @ExceptionMetered
    public IStatus createGame(@PathParam("gameID") @NotNull @Min(0) Long gameID, @Context SecurityContext securityContext) {
        long userID = Long.parseLong(securityContext.getUserPrincipal().getName());
        boolean success = dao.deleteGame(userID, gameID);
        if (success) {
            database.deleteGame(gameID);
            return OK;
        } else {
            return NO_ACCESS;
        }
    }

    @OPTIONS
    public void options() {}

    private void sendReloadMessage(long gameID, String players, long version) {
        List<String> recipients = Arrays.stream(new String[] {players}).collect(Collectors.toList());
        try {
            MessagingService.sendMessage("tac-game-server", "game/", recipients, "" + gameID, version);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}

package de.johanneswirth.tac.gameserver.services;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import de.johanneswirth.tac.common.IStatus;
import de.johanneswirth.tac.common.Secured;
import de.johanneswirth.tac.gameserver.data.GameDAO;
import de.johanneswirth.tac.gameserver.data.NoSQLDatabase;
import de.johanneswirth.tac.gameserver.entities.game.Game;
import de.johanneswirth.tac.gameserver.entities.game.actions.Action;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import static de.johanneswirth.tac.common.ErrorStatus.NO_ACCESS;
import static de.johanneswirth.tac.common.ErrorStatus.UNKOWN_GAME;
import static de.johanneswirth.tac.common.SuccessStatus.OK;

@Path("game/simulate/{gameID}")
public class SimulateService {

    private GameDAO dao;

    private NoSQLDatabase database;

    public SimulateService(NoSQLDatabase database, Jdbi jdbi) {
        this.database = database;
        this.dao = jdbi.onDemand(GameDAO.class);
    }

    @PUT
    @Secured
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Valid
    @NotNull
    @Timed
    @ExceptionMetered
    public IStatus<Game> simulateAction(@PathParam("gameID") @NotNull @Min(0) Long gameID, @NotNull @Valid Action move, @Context SecurityContext context) {
        Game game = database.loadGame(gameID);
        if (game == null)
            return UNKOWN_GAME;
        else if (!dao.hasAccessOnGame(Long.parseLong(context.getUserPrincipal().getName()), gameID))
            return NO_ACCESS;
        else {
            game.setCurrentCard(null);
            game.simulateAction(move);
            return OK(game, System.currentTimeMillis());
        }
    }

    @OPTIONS
    public void options() {}
}

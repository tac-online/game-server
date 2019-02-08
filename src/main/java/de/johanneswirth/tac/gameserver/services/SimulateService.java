package de.johanneswirth.tac.gameserver.services;

import de.johanneswirth.tac.common.Secured;
import de.johanneswirth.tac.common.Status;
import de.johanneswirth.tac.gameserver.data.NoSQLDatabase;
import de.johanneswirth.tac.gameserver.entities.game.Game;
import de.johanneswirth.tac.gameserver.entities.game.actions.Action;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static de.johanneswirth.tac.common.ServiceUtils.getResponse;

@Path("game/simulate/{gameID}")
public class SimulateService {

    private final String METHODS = "PUT";

    private NoSQLDatabase database;

    public SimulateService(NoSQLDatabase database) {
        this.database = database;
    }

    @PUT
    @Secured
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response simulateAction(@PathParam("gameID") String gameID, Action move, @Context SecurityContext context) {
        Status status;
        if (gameID == null || move == null)
            return getResponse(Status.ILLEGAL_PARAMETERS, METHODS);
        Game game = database.loadGame(gameID);
        if (game == null)
            status = Status.UNKOWN_GAME;
        else if (game.hasAccessOnGame(context.getUserPrincipal().getName()))
            status = Status.NO_ACCESS;
        else {
            game.setCurrentCard(null);
            game.simulateAction(move);
            status = Status.OK(game);
        }

        return getResponse(status, METHODS);
    }

    @OPTIONS
    public Response optionsSimulate() {
        return getResponse(null, METHODS);
    }
}

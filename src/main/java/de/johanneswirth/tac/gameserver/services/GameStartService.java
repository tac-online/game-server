package de.johanneswirth.tac.gameserver.services;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import de.johanneswirth.tac.common.IStatus;
import de.johanneswirth.tac.common.Secured;
import de.johanneswirth.tac.gameserver.data.GameDAO;
import de.johanneswirth.tac.gameserver.data.NoSQLDatabase;
import de.johanneswirth.tac.gameserver.entities.game.Game;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import java.util.List;
import static de.johanneswirth.tac.common.SuccessStatus.OK;

@Path("games")
public class GameStartService {

    private GameDAO dao;

    private NoSQLDatabase database;

    public GameStartService(NoSQLDatabase database, Jdbi jdbi) {
        this.database = database;
        this.dao = jdbi.onDemand(GameDAO.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Valid
    @NotNull
    @Timed
    @ExceptionMetered
    public IStatus<List<Long>> getGames(@Context SecurityContext securityContext) {
        long userID = Long.parseLong(securityContext.getUserPrincipal().getName());
        return OK(dao.getGames(userID), System.currentTimeMillis());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Valid
    @NotNull
    @Timed
    @ExceptionMetered
    public IStatus<Long> createGame(@Context SecurityContext securityContext) {
        long userID = Long.parseLong(securityContext.getUserPrincipal().getName());
        long gameID = dao.createNewGame(userID);
        Game game = new Game();
        database.saveGame(game, gameID);
        return OK(gameID,0);
    }

    @OPTIONS
    public void options() {}
}

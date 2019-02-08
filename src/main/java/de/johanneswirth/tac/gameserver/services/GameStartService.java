package de.johanneswirth.tac.gameserver.services;

import de.johanneswirth.tac.common.Secured;
import de.johanneswirth.tac.common.Status;
import de.johanneswirth.tac.gameserver.data.NoSQLDatabase;
import de.johanneswirth.tac.gameserver.entities.game.Game;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;

import static de.johanneswirth.tac.common.ServiceUtils.getResponse;
import static de.johanneswirth.tac.common.Utils.LOGGER;

@Path("game/start")
public class GameStartService {

    private final String METHODS = "POST";

    private NoSQLDatabase database;

    public GameStartService(NoSQLDatabase database) {
        this.database = database;
    }

    @POST
    @Secured
    @Consumes({ MediaType.APPLICATION_JSON} )
    @Produces({ MediaType.APPLICATION_JSON} )
    public Response reset(List<String> players, @Context SecurityContext context) {
        if (players == null)
            return getResponse(Status.ILLEGAL_PARAMETERS, METHODS);
        if (!players.contains(context.getUserPrincipal().getName()))
            return getResponse(Status.ILLEGAL_PARAMETERS, METHODS);
        Game game = new Game(players.toArray(new String[0]));
        String gameID = calcGameID(players);
        database.saveGame(game, gameID);
        return getResponse(Status.OK(gameID), METHODS);
    }

    private String calcGameID(List<String> players) {
        String base = players.stream().reduce(String::concat).get() + System.currentTimeMillis();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(base.getBytes());
            base = new String(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
        return base;
    }

    @OPTIONS
    public Response options() {
        return getResponse(null, METHODS);
    }
}

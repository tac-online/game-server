package de.johanneswirth.tac.gameserver.services;

import de.johanneswirth.tac.common.Status;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static de.johanneswirth.tac.common.ServiceUtils.getResponse;

@Path("version")
public final class InterfaceVersionService {
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getBoard() {
        Status status = Status.OK(new Version());
        return getResponse(status, "GET");
    }

    @OPTIONS
    public Response options() {
        return getResponse(null, "GET");
    }

    private class Version {
        public int majorVersion = 1;
        public int minorVersion = 1;
    }
}

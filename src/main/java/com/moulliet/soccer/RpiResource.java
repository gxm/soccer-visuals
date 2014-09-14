package com.moulliet.soccer;

import com.moulliet.soccer.soccer.SoccerParser;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/rpi/")
public class RpiResource {
    private static final Logger logger = LoggerFactory.getLogger(RpiResource.class);

    @GET
    @Path("/{year}/{teamCount}")
    public Response getBox(@PathParam("year") int year,
                           @PathParam("teamCount") int teamCount,
                           @QueryParam("callback") final String callback) throws IOException {
        logger.info("loading for " + teamCount);
        JsonNode jsonNodes = SoccerParser.rpis(year, teamCount);
        String entity = "";
        if (callback != null) {
            entity += callback + "(";
        }
        entity += jsonNodes.toString();
        if (callback != null) {
            entity += ");";
        }
        return Response.ok().entity(entity).type("text/javascript").build();
    }
}

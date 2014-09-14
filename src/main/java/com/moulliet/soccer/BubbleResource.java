package com.moulliet.soccer;

import com.moulliet.soccer.ncsoccer.NCSoccerParser;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 *
 */
@Path("/bubble/")
public class BubbleResource {
    private static final Logger logger = LoggerFactory.getLogger(BubbleResource.class);

    @GET
    @Path("/{year}/{teamId}")
    public Response getBox(@PathParam("year") int year,
                           @PathParam("teamId") int teamId,
                           @QueryParam("callback") final String callback) throws IOException {
        logger.info("getting for " + year + " " + teamId);
        Team team = Teams.get(teamId);

        JsonNode jsonNodes = NCSoccerParser.bubbleData(year, team);
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

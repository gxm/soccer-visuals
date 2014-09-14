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

/**
 *
 */
@Path("/bundle/")
public class BundleResource {
    private static final Logger logger = LoggerFactory.getLogger(BundleResource.class);

    @GET
    @Path("/{year}/{teams}")
    public Response getBox(@PathParam("year") int year,
                           @PathParam("teams") int teams,
                           @QueryParam("callback") final String callback)
            throws IOException {
        logger.info("getting for " + year + " " + teams);
        JsonNode jsonNodes = SoccerParser.bundleData(year, teams);
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

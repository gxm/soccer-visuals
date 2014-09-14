package com.moulliet.common;

import com.sun.jersey.api.client.Client;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;

/**
 *
 */
@Path("/file")
public class FileResource {
    @Context
    UriInfo uriInfo;

    private static final Logger logger = LoggerFactory.getLogger(FileResource.class);
    private Client client = ClientCreator.cached();
    private static final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Path("{path:.*}")
    public String get(@PathParam("path") String path) throws IOException {
        File file = new File(Config.getBaseDir() + "/web/" + path);

        return FileUtils.readFileToString(file);
    }
}

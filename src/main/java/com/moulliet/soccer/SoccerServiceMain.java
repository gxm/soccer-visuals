package com.moulliet.soccer;

import com.moulliet.common.Config;
import com.moulliet.soccer.soccer.SoccerParser;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoccerServiceMain {
    private static final Logger logger = LoggerFactory.getLogger(SoccerServiceMain.class);

    private static Server server;

    public static void main(String[] args) throws Exception {
        logger.debug("starting up SoccerServiceMain ");
        startResources(8081);
        server.join();
    }

    public static void startResources(int port) throws Exception {
        logger.info("starting Soccer Service on port " + port);
        ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
        servletHolder.setInitParameter(PackagesResourceConfig.PROPERTY_PACKAGES,
                "com.moulliet.common com.moulliet.soccer");
        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addServlet(servletHolder, "/*");
        Config.getInstance().set("stat.iterations", 500);
        SoccerParser.loadData();
        Teams.calculateRpis();
        server.start();
        logger.info("started Soccer Service on port " + port);
    }

    public static void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.warn("unable to stop", e);
        }
    }
}

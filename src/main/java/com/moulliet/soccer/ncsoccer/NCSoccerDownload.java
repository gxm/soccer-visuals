package com.moulliet.soccer.ncsoccer;

import com.moulliet.common.ClientCreator;
import com.moulliet.common.Config;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class NCSoccerDownload {
    public static void main(String[] args) throws IOException {
        String ROOT_URL = "http://woso-stats.tk/college/2014/";
        Client client = ClientCreator.cached();
        for (int i = 1; i <= 333; i++) {
            ClientResponse response = client.resource(ROOT_URL + "schedule/" + i).get(ClientResponse.class);
            if (response.getStatus() == 200) {
                String entity = response.getEntity(String.class);
                FileUtils.writeStringToFile(new File(Config.getBaseDir() + "/data/ncsoccer/" + i + ".html"), entity);
                System.out.println("downloaded " + i);
            } else {
                System.out.println("unexpected repsonse " + response.getStatus() + " " + i);
            }
        }

        ClientResponse response = client.resource(ROOT_URL + "data/2014-getAllRecords.json").get(ClientResponse.class);
        if (response.getStatus() == 200) {
            String entity = response.getEntity(String.class);
            FileUtils.writeStringToFile(new File(Config.getBaseDir() + "/data/ncsoccer/2014-getAllRecords.json"), entity);
            System.out.println("downloaded getAllRecords.json");
        } else {
            System.out.println("unexpected repsonse " + response.getStatus());
        }

        FileUtils.writeStringToFile(new File(Config.getBaseDir() + "/data/ncsoccer/updated"),
                new DateTime().toString("yyyy-MM-dd KK:mm aa zz"));
    }
}

package com.moulliet.soccer.soccer;

import com.moulliet.common.Config;
import com.moulliet.soccer.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SoccerParser {
    private static final Logger logger = LoggerFactory.getLogger(SoccerParser.class);

    private static String data;
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.000");

    public static void loadData() throws IOException {
        File dir = new File(Config.getBaseDir() + "/data/soccer/");
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".html");
            }
        });
        for (File file : files) {
            parseFile(file);
        }

    }

    public static JsonNode bubbleData(int year, Team team) throws IOException {
        ObjectNode rootNode = mapper.createObjectNode();
        addDate(rootNode);
        ArrayNode teamsNode = mapper.createArrayNode();

        List<Bubble> bubbles = team.getSeason(year).getBubbles();
        for (Bubble bubble : bubbles) {
            bubble.write(teamsNode.addObject());
        }
        rootNode.put("data", teamsNode);
        return rootNode;
    }

    public static JsonNode bundleData(int year, int maxId) throws IOException {
        ObjectNode rootNode = mapper.createObjectNode();
        addDate(rootNode);
        List<Team> teams = Teams.getTeamsByRpi(year);
        float minRpi = teams.get(maxId - 1).getSeason(year).getRpi();

        ArrayNode arrayNode = rootNode.putArray("data");
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            Season season = team.getSeason(year);
            if (season.getRpi() >= minRpi) {

                ObjectNode teamNode = arrayNode.addObject();
                teamNode.put("rpiId", i + 1);
                teamNode.put("name", team.getName());
                //use import field for games lost & tied?  how to handle future games?
                ArrayNode imports = teamNode.putArray("imports");
                Collection<Game> games = season.getGames();
                for (Game game : games) {
                    Team other = game.getOther(team);
                    if (game.isLossOrTie(team) && other.getSeason(year).getRpi() >= minRpi && game.isPlayed()) {
                        ObjectNode importNode = imports.addObject();
                        importNode.put("name", other.getName());
                        importNode.put("goals", game.goalDifference());
                    }
                }
            }
        }
        return rootNode;
    }

    public static JsonNode rpis(int year, int teamCount) throws IOException {
        ObjectNode rootNode = mapper.createObjectNode();
        addDate(rootNode);
        DefaultSeasonStrategy defaultStrategy = new DefaultSeasonStrategy();
        List<Team> teams = Teams.getTeamsByRpi(year, defaultStrategy);
        StatisticalSeasonStrategy statisticalStrategy = new StatisticalSeasonStrategy();
        ArrayNode teamsNode = rootNode.putArray("teams");
        for (int i = 0; i < teamCount; i++) {
            Team team = teams.get(i);
            ObjectNode teamNode = teamsNode.addObject();
            teamNode.put("name", team.getName());
            teamNode.put("conference", team.getConference());
            Map<LocalDate, Float> dateRpiMap = team.getSeason(year).getDateRpiMap();
            for (Map.Entry<LocalDate, Float> entry : dateRpiMap.entrySet()) {
                teamNode.put(entry.getKey().toString("MM-dd"), round(entry.getValue()));
            }
            if (year == Dates.CURRENT_YEAR) {
                teamNode.put("current", round(defaultStrategy.getRpi(team, year)));
                teamNode.put("prediction", round(statisticalStrategy.getRpi(team, year)));
            }
        }

        return rootNode;
    }

    private static String round(float toRound) {
        return DECIMAL_FORMAT.format(toRound);
    }

    private static void handle(int teamCount, int year, SeasonStrategy seasonStrategy, ArrayNode nodes) {
        //is this going to work for stats? getStatSeasonsRpi
        List<Team> teams = Teams.getTeamsByRpi(year, seasonStrategy);
        for (int i = 0; i < teamCount; i++) {
            Team team = teams.get(i);
            ObjectNode teamNode = nodes.addObject();
            teamNode.put("rank", i + 1);
            teamNode.put("name", team.getName());
            teamNode.put("rpi", seasonStrategy.getRpi(team, year));

        }
    }

    private static void parseFile(File file) throws IOException {
        data = FileUtils.readFileToString(file);
        String id = file.getName().substring(0, file.getName().indexOf("."));
        String teamName = StringUtils.substringBetween(data, "teaminfoname\">", "<a ");
        Team team = Teams.get(Integer.parseInt(id));
        String conference = substringAfter("teaminfoconf\">", "</a", ">");
        team.setName(teamName);
        team.setConference(conference);
        parseGames(team);
    }

    private static void parseGames(Team team) {
        String[] rows = StringUtils.substringsBetween(data, "<tr class=\"schedulegame", "</tr>");
        for (String row : rows) {
            GameParser gameParser = new GameParser(row);
            Game game = gameParser.parse(team);
            if (game != null) {
                team.getSeason(game.getDate().getYear()).addGame(game);
            }
        }
    }

    private static void addDate(ObjectNode objectNode) throws IOException {
        File updated = new File(Config.getBaseDir() + "/data/soccer/updated");
        String date = FileUtils.readFileToString(updated);
        objectNode.put("date", date);
    }

    private static String substringAfter(String start, String end, String after) {
        String initial = StringUtils.substringBetween(data, start, end);
        if (initial == null) return null;
        return initial.substring(initial.indexOf(after) + after.length());
    }


}

package com.moulliet.soccer;

import com.moulliet.common.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Teams {
    private static final Logger logger = LoggerFactory.getLogger(Teams.class);

    private static Map<Integer, Team> teamsById = new HashMap<Integer, Team>();

    public static Team get(int id) {
        Team team = teamsById.get(id);
        if (null == team) {
            team = new Team(id);
            teamsById.put(id, team);
        }
        return team;
    }

    public static void calculateRpis() {
        for (int year = 2009; year <= Dates.CURRENT_YEAR; year++) {
            calculateRpi(year);
        }

        forecastRpi();

        statisticalRpi();
    }

    private static void statisticalRpi() {
        logger.info("starting statistical");
        int year = Dates.CURRENT_YEAR;
        List<Team> teamsByRpi = getTeamsByRpi(year);
        int iterations = Config.getInstance().get("stat.iterations", 500);
        for (int i = 0; i < iterations; i++) {
            for (Team team : teamsByRpi) {
                team.statisticalCopy(year);
            }
            for (Team team : teamsByRpi) {
                team.statisticalRpi(year);
            }
        }
        logger.info("completed statistical");
    }

    private static void forecastRpi() {
        logger.info("starting forecast");
        int year = Dates.CURRENT_YEAR;
        List<Team> teamsByRpi = getTeamsByRpi(year);
        for (int i = 0; i < teamsByRpi.size(); i++) {
            Team team = teamsByRpi.get(i);
            team.getSeason(year).setRpiRank(i + 1);
        }
        for (Team team : teamsByRpi) {
            team.forecastCopy(year);
        }
        for (Team team : teamsByRpi) {
            team.forecastRpi(year);
        }
        logger.info("completed forecast");
    }

    public static void clearSeason(int year) {
        for (Team team : teamsById.values()) {
            team.clearSeason(year);
        }
    }

    public static void calculateRpi(int year) {
        for (Team team : teamsById.values()) {
            team.calculateRpi(year);
        }
    }

    public static List<Team> getTeamsByRpi(final int year) {
        return getTeamsByRpi(year, new DefaultSeasonStrategy());
    }

    public static List<Team> getTeamsByRpi(final int year, final SeasonStrategy seasonStrategy) {
        ArrayList<Team> list = new ArrayList<Team>(teamsById.values());

        Collections.sort(list, new Comparator<Team>() {
            /**
             * Compares its two arguments for order.
             * Returns a negative integer, zero, or a positive integer as the
             * first argument is less than, equal to, or greater than the second.
             */
            public int compare(Team team1, Team team2) {
                return (int) (10000 * (seasonStrategy.getRpi(team1, year)
                        - seasonStrategy.getRpi(team2, year)));
            }
        });

        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            Team team = list.get(i);
            seasonStrategy.getSeason(team, year).setRpiRank(i + 1);
        }
        return list;
    }

    public static List<Team> getTeamsByName() {
        ArrayList<Team> list = new ArrayList<Team>(teamsById.values());
        Collections.sort(list, new Comparator<Team>() {
            /**
             * Compares its two arguments for order.
             * Returns a negative integer, zero, or a positive integer as the
             * first argument is less than, equal to, or greater than the second.
             */
            public int compare(Team team1, Team team2) {
                return (team1.getName().compareTo(team2.getName()));
            }
        });
        return list;
    }


}

package com.moulliet.soccer;

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
        return list;
    }

}

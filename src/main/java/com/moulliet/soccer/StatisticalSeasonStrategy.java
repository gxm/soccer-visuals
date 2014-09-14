package com.moulliet.soccer;

import org.joda.time.LocalDate;

public class StatisticalSeasonStrategy implements SeasonStrategy {
    public Season getSeason(Team team, int year) {
        return team.getSeason(year).getStatSeason();
    }

    public float getRpi(Team team, int year) {
        return team.getStatSeasonsRpi();
    }

    public boolean useBubbles(LocalDate endDate) {
        return false;
    }
}

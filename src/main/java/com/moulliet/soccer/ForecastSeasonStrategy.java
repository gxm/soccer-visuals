package com.moulliet.soccer;

import org.joda.time.LocalDate;

public class ForecastSeasonStrategy implements SeasonStrategy {

    public Season getSeason(Team team, int year) {
        return team.getSeason(year).getForecastSeason();
    }

    public float getRpi(Team team, int year) {
        return getSeason(team, year).getRpi();
    }

    public boolean useBubbles(LocalDate endDate) {
        return false;
    }
}

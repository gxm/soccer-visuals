package com.moulliet.soccer;

import org.joda.time.LocalDate;

public interface SeasonStrategy {

    Season getSeason(Team team, int year);

    float getRpi(Team team, int year);

    boolean useBubbles(LocalDate endDate);
}

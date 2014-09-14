package com.moulliet.soccer;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team {
    private static final Logger logger = LoggerFactory.getLogger(Team.class);

    private int id;
    private String name;
    private String conference;
    private Map<Integer, Season> seasonsByTeamId = new HashMap<Integer, Season>();

    private List<Float> statRpis = new ArrayList<Float>();

    public Team(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.remove(name, ".");
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public Season getSeason(int year) {
        Season season = seasonsByTeamId.get(year);
        if (null == season) {
            season = new Season(year, this);
            seasonsByTeamId.put(year, season);
        }
        return season;
    }

    public void clearSeason(int year) {
        Season season = seasonsByTeamId.get(year);
        if (null != season) {
            seasonsByTeamId.put(year, null);
        }
    }

    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public void calculateRpi(int year) {
        try {
            /**
             * element 1 - (W + 1/2 T)/(W + L + T)
             * element 2 - average of - (OW + 1/2OT)/(OW + (OL) + OT) - exclude results again this team
             * element 3 - element 2 of opponents
             *
             * total (Element 1 + (2 x Element 2) + Element 3)/4
             */
            Season season = getSeason(year);

            Dates.DatePair dates = Dates.get().getStartDate(year);
            LocalDate currentDate = dates.startDate;
            while (currentDate.isBefore(dates.endDate) || currentDate.isEqual(dates.endDate)) {
                season.calculateRpi(currentDate);
                currentDate = currentDate.plusDays(7);
            }

            season.calculateRpi();
        } catch (Exception e) {
            logger.warn("unable to calculate rpi for team id {} {} year {} ", id, name, year);
            logger.warn("team " + name, e);
        }

    }

}

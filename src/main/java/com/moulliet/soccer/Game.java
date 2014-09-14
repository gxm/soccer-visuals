package com.moulliet.soccer;

import org.joda.time.LocalDate;

public class Game {
    private LocalDate date;
    private int homeTeamId;
    private int awayTeamId;
    private int homeScore;
    private int awayScore;
    private boolean neutral;
    private boolean played = false;

    public Game(LocalDate date, int homeTeamId, int awayTeamId, int homeScore, int awayScore, boolean neutral) {
        this.date = date;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.neutral = neutral;
        played = true;
    }

    public Game(LocalDate date, int homeTeamId, int awayTeamId, boolean neutral) {
        this.date = date;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.neutral = neutral;
        played = false;
    }

    public Game(Game game) {
        this.date = game.getDate();
        this.homeTeamId = game.getHomeTeamId();
        this.awayTeamId = game.getAwayTeamId();
        this.neutral = game.isNeutral();
        played = false;
    }

    public int goalDifference() {
        return Math.abs(homeScore - awayScore);
    }

    public boolean isLossOrTie(Team team) {
        return isLoss(team) || isTie(team);
    }

    public boolean isLoss(Team team) {
        if (team.getId() == homeTeamId) {
            return homeScore < awayScore;
        }
        return awayScore < homeScore;
    }

    public boolean isTie(Team team) {
        return awayScore == homeScore;
    }

    public Team getOther(Team team) {
        if (team.getId() == homeTeamId) {
            return Teams.get(awayTeamId);
        }
        return Teams.get(homeTeamId);
    }

    public String as(Team team) {
        String result = "";
        if (isLoss(team)) {
            result += "L ";
        } else if (isTie(team)) {
            result += "T ";
        } else {
            result += "W ";
        }
        if (team.getId() == homeTeamId) {
            result += homeScore + "-" + awayScore;
        } else {
            result += awayScore + "-" + homeScore;
        }
        return result + " " + getOther(team).getName() + " " + date + " " + played;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public boolean isNeutral() {
        return neutral;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
        played = true;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
        played = true;
    }

    @Override
    public String toString() {
        return "Game{" +
                "date=" + date +
                ", homeTeamId=" + homeTeamId +
                ", awayTeam=" + awayTeamId +
                ", homeScore=" + homeScore +
                ", awayScore=" + awayScore +
                ", neutral=" + neutral +
                ", played=" + played +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        if (awayScore != game.awayScore) return false;
        if (awayTeamId != game.awayTeamId) return false;
        if (homeScore != game.homeScore) return false;
        if (homeTeamId != game.homeTeamId) return false;
        if (neutral != game.neutral) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = homeTeamId;
        result = 31 * result + awayTeamId;
        result = 31 * result + homeScore;
        result = 31 * result + awayScore;
        result = 31 * result + (neutral ? 1 : 0);
        return result;
    }

    public boolean isFuture(LocalDate endDate) {
        if (endDate == null) {
            return false;
        }
        return date.isAfter(endDate);
    }
}

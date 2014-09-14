package com.moulliet.soccer;

import java.util.Random;

public class StatisticalGame {
    private static final Random random = new Random();
    private static SeasonStrategy strategy = new StatisticalSeasonStrategy();

    public static Game lookup(Game game) {
        Team homeTeam = Teams.get(game.getHomeTeamId());
        Team awayTeam = Teams.get(game.getAwayTeamId());

        Season homeSeason = strategy.getSeason(homeTeam, 0);

        if (homeSeason != null) {
            Game homeGame = homeSeason.getGame(game.getDate());
            if (homeGame != null) {
                return homeGame;
            }
        }
        Season awaySeason = strategy.getSeason(awayTeam, 0);
        if (awaySeason != null) {
            Game awayGame = awaySeason.getGame(game.getDate());
            if (awayGame != null) {
                return awayGame;
            }
        }
        int year = Dates.CURRENT_YEAR;
        int homeRank = homeTeam.getSeason(year).getRpiRank();
        int awayRank = awayTeam.getSeason(year).getRpiRank();
        int difference = homeRank - awayRank;

        //when diff is negative, home team wins
        int min = Math.min(homeRank, awayRank);
        WinLossTie winLossTie = getPercents(min);
        Game newGame = new Game(game);
        float randFloat = random.nextFloat() * 100;
        if (randFloat < winLossTie.win) {
            if (difference < 0) {
                newGame.setHomeScore(1);
                newGame.setAwayScore(0);
            } else {
                newGame.setHomeScore(0);
                newGame.setAwayScore(1);
            }

        } else if (randFloat < winLossTie.win + winLossTie.tie) {
            newGame.setHomeScore(0);
            newGame.setAwayScore(0);
        } else {
            if (difference < 0) {
                newGame.setHomeScore(0);
                newGame.setAwayScore(1);
            } else {
                newGame.setHomeScore(1);
                newGame.setAwayScore(0);
            }
        }
        return newGame;
    }

    static WinLossTie getPercents(int rpiRank) {

        WinLossTie wlt = new WinLossTie();
        if (rpiRank <= 10) {
            wlt.win = 82.1f;
            wlt.tie = 7.7f;
            wlt.loss = 10.3f;

        } else if (rpiRank <= 20) {
            wlt.win = 76.2f;
            wlt.tie = 9.6f;
            wlt.loss = 14.1f;
        } else if (rpiRank <= 30) {
            wlt.win = 73.4f;
            wlt.tie = 10.6f;
            wlt.loss = 16.0f;
        } else if (rpiRank <= 40) {
            wlt.win = 71.4f;
            wlt.tie = 11.6f;
            wlt.loss = 17.0f;
        } else if (rpiRank <= 50) {
            wlt.win = 72.0f;
            wlt.tie = 11.1f;
            wlt.loss = 16.9f;
        } else {
            wlt.win = 72.9f;
            wlt.tie = 11.2f;
            wlt.loss = 16.0f;
        }
        return wlt;
    }

    static class WinLossTie {
        float win;
        float tie;
        float loss;


    }
}

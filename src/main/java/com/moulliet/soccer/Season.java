package com.moulliet.soccer;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Season {
    private static final Logger logger = LoggerFactory.getLogger(Season.class);
    private int year;
    private Team team;
    private Map<LocalDate, Game> games = new TreeMap<LocalDate, Game>();
    private float rpi = 0;
    private Map<Integer, Bubble> bubbles;
    private Calc level1;
    private Map<Team, Calc> level2 = new HashMap<Team, Calc>();
    private SeasonStrategy seasonStrategy = new DefaultSeasonStrategy();

    private Map<LocalDate, Float> dateRpiMap = new TreeMap<LocalDate, Float>();

    public Season(int year, Team team) {
        this.year = year;
        this.team = team;
    }

    public Map<LocalDate, Float> getDateRpiMap() {
        return dateRpiMap;
    }

    public void addGame(Game game) {
        games.put(game.getDate(), game);
    }

    public Collection<Game> getGames() {
        return games.values();
    }

    public float getRpi() {
        return rpi;
    }

    public void calculateRpi() {
        calculateRpi(null);
    }

    public void calculateRpi(LocalDate endDate) {

        bubbles = new HashMap<Integer, Bubble>();
        /**
         * element 1 - (W + 1/2 T)/(W + L + T)
         * element 2 - average of - (OW + 1/2OT)/(OW + (OL) + OT) - exclude results against team
         * element 3 - average of (average of element 2 of opponents) - exclude results against opponent
         *
         * total (Element 1 + (2 x Element 2) + Element 3)/4
         */
        Calc element1 = element1(endDate);
        if (element1 == null) {
            rpi = 0;
        } else {
            rpi = (element1.calc + element2(endDate) * 2 + element3(endDate)) / 4;
        }
        if (endDate != null) {
            dateRpiMap.put(endDate, rpi);
        }
        //normalize bubbles
        if (seasonStrategy.useBubbles(endDate)) {
            float percent = 0;
            float adjusted = 0;
            for (Bubble bubble : bubbles.values()) {
                percent += bubble.getPercent();
                adjusted += bubble.getAdjusted();
            }
            float percentFactor = 100 / percent;
            float adjustedFactor = 100 / adjusted;
            for (Bubble bubble : bubbles.values()) {
                bubble.normalize(percentFactor, adjustedFactor);
            }
        }

    }

    private Bubble getBubble(Team team) {
        if (!bubbles.containsKey(team.getId())) {
            bubbles.put(team.getId(), new Bubble(team));
        }
        return bubbles.get(team.getId());
    }

    public Calc element1(LocalDate endDate) {
        level1 = calc(games.values(), team, null, endDate);
        if (null != level1) {
            if (seasonStrategy.useBubbles(endDate)) {
                getBubble(team).add(0.25f, 0.25f * level1.calc);
            }
        }
        return level1;
    }

    public float element2(LocalDate endDate) {
        List<Float> element2s = new ArrayList<Float>();
        float percent = (float) 1 / level1.games / 2;
        for (Game game : games.values()) {
            if (game.isPlayed()) {
                Team other = game.getOther(team);
                Collection<Game> otherGames = seasonStrategy.getSeason(other, year).games.values();
                Calc element2 = calc(otherGames, other, team, endDate);
                if (null != element2) {
                    level2.put(other, element2);
                    if (seasonStrategy.useBubbles(endDate)) {
                        getBubble(other).add(percent, percent * element2.calc);
                    }
                    element2s.add(element2.calc);
                }
            }
        }
        return average(element2s);
    }

    public float element3(LocalDate endDate) {
        try {
            List<Float> element3s = new ArrayList<Float>();
            for (Game game : games.values()) {
                if (game.isPlayed()) {

                    Team other = game.getOther(team);
                    Collection<Game> otherGames = seasonStrategy.getSeason(other, year).games.values();
                    List<Float> otherE3 = new ArrayList<Float>();

                    Calc otherCalc = level2.get(other);
                    if (null != otherCalc) {
                        float percent = (float) 1 / otherCalc.games / level1.games / 4;
                        for (Game otherGame : otherGames) {
                            if (otherGame.isPlayed()) {
                                Team third = otherGame.getOther(other);
                                Collection<Game> thirdgames = seasonStrategy.getSeason(third, year).getGames();
                                Calc element3 = calc(thirdgames, third, other, endDate);
                                if (null != element3) {
                                    if (seasonStrategy.useBubbles(endDate)) {
                                        getBubble(third).add(percent, percent * element3.calc);
                                    }
                                    otherE3.add(element3.calc);
                                }
                            }
                        }
                        element3s.add(average(otherE3));
                    }
                }
            }
            return average(element3s);
        } catch (Exception e) {
            logger.warn(team.getName() + " " + year + " " + endDate, e);
            return 0;
        }
    }

    private Calc calc(Collection<Game> gameList, Team team, Team exclude, LocalDate endDate) {
        int wins = 0;
        int ties = 0;
        int losses = 0;
        int games = 0;
        for (Game game : gameList) {
            if (!game.isPlayed() || game.isFuture(endDate)) {
                //do nothing
            } else if (game.getOther(team).equals(exclude)) {
                games++;
            } else if (game.isLoss(team)) {
                losses++;
            } else if (game.isTie(team)) {
                ties++;
            } else {
                wins++;
            }
        }
        float denominator = (float) (wins + ties + losses);
        if (denominator == 0) return null;
        float calc = (wins + (float) ties / 2) / denominator;
        return new Calc(calc, denominator + games);
    }

    public static float average(List<Float> elements) {
        float result = 0;
        for (Float element : elements) {
            result += element;
        }
        return result / elements.size();
    }

    public List<Bubble> getBubbles() {
        return new ArrayList<Bubble>(bubbles.values());
    }

    public static class Calc {
        public float calc;
        public float games;

        Calc(float calc, float games) {
            this.calc = calc;
            this.games = games;
        }
    }
}

package com.moulliet.soccer.soccer;

import com.moulliet.soccer.Game;
import com.moulliet.soccer.Team;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 */
public class GameParser {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyyy");
    private String row;
    private int index = 0;

    public GameParser(String row) {
        this.row = row;
    }

    public Game parse(Team team) {
        row = StringUtils.substringAfter(row, "<td>");
        String date = before("</td>");

        int id = 0;
        try {
            id = Integer.parseInt(between("schedule/", "\">"));
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
        LocalDate localDateTime = dateTimeFormatter.parseLocalDate(date);

        String match = between(">", "</a>");
        //if match starts with vs or at, home or away
        //if match contains #, neutral site
        //if match ends with EXH, exhibition, ignore
        boolean home = !match.startsWith("at");
        boolean neutral = match.contains("#");
        if (match.endsWith("(EXH)")) return null;

        String results = between("<td>", "</td>");
        if (results.trim().isEmpty()) {
            return new Game(localDateTime, home ? team.getId() : id, home ? id : team.getId(), neutral);
        } else {
            int teamScore = Integer.parseInt(StringUtils.substringBetween(results, " ", "-"));
            int otherScore = Integer.parseInt(StringUtils.substringAfter(results, "-"));
            if (home) {
                return new Game(localDateTime, team.getId(), id, teamScore, otherScore, neutral);
            }
            return new Game(localDateTime, id, team.getId(), otherScore, teamScore, neutral);
        }
    }

    private String before(String before) {
        int endIndex = row.indexOf(before, index);
        String substring = row.substring(index, endIndex);
        index = endIndex;
        return substring;
    }

    private String between(String after, String before) {
        int startIndex = row.indexOf(after, index) + after.length();
        int endIndex = row.indexOf(before, startIndex);
        index = endIndex;
        return row.substring(startIndex, endIndex);
    }
}

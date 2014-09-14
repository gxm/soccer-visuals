package com.moulliet.soccer;

import org.codehaus.jackson.node.ObjectNode;

import java.text.DecimalFormat;

/**
 *
 */
public class Bubble {
    private final DecimalFormat df = new DecimalFormat("##.##");
    private float percent = 0;
    private float adjusted = 0;
    private Team team;

    public Bubble(Team team) {
        this.team = team;
    }

    public void add(float percent, float adjusted) {
        this.percent += percent;
        this.adjusted += adjusted;
    }

    public float getPercent() {
        return percent;
    }

    public float getAdjusted() {
        return adjusted;
    }

    public void normalize(float percentFactor, float adjustedFactor) {
        percent *= percentFactor;
        adjusted *= adjustedFactor;
    }

    public void write(ObjectNode teamNode) {
        teamNode.put("name", team.getName());
        teamNode.put("conference", team.getConference());
        teamNode.put("value", df.format(adjusted));
    }

}

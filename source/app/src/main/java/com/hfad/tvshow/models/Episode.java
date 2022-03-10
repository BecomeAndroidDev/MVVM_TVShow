package com.hfad.tvshow.models;

import com.google.gson.annotations.SerializedName;

public class Episode {

    @SerializedName("season")
    private int season;

    @SerializedName("episode")
    private int episode;

    @SerializedName("name")
    private String name;

    @SerializedName("air_date")
    private String airDate;

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }
}

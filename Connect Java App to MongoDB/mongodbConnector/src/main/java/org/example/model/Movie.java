package org.example.model;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class Movie {
    private ObjectId id;
    private String plot;
    List<String> genres;
    private Double runtime;
    private List<String> cast;
    private String lastupdated;
    private Date released;
    String title;
    private Awards awards;


    public Awards getAwards() {
        return awards;
    }

    public void setAwards(Awards awards) {
        this.awards = awards;
    }

    public Date getReleased() {
        return released;
    }

    public void setReleased(Date released) {
        this.released = released;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastUpdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Double getRuntime() {
        return runtime;
    }

    public void setRuntime(Double runtime) {
        this.runtime = runtime;
    }

    public List<String> getCast() {
        return cast;
    }

    public void setCast(List<String> cast) {
        this.cast = cast;
    }


    public String getPlot() {
        return plot;
    }
    public void setPlot(String plot) {
        this.plot = plot;
    }
    public List<String> getGenres() {
        return genres;
    }
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
//    @Override
//    public String toString() {
//        return "Movie [\n  plot=" + plot + ",\n  genres=" + genres + ",\n  title=" + title + "\n]";
//    }


    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id + '\n' +
                ", plot='" + plot + '\n' +
                ", genres=" + genres + '\n' +
                ", runtime=" + runtime + '\n' +
                ", cast=" + cast + '\n' +
                ", lastupdated='" + lastupdated + '\n' +
                ", released=" + released + '\n' +
                ", title='" + title + '\n' +
                ", awards=" + awards + '\n' +
                '}';
    }
}

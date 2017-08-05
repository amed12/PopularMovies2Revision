package com.sun3toline.popularmovies2revision.data.model.responses;

import com.sun3toline.popularmovies2revision.data.model.MovieVideo;

import java.util.List;

/**
 * Created by coldware on 8/4/17.
 */

public class MovieTrailersResponse {
    private int id;
    private List<MovieVideo> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MovieVideo> getResults() {
        return results;
    }

    public void setResults(List<MovieVideo> results) {
        this.results = results;
    }
}

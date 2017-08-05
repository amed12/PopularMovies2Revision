package com.sun3toline.popularmovies2revision.data.model.responses;

import com.sun3toline.popularmovies2revision.data.model.Review;

import java.util.List;

/**
 * Created by coldware on 8/4/17.
 */

public class MovieReviewResponse {
    private List<Review> results;

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}

package com.sun3toline.popularmovies2revision.ui.myhome;

import com.sun3toline.popularmovies2revision.data.model.Movie;

import java.util.List;

/**
 * Created by coldware on 8/4/17.
 */

public interface MainView {
    void showLoading(boolean show);

    void displayMovies(List<Movie> results);

    void showError(String message);
}

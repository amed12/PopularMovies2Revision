package com.sun3toline.popularmovies2revision.ui.detailmovie;

import com.sun3toline.popularmovies2revision.data.model.MovieVideo;
import com.sun3toline.popularmovies2revision.data.model.Review;

import java.util.List;

/**
 * Created by coldware on 8/4/17.
 */

public interface DetailView {
    void displayVideo(List<MovieVideo> first);

    void displayReview(List<Review> second);

    void displayError(String localizedMessage);

    void favoriteSuccess();

    void showError(String localizedMessage);

    void movieIsFavorited();
}

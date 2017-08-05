package com.sun3toline.popularmovies2revision.data.repository;

import com.sun3toline.popularmovies2revision.data.model.Movie;
import com.sun3toline.popularmovies2revision.data.model.MovieVideo;
import com.sun3toline.popularmovies2revision.data.model.Review;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by coldware on 8/4/17.
 */

public interface MovieRepository {
    Observable<List<Movie>> getPopularMovie(int page);

    Observable<List<Movie>> getTopRatedMovie(int page);

    Observable<List<MovieVideo>> getVideoTrailers(int movieId);

    Observable<List<Review>> getMovieReviews(int movieId, int page);

    Completable saveMovie(Movie movie);

    Completable deleteMovie(Movie movie);

    Single<Movie> getMovie(Movie movie);

    Observable<List<Movie>> getFavoriteMovies();
}

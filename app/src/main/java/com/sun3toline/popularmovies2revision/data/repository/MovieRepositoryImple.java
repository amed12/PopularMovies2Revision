package com.sun3toline.popularmovies2revision.data.repository;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.sun3toline.popularmovies2revision.data.TheMovieDbServices;
import com.sun3toline.popularmovies2revision.data.model.Movie;
import com.sun3toline.popularmovies2revision.data.model.MovieVideo;
import com.sun3toline.popularmovies2revision.data.model.Review;
import com.sun3toline.popularmovies2revision.data.provider.MovieDbContract;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.COLUMN_MOVIE_BACKDROP_PATH;
import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.COLUMN_MOVIE_FAVORED;
import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.COLUMN_MOVIE_ID;
import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.COLUMN_MOVIE_OVERVIEW;
import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.COLUMN_MOVIE_POSTER_PATH;
import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.COLUMN_MOVIE_RELEASE_DATE;
import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.COLUMN_MOVIE_TITLE;
import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.COLUMN_MOVIE_VOTE_AVERAGE;
import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.COLUMN_MOVIE_VOTE_COUNT;
import static com.sun3toline.popularmovies2revision.data.provider.MovieDbContract.Movie.CONTENT_URI;
/**
 * Created by coldware on 8/4/17.
 */

public class MovieRepositoryImple implements MovieRepository {

    private static final String TAG = MovieRepositoryImple.class.getSimpleName();
    private TheMovieDbServices services;
    private ContentResolver contentResolver;

    private final String[] movieProjection = new String[]{
            MovieDbContract.Movie.COLUMN_MOVIE_ID,
            MovieDbContract.Movie.COLUMN_MOVIE_TITLE,
            MovieDbContract.Movie.COLUMN_MOVIE_OVERVIEW,
            MovieDbContract.Movie.COLUMN_MOVIE_VOTE_COUNT,
            MovieDbContract.Movie.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieDbContract.Movie.COLUMN_MOVIE_RELEASE_DATE,
            MovieDbContract.Movie.COLUMN_MOVIE_FAVORED,
            MovieDbContract.Movie.COLUMN_MOVIE_POSTER_PATH,
            MovieDbContract.Movie.COLUMN_MOVIE_BACKDROP_PATH,
    };

    public MovieRepositoryImple(TheMovieDbServices services, ContentResolver contentResolver) {
        this.services = services;
        this.contentResolver = contentResolver;
    }

    @Override
    public Observable<List<Movie>> getPopularMovie(int page) {
        return services.getPopularMovie(page).flatMap(movieResponse -> Observable.just(movieResponse.results));
    }

    @Override
    public Observable<List<Movie>> getTopRatedMovie(int page) {
        return services.getTopRatedMovie(page).flatMap(movieResponse -> Observable.just(movieResponse.results));
    }

    @Override
    public Observable<List<MovieVideo>> getVideoTrailers(int movieId) {
        return services.getMovieTrailers(movieId).flatMap(movieTrailersResponse -> Observable.just(movieTrailersResponse.getResults()));
    }

    @Override
    public Observable<List<Review>> getMovieReviews(int movieId, int page) {
        return services.getMovieReview(movieId, page).flatMap(movieReviewResponse -> Observable.just(movieReviewResponse.getResults()));
    }

    @Override
    public Completable saveMovie(Movie movie) {
        Log.d(TAG, "saveMovie: " + movie.getTitle());
        return Completable.create(e -> {
            final ContentValues movieValues = getMovieValues(movie);
            contentResolver.insert(CONTENT_URI, movieValues);
            e.onComplete();
        });
    }

    @Override
    public Completable deleteMovie(Movie movie) {
        Log.d(TAG, "deleteMovie: " + movie.getTitle());
        return Completable.create(e -> {
            final String where = String.format("%s=?", COLUMN_MOVIE_ID);
            final String[] args = new String[]{String.valueOf(movie.getId())};
            contentResolver.delete(CONTENT_URI, where, args);
            e.onComplete();
        });
    }

    @Override
    public Single<Movie> getMovie(Movie movie) {
        return Single.create(e -> {
            final String where = String.format("%s=?", COLUMN_MOVIE_ID);
            final String[] args = new String[]{String.valueOf(movie.getId())};
            final Cursor cursor = contentResolver.query(CONTENT_URI, movieProjection, where, args, null);
            Log.d(TAG, "getMovie: " + movie.getTitle());
            Log.d(TAG, "getMovie: cursor count " + cursor.getCount());
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                final Movie resultMovie = Movie.fromCursor(cursor);
                e.onSuccess(resultMovie);
            } else {
                e.onError(new Throwable("No movies"));
            }
        });
    }

    @Override
    public Observable<List<Movie>> getFavoriteMovies() {
        return Observable.create(e -> {
            List<Movie> movies = new ArrayList<>();
            final Cursor query = contentResolver.query(CONTENT_URI, movieProjection, null, null, null);
            if (query.moveToFirst()) {
                do {
                    movies.add(Movie.fromCursor(query));
                } while (query.moveToNext());
            }
            Log.d(TAG, "getFavoriteMovies: " + movies.size());
            e.onNext(movies);
        });
    }

    /**
     * Return a {@link ContentValues} item with the values from a {@link Movie}.
     */
    public static ContentValues getMovieValues(Movie movie) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_ID, movie.getId());
        values.put(COLUMN_MOVIE_TITLE, movie.getTitle());
        values.put(COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        values.put(COLUMN_MOVIE_VOTE_COUNT, movie.getVote_count());
        values.put(COLUMN_MOVIE_VOTE_AVERAGE, movie.getVote_average());
        values.put(COLUMN_MOVIE_RELEASE_DATE, movie.getRelease_date());
        values.put(COLUMN_MOVIE_FAVORED, movie.isFavMovie());
        values.put(COLUMN_MOVIE_POSTER_PATH, movie.getPoster_path());
        values.put(COLUMN_MOVIE_BACKDROP_PATH, movie.getBackdrop_path());
        return values;
    }

}

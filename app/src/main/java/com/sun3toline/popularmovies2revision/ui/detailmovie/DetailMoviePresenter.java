package com.sun3toline.popularmovies2revision.ui.detailmovie;

import android.util.Log;
import android.util.Pair;

import com.sun3toline.popularmovies2revision.data.model.Movie;
import com.sun3toline.popularmovies2revision.data.repository.MovieRepository;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by coldware on 8/4/17.
 */

public class DetailMoviePresenter {
    private static final String TAG = DetailMoviePresenter.class.getSimpleName();
    private MovieRepository movieRepository;
    private DetailView view;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public DetailMoviePresenter(MovieRepository movieRepository, DetailView view) {
        this.movieRepository = movieRepository;
        this.view = view;
    }

    public void getVideoAndReview(int movieId) {
        compositeDisposable.add(Observable.zip(movieRepository.getVideoTrailers(movieId), movieRepository.getMovieReviews(movieId, 1),
                (movieVideos, reviews) -> new Pair<>(movieVideos, reviews))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listListPair -> {
                    view.displayVideo(listListPair.first);
                    view.displayReview(listListPair.second);
                }, throwable -> {
                    view.displayError(throwable.getLocalizedMessage());
                }));
    }

    public void favoriteMovie(Movie movie) {
        compositeDisposable.add(movieRepository.saveMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> view.favoriteSuccess(),
                        throwable -> view.showError(throwable.getLocalizedMessage())));
    }

    public void isMovieFavorited(Movie movie) {
        compositeDisposable.add(movieRepository.getMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> view.movieIsFavorited(), error -> {
                    Log.e(TAG, "isMovieFavorited: " + error.getLocalizedMessage());
                }));
    }

    public void deleteFavorite(Movie movie) {
        compositeDisposable.add(movieRepository.deleteMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(TAG, "deleteFavorite: success");
                }, throwable -> {
                    Log.e(TAG, "deleteFavorite: error");
                }));
    }
}

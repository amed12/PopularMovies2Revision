package com.sun3toline.popularmovies2revision.ui.myhome;

import com.sun3toline.popularmovies2revision.data.model.Movie;
import com.sun3toline.popularmovies2revision.data.repository.MovieRepository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by coldware on 8/4/17.
 */

public class MainPresenter {
    private MovieRepository movieRepository;
    private MainView view;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainPresenter(MovieRepository movieRepository, MainView view) {
        this.movieRepository = movieRepository;
        this.view = view;
    }


    public void getMovie(int sort, int page) {
        view.showLoading(true);

        Observable<List<Movie>> observableMovies;


        if (sort == MainActivity.POPULAR) {
            observableMovies = movieRepository.getPopularMovie(page);
        } else {
            observableMovies = movieRepository.getTopRatedMovie(page);
        }

        compositeDisposable.add(observableMovies.
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    view.showLoading(false);
                    if (movies != null && movies.size() > 0) {
                        view.displayMovies(movies);
                    }
                }, throwable -> {
                    view.showLoading(false);
                    view.showError(throwable.getLocalizedMessage());
                }));
    }

    public void detachView() {
        compositeDisposable.dispose();
    }

    public void attachView() {
        compositeDisposable = new CompositeDisposable();
    }

    public void getFavoriteMovie() {
        compositeDisposable.add(movieRepository.getFavoriteMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> view.displayMovies(movies),
                        throwable -> view.showError(throwable.getLocalizedMessage())));
    }
}
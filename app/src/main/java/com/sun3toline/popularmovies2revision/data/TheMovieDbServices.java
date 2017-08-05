package com.sun3toline.popularmovies2revision.data;

import com.sun3toline.popularmovies2revision.BuildConfig;
import com.sun3toline.popularmovies2revision.data.model.responses.MovieResponse;
import com.sun3toline.popularmovies2revision.data.model.responses.MovieReviewResponse;
import com.sun3toline.popularmovies2revision.data.model.responses.MovieTrailersResponse;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by coldware on 8/4/17.
 */

public interface TheMovieDbServices {

    @GET("movie/popular")
    Observable<MovieResponse> getPopularMovie(@Query("page") int page);

    @GET("movie/top_rated")
    Observable<MovieResponse> getTopRatedMovie(@Query("page") int page);

    @GET("movie/{id}/videos")
    Observable<MovieTrailersResponse> getMovieTrailers(@Path("id") int movieId);

    @GET("movie/{id}/reviews")
    Observable<MovieReviewResponse> getMovieReview(@Path("id") int movieId, @Query("page") int page);

    class Creator {

        public static TheMovieDbServices instance() {
            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            final TheMovieDbInterceptor theMovieDbInterceptor = new TheMovieDbInterceptor();

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(theMovieDbInterceptor)
                    .build();

            final Retrofit retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .baseUrl(BuildConfig.THEMOVIEDB_API)
                    .build();
            return retrofit.create(TheMovieDbServices.class);
        }
    }
}


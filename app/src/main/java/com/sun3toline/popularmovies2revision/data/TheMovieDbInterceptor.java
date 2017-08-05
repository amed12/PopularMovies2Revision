package com.sun3toline.popularmovies2revision.data;

import com.sun3toline.popularmovies2revision.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by coldware on 8/4/17.
 */

public class TheMovieDbInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        final HttpUrl url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("api_key", BuildConfig.THEMOVIEDB_KEY)
                .build();
        final Request request = chain.request().newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
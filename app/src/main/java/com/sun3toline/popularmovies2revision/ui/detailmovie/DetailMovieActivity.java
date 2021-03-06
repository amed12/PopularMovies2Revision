package com.sun3toline.popularmovies2revision.ui.detailmovie;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.nitrico.lastadapter.Holder;
import com.github.nitrico.lastadapter.ItemType;
import com.github.nitrico.lastadapter.LastAdapter;
import com.sun3toline.popularmovies2revision.BR;
import com.sun3toline.popularmovies2revision.R;
import com.sun3toline.popularmovies2revision.data.TheMovieDbServices;
import com.sun3toline.popularmovies2revision.data.model.Movie;
import com.sun3toline.popularmovies2revision.data.model.MovieVideo;
import com.sun3toline.popularmovies2revision.data.model.Review;
import com.sun3toline.popularmovies2revision.data.repository.MovieRepository;
import com.sun3toline.popularmovies2revision.data.repository.MovieRepositoryImple;
import com.sun3toline.popularmovies2revision.databinding.ActivityDetailMovieBinding;
import com.sun3toline.popularmovies2revision.databinding.ItemMovieReviewBinding;
import com.sun3toline.popularmovies2revision.databinding.ItemMovieTrailersBinding;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by coldware on 8/4/17.
 */

public class DetailMovieActivity extends AppCompatActivity implements DetailView {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();
    private static final String STATE_MOVIE_VIDEOS = "STATE_MOVIES_TRAILERS";
    private static final String STATE_MOVIE_REVIEW = "STATE_REVIEW";
    private static final String STATE_MOVIE = "STATE_MOVIE";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.rvTrailers)
    RecyclerView rvTrailers;
    @BindView(R.id.rvReview)
    RecyclerView rvReview;
    @BindView(R.id.btn_favorite)
    FloatingActionButton fabFavorite;
    @BindView(R.id.poster)
    ImageView imgPoster;

    private Movie movie;
    private DetailMoviePresenter presenter;
    private MovieRepository movieRepository;
    private TheMovieDbServices movieDbServices;

    private List<MovieVideo> movieVideos = new ArrayList<>();
    private List<Review> movieReview = new ArrayList<>();
    private LastAdapter trailersAdapter;
    private LastAdapter reviewAdapter;
    private boolean isFavorited = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init retrofit
        movieDbServices = TheMovieDbServices.Creator.instance();
        // init repository
        movieRepository = new MovieRepositoryImple(movieDbServices, DetailMovieActivity.this.getContentResolver());
        presenter = new DetailMoviePresenter(movieRepository, this);

        movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate: get saved instance state");
            movieVideos = savedInstanceState.getParcelableArrayList(STATE_MOVIE_VIDEOS);
            movieReview = savedInstanceState.getParcelableArrayList(STATE_MOVIE_REVIEW);
            movie = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MOVIE));
        }


        if (movie != null) {

            ActivityDetailMovieBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_movie);
            ButterKnife.bind(this, binding.getRoot());

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.setMovie(movie);
            collapsingToolbarLayout.setTitle(" ");


            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle(movie.getTitle());
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbarLayout.setTitle(" ");
                        isShow = false;
                    }
                }
            });

            // request movie review n video
            presenter.getVideoAndReview(movie.getId());
            presenter.isMovieFavorited(movie);

            initRvTrailers();
            initRvReview();
        }

    }

    private void initRvReview() {
        rvReview.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new LastAdapter(movieReview, BR.review)
                .map(Review.class, new ItemType<ItemMovieReviewBinding>(R.layout.item_movie_review) {
                    @Override
                    public void onCreate(Holder<ItemMovieReviewBinding> holder) {
                    }

                    @Override
                    public void onBind(Holder<ItemMovieReviewBinding> holder) {
                        super.onBind(holder);
                        Log.d(TAG, "onBind: " + holder.getBinding().getReview().getAuthor());
                    }
                })
                .into(rvReview);
    }

    private void initRvTrailers() {
        rvTrailers.setLayoutManager(new LinearLayoutManager(this));
        trailersAdapter = new LastAdapter(movieVideos, BR.trailers)
                .map(MovieVideo.class, new ItemType<ItemMovieTrailersBinding>(R.layout.item_movie_trailers) {
                    @Override
                    public void onBind(Holder<ItemMovieTrailersBinding> holder) {
                        super.onBind(holder);
                        Log.d(TAG, "onBind: " + holder.getBinding().getTrailers().getName());
                    }

                    @Override
                    public void onCreate(Holder<ItemMovieTrailersBinding> holder) {
                        super.onCreate(holder);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "onClick: " + holder.getBinding().getTrailers().getKey());
                                openVideoTrailers(holder.getBinding().getTrailers().getKey());
                            }
                        });
                    }
                }).into(rvTrailers);
    }

    private void openVideoTrailers(String key) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key)));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (movieVideos != null) {
            outState.putParcelableArrayList(STATE_MOVIE_VIDEOS, new ArrayList<>(movieVideos));
        }
        if (movieReview != null) {
            outState.putParcelableArrayList(STATE_MOVIE_REVIEW, new ArrayList<>(movieReview));
        }
        if (movie != null) {
            outState.putParcelable(STATE_MOVIE, Parcels.wrap(movie));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void displayVideo(List<MovieVideo> videos) {
        movieVideos.clear();
        movieVideos.addAll(videos);
        trailersAdapter.notifyDataSetChanged();

        for (MovieVideo video : videos) {
            Log.d(TAG, "displayVideo: " + video.getKey() + " " + video.getSize());
        }
    }

    @Override
    public void displayReview(List<Review> reviews) {
        movieReview.clear();
        movieReview.addAll(reviews);
        reviewAdapter.notifyDataSetChanged();
        for (Review review : reviews) {
            Log.d("tag", "review data " + review.getAuthor());
        }
    }

    @Override
    public void displayError(String localizedMessage) {

    }

    @Override
    public void favoriteSuccess() {
        Log.d(TAG, "favoriteSuccess: ");
        isFavorited = true;
        fabFavorite.setImageResource(R.drawable.ic_favorite_white_36dp);
    }

    @Override
    public void showError(String localizedMessage) {

    }

    @Override
    public void movieIsFavorited() {
        isFavorited = true;
        fabFavorite.setImageResource(R.drawable.ic_favorite_white_36dp);
    }

    @OnClick(R.id.btn_favorite)
    void onFavoriteClick() {
        if (isFavorited) {
            presenter.deleteFavorite(movie);
            fabFavorite.setImageResource(R.drawable.ic_favorite_border_white_36dp);
        } else {
            presenter.favoriteMovie(movie);
        }

    }
}

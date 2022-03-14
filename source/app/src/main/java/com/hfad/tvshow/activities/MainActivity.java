package com.hfad.tvshow.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.tvshow.R;
import com.hfad.tvshow.adapters.TVShowsAdapter;
import com.hfad.tvshow.databinding.ActivityMainBinding;
import com.hfad.tvshow.listeners.TVShowsListener;
import com.hfad.tvshow.models.TVShow;
import com.hfad.tvshow.responses.TVShowResponse;
import com.hfad.tvshow.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements TVShowsListener {
    private ActivityMainBinding mainBinding;
    private MostPopularTVShowsViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        doInitialization();
    }

    private void doInitialization() {
        mainBinding.tvshowRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.tvshowRecyclerview.setHasFixedSize(true);

        tvShowsAdapter = new TVShowsAdapter(tvShows, this);
        mainBinding.tvshowRecyclerview.setAdapter(tvShowsAdapter);

        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);

        mainBinding.tvshowRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(mainBinding.tvshowRecyclerview.canScrollVertically(1)) {
                    if(currentPage <= totalAvailablePage) {
                        currentPage+=1;
                        getMostPopularTVShows();
                    }
                }
            }
        });

        mainBinding.imgWatchlist.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, WatchlistActivity.class));
        });

        mainBinding.imgSearch.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        getMostPopularTVShows();
    }

    private void getMostPopularTVShows() {
        toggleLoading();
        viewModel.getMostPopularTVShows(currentPage).observe(this, new Observer<TVShowResponse>() {

            @Override
            public void onChanged(TVShowResponse tvShowResponse) {
                toggleLoading();
                if(tvShowResponse!=null) {
                    totalAvailablePage = tvShowResponse.getTotalPages();
                    if(tvShowResponse.getTvShows()!=null) {
                        int oldCount = tvShows.size();
                        tvShows.addAll(tvShowResponse.getTvShows());
                        tvShowsAdapter.setTvShows(tvShows);
                        tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
                    }
                }
            }

        });
    }

    private void toggleLoading() {
        if(currentPage == 1) {
            if(mainBinding.getIsLoading() != null && mainBinding.getIsLoading()) {
                mainBinding.setIsLoading(false);
            } else {
                mainBinding.setIsLoading(true);
            }
        } else {
            if(mainBinding.getIsLoadingMore() != null && mainBinding.getIsLoadingMore()) {
                mainBinding.setIsLoadingMore(false);
            } else {
                mainBinding.setIsLoadingMore(true);
            }
        }
    }


    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = TVShowDetailsActivity.newIntent(MainActivity.this, tvShow);
        startActivity(intent);
    }
}
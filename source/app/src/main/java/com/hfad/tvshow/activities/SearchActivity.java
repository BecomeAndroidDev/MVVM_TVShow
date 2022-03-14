package com.hfad.tvshow.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.hfad.tvshow.R;
import com.hfad.tvshow.adapters.TVShowsAdapter;
import com.hfad.tvshow.databinding.ActivitySearchBinding;
import com.hfad.tvshow.listeners.TVShowsListener;
import com.hfad.tvshow.models.TVShow;
import com.hfad.tvshow.responses.TVShowResponse;
import com.hfad.tvshow.viewmodels.MostPopularTVShowsViewModel;
import com.hfad.tvshow.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity 
        implements TVShowsListener {
    private ActivitySearchBinding searchBinding;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private SearchViewModel viewModel;
    private int currentPage = 1;
    private int totalAvailablePage = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(searchBinding.getRoot());

        doInitialization();
    }
    
    void doInitialization() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        searchBinding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        tvShowsAdapter = new TVShowsAdapter(tvShows, SearchActivity.this);
        searchBinding.tvShowRecyclerview.setHasFixedSize(true);
        searchBinding.tvShowRecyclerview.setAdapter(tvShowsAdapter);

        searchBinding.imageSearch.setOnClickListener(view -> {
            currentPage = 1;
            totalAvailablePage = 1;
            searchTVShows();
        });

        searchBinding.tvShowRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(searchBinding.tvShowRecyclerview.canScrollVertically(1)) {
                    if(currentPage <= totalAvailablePage) {
                        currentPage+=1;
                        searchTVShows();
                    }
                }
            }
        });

        searchBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(timer!=null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().trim().isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper())
                                .post(new Runnable() {
                                    @Override
                                    public void run() {
                                        currentPage = 1;
                                        totalAvailablePage = 1;
                                        searchTVShows();
                                    }
                                });
                        }
                    }, 800);
                }
                else {
                    tvShows.clear();
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void searchTVShows() {
        toggleLoading();
        String query = searchBinding.inputSearch.getText().toString().trim();
        viewModel.searchTVShows(query, currentPage).observe(this, new Observer<TVShowResponse>() {

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
            if(searchBinding.getIsLoading() != null && searchBinding.getIsLoading()) {
                searchBinding.setIsLoading(false);
            } else {
                searchBinding.setIsLoading(true);
            }
        } else {
            if(searchBinding.getIsLoadingMore() != null && searchBinding.getIsLoadingMore()) {
                searchBinding.setIsLoadingMore(false);
            } else {
                searchBinding.setIsLoadingMore(true);
            }
        }
    }


    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = TVShowDetailsActivity.newIntent(SearchActivity.this, tvShow);
        startActivity(intent);
    }
}
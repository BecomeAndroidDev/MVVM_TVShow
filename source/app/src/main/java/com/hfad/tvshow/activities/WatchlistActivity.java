package com.hfad.tvshow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hfad.tvshow.adapters.WatchlistAdapter;
import com.hfad.tvshow.databinding.ActivityWatchlistBinding;
import com.hfad.tvshow.listeners.WatchlistListener;
import com.hfad.tvshow.models.TVShow;
import com.hfad.tvshow.utilities.TempDataHolder;
import com.hfad.tvshow.viewmodels.WatchlistViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchlistActivity extends AppCompatActivity
    implements WatchlistListener {
    private ActivityWatchlistBinding activityWatchlistBinding;
    private WatchlistViewModel watchlistViewModel;
    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchlistBinding = ActivityWatchlistBinding.inflate(getLayoutInflater());
        setContentView(activityWatchlistBinding.getRoot());
        doInitialization();
    }

    private void doInitialization() {
        watchlistViewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        activityWatchlistBinding.imageBack.setOnClickListener(view -> onBackPressed());
        watchList = new ArrayList<>();
        loadWatchlists();
    }

    private void loadWatchlists() {
        activityWatchlistBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(watchlistViewModel.loadWatchlist()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    activityWatchlistBinding.setIsLoading(false);
                    if(watchList.size() > 0) {
                        watchList.clear();
                    }
                    watchList.addAll(tvShows);
                    watchlistAdapter = new WatchlistAdapter(watchList, WatchlistActivity.this);
                    activityWatchlistBinding.watchlistRecyclerview.setAdapter(watchlistAdapter);
                    activityWatchlistBinding.watchlistRecyclerview.setVisibility(View.VISIBLE);
                    compositeDisposable.dispose();
                })
        );
    }

    protected void onResume() {
        super.onResume();
        if(TempDataHolder.IS_WATCHLIST_UPDATED) {
            loadWatchlists();
            TempDataHolder.IS_WATCHLIST_UPDATED = false;
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = TVShowDetailsActivity.newIntent(WatchlistActivity.this, tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTVShowFromWatchlist(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                watchlistViewModel.removeTVShowFromWatchlist(tvShow)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    watchList.remove(tvShow);
                    watchlistAdapter.notifyItemRemoved(position);
                    watchlistAdapter.notifyItemRangeChanged(position, watchlistAdapter.getItemCount());
                    compositeDisposable.dispose();
                })
        );
    }
}
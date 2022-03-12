package com.hfad.tvshow.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hfad.tvshow.database.TVShowsDatabase;
import com.hfad.tvshow.models.TVShow;
import com.hfad.tvshow.models.TVShowDetails;
import com.hfad.tvshow.repositories.TVShowDetailsRepository;
import com.hfad.tvshow.responses.TVShowDetailsResponse;
import com.hfad.tvshow.responses.TVShowResponse;

import io.reactivex.Completable;

public class TVShowDetailsViewModel extends AndroidViewModel {
    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowsDatabase tvShowsDatabase;

    public TVShowDetailsViewModel(@NonNull Application application) {
        super(application);
        this.tvShowDetailsRepository = new TVShowDetailsRepository();
        tvShowsDatabase = TVShowsDatabase.getTVShowsDatabase(application);
    }

    public LiveData<TVShowDetailsResponse> getTVShowsDetails(String tvShowId) {
        return tvShowDetailsRepository.getTVShowsDetails(tvShowId);
    }

    public Completable addToWatchList(TVShow tvShow) {
        return tvShowsDatabase.tvShowDAO().addToWatchList(tvShow);
    }
}

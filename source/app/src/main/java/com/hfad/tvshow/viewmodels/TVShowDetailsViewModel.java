package com.hfad.tvshow.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hfad.tvshow.models.TVShowDetails;
import com.hfad.tvshow.repositories.TVShowDetailsRepository;
import com.hfad.tvshow.responses.TVShowDetailsResponse;
import com.hfad.tvshow.responses.TVShowResponse;

public class TVShowDetailsViewModel extends ViewModel {
    private TVShowDetailsRepository tvShowDetailsRepository;

    public TVShowDetailsViewModel() {
        this.tvShowDetailsRepository = new TVShowDetailsRepository();
    }

    public LiveData<TVShowDetailsResponse> getTVShowsDetails(String tvShowId) {
        return tvShowDetailsRepository.getTVShowsDetails(tvShowId);
    }
}

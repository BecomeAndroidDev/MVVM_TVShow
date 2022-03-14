package com.hfad.tvshow.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hfad.tvshow.repositories.MostPopularTVShowsRepository;
import com.hfad.tvshow.repositories.SearchTVShowRepository;
import com.hfad.tvshow.responses.TVShowResponse;

public class SearchViewModel extends ViewModel {
    private SearchTVShowRepository searchTVShowRepository;

    public SearchViewModel() {
        searchTVShowRepository = new SearchTVShowRepository();
    }

    public LiveData<TVShowResponse> searchTVShows(String query, int page) {
        return searchTVShowRepository.searchTVShows(query, page);
    }
}

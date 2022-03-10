package com.hfad.tvshow.network;

import com.hfad.tvshow.models.TVShow;
import com.hfad.tvshow.responses.TVShowDetailsResponse;
import com.hfad.tvshow.responses.TVShowResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("most-popular")
    Call<TVShowResponse> getMostPopularTVShows(@Query("page") int page);

    @GET("show-details")
    Call<TVShowDetailsResponse> getTVShowsDetails(@Query("q") String tvShowId);

}

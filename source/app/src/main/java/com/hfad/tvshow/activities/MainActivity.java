package com.hfad.tvshow.activities;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hfad.tvshow.R;
import com.hfad.tvshow.adapters.TVShowsAdapter;
import com.hfad.tvshow.databinding.ActivityMainBinding;
import com.hfad.tvshow.models.TVShow;
import com.hfad.tvshow.responses.TVShowResponse;
import com.hfad.tvshow.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private MostPopularTVShowsViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;

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

        tvShowsAdapter = new TVShowsAdapter(tvShows);
        mainBinding.tvshowRecyclerview.setAdapter(tvShowsAdapter);

        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        getMostPopularTVShows(0);
    }

    private void getMostPopularTVShows(int page) {
        mainBinding.setIsLoading(true);
        viewModel.getMostPopularTVShows(page).observe(this, new Observer<TVShowResponse>() {

            @Override
            public void onChanged(TVShowResponse tvShowResponse) {
                mainBinding.setIsLoading(false);
                if(tvShowResponse!=null) {
                    tvShows.addAll(tvShowResponse.getTvShows());
                    tvShowsAdapter.setTvShows(tvShows);
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }

        });
    }
}
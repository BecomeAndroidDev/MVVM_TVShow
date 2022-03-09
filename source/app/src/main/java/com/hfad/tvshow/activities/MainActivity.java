package com.hfad.tvshow.activities;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.hfad.tvshow.R;
import com.hfad.tvshow.responses.TVShowResponse;
import com.hfad.tvshow.viewmodels.MostPopularTVShowsViewModel;

public class MainActivity extends AppCompatActivity {

    private MostPopularTVShowsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        getMostPopularTVShows(0);
    }

    private void getMostPopularTVShows(int page) {
        viewModel.getMostPopularTVShows(page).observe(this, new Observer<TVShowResponse>() {

            @Override
            public void onChanged(TVShowResponse tvShowResponse) {
                Toast.makeText(getApplicationContext(),
                        "Total Pages: " + tvShowResponse.getTotalPages(),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }
}
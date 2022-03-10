package com.hfad.tvshow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hfad.tvshow.R;
import com.hfad.tvshow.databinding.ActivityTvshowDetailsBinding;
import com.hfad.tvshow.models.TVShow;
import com.hfad.tvshow.responses.TVShowDetailsResponse;
import com.hfad.tvshow.viewmodels.TVShowDetailsViewModel;

public class TVShowDetailsActivity extends AppCompatActivity {
    public static String EXTRA_TV_SHOW_ID = "id";
    public static String EXTRA_TV_SHOW_NAME = "name";
    public static String EXTRA_TV_SHOW_START_DATE = "start_date";
    public static String EXTRA_TV_COUNTRY = "country";
    public static String EXTRA_TV_NETWORK = "network";
    public static String EXTRA_TV_STATUS = "status";

    private ActivityTvshowDetailsBinding mBinding;
    private TVShowDetailsViewModel mViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityTvshowDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        
        doInitialization();
    }

    private void doInitialization() {
        mViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        getTVShowDetails();
    }

    private void getTVShowDetails() {
        mBinding.setIsLoading(true);
        String tvShowId = getIntent().getStringExtra(EXTRA_TV_SHOW_ID);
        mViewModel.getTVShowsDetails(tvShowId).observe(this, new Observer<TVShowDetailsResponse>() {
            @Override
            public void onChanged(TVShowDetailsResponse tvShowDetailsResponse) {
                mBinding.setIsLoading(false);
                Toast.makeText(getApplicationContext(),
                        tvShowDetailsResponse.getTvShowDetails().getUrl(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Intent newIntent(Context context, TVShow tvShow) {
        Intent intent = new Intent(context, TVShowDetailsActivity.class);
        intent.putExtra(EXTRA_TV_SHOW_ID, tvShow.getId());
        intent.putExtra(EXTRA_TV_SHOW_NAME, tvShow.getName());
        intent.putExtra(EXTRA_TV_SHOW_START_DATE, tvShow.getStartDate());
        intent.putExtra(EXTRA_TV_COUNTRY, tvShow.getCountry());
        intent.putExtra(EXTRA_TV_NETWORK, tvShow.getNetwork());
        intent.putExtra(EXTRA_TV_STATUS, tvShow.getStatus());
        return intent;
    }
}
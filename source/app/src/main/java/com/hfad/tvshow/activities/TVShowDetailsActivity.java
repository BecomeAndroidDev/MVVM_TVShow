package com.hfad.tvshow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hfad.tvshow.R;
import com.hfad.tvshow.adapters.ImageSliderAdapter;
import com.hfad.tvshow.databinding.ActivityTvshowDetailsBinding;
import com.hfad.tvshow.models.TVShow;
import com.hfad.tvshow.models.TVShowDetails;
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
                TVShowDetails tvShowDetails = tvShowDetailsResponse.getTvShowDetails();
                if(tvShowDetails != null) {
                    if(tvShowDetails.getPictures() != null) {
                        loadImageSlider(tvShowDetails.getPictures());
                    }
                }
            }
        });
    }

    private void loadImageSlider(String[] sliderImages) {
        mBinding.sliderViewpager.setOffscreenPageLimit(1);
        mBinding.sliderViewpager.setAdapter(new ImageSliderAdapter(sliderImages));
        mBinding.sliderViewpager.setVisibility(View.VISIBLE);
        mBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        mBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicator(sliderImages.length);
        mBinding.sliderViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicator(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8,0);
        for(int i=0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                getApplicationContext(), R.drawable.background_slider_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            mBinding.layoutSliderIndicator.addView(indicators[i]);
        }
        mBinding.layoutSliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = mBinding.layoutSliderIndicator.getChildCount();
        for(int i=0; i < childCount; i++) {
            ImageView imageView = (ImageView) mBinding.layoutSliderIndicator.getChildAt(i);
            if(i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.background_slider_indicator_active
                ));
            }
            else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.background_slider_indicator_inactive
                ));
            }
        }
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
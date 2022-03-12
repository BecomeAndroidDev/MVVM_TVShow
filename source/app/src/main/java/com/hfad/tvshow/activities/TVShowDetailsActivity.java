package com.hfad.tvshow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hfad.tvshow.R;
import com.hfad.tvshow.adapters.EpisodesAdapter;
import com.hfad.tvshow.adapters.ImageSliderAdapter;
import com.hfad.tvshow.databinding.ActivityTvshowDetailsBinding;
import com.hfad.tvshow.databinding.LayoutEpisodesBottomSheetBinding;
import com.hfad.tvshow.models.TVShow;
import com.hfad.tvshow.models.TVShowDetails;
import com.hfad.tvshow.responses.TVShowDetailsResponse;
import com.hfad.tvshow.viewmodels.TVShowDetailsViewModel;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {
    public static String EXTRA_TV_SHOW = "tv_show";

    private ActivityTvshowDetailsBinding activityTvshowDetailsBinding;
    private TVShowDetailsViewModel mViewModel;
    private BottomSheetDialog episodesBottomSheet;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowDetailsBinding = ActivityTvshowDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityTvshowDetailsBinding.getRoot());

        doInitialization();
    }

    private void doInitialization() {
        mViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTvshowDetailsBinding.imageBack.setOnClickListener(view -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra(EXTRA_TV_SHOW);
        getTVShowDetails();
    }

    private void getTVShowDetails() {
        activityTvshowDetailsBinding.setIsLoading(true);
        String tvShowId = tvShow.getId();
        mViewModel.getTVShowsDetails(tvShowId).observe(this, new Observer<TVShowDetailsResponse>() {
            @Override
            public void onChanged(TVShowDetailsResponse tvShowDetailsResponse) {
                activityTvshowDetailsBinding.setIsLoading(false);
                TVShowDetails tvShowDetails = tvShowDetailsResponse.getTvShowDetails();
                if (tvShowDetails != null) {
                    if (tvShowDetails.getPictures() != null) {
                        loadImageSlider(tvShowDetails.getPictures());
                    }
                    activityTvshowDetailsBinding.setTvShowImageURL(tvShowDetails.getImagePath());
                    activityTvshowDetailsBinding.imageTvShow.setVisibility(View.VISIBLE);

                    activityTvshowDetailsBinding.setDescription(String.valueOf(
                            HtmlCompat.fromHtml(tvShowDetails.getDescription()
                                    , HtmlCompat.FROM_HTML_MODE_COMPACT)
                    ));
                    activityTvshowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                    activityTvshowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                    activityTvshowDetailsBinding.textReadMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (activityTvshowDetailsBinding.textReadMore.getText().toString().equals(
                                    getResources().getString(R.string.read_more))) {
                                activityTvshowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                                activityTvshowDetailsBinding.textDescription.setEllipsize(null);
                                activityTvshowDetailsBinding.textReadMore.setText(R.string.read_less);
                            } else {
                                activityTvshowDetailsBinding.textDescription.setMaxLines(4);
                                activityTvshowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                                activityTvshowDetailsBinding.textReadMore.setText(R.string.read_more);
                            }
                        }
                    });

                    activityTvshowDetailsBinding.setRating(tvShowDetails.getRating());
                    if (tvShowDetails.getGenres() != null) {
                        activityTvshowDetailsBinding.setGenre(tvShowDetails.getGenres()[0]);
                    } else {
                        activityTvshowDetailsBinding.setGenre("N/A");
                    }
                    activityTvshowDetailsBinding.setRuntime(tvShowDetails.getRuntime() + " Min");
                    activityTvshowDetailsBinding.viewDivider.setVisibility(View.VISIBLE);
                    activityTvshowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                    activityTvshowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);

                    activityTvshowDetailsBinding.buttonWebsite.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(tvShowDetails.getUrl()));
                        startActivity(intent);
                    });
                    activityTvshowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                    activityTvshowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                    activityTvshowDetailsBinding.buttonEpisodes.setOnClickListener(view -> {
                        if (episodesBottomSheet == null) {
                            episodesBottomSheet = new BottomSheetDialog(
                                    TVShowDetailsActivity.this);
                            layoutEpisodesBottomSheetBinding = LayoutEpisodesBottomSheetBinding.inflate(
                                    getLayoutInflater()
                            );
                            episodesBottomSheet.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                            layoutEpisodesBottomSheetBinding.episodesRecyclerview.setAdapter(
                                    new EpisodesAdapter(tvShowDetails.getEpisodes())
                            );
                            layoutEpisodesBottomSheetBinding.textTitle.setText(
                                    String.format("Episodes | %s", tvShow.getName())
                            );
                            layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(view1 -> episodesBottomSheet.dismiss());
                        }

                        ///-- Optional selection start --///
                        FrameLayout frameLayout = episodesBottomSheet.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet
                        );
                        if(frameLayout != null) {
                            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        ///-- Optional selection end --///
                        episodesBottomSheet.show();
                    });

                    activityTvshowDetailsBinding.imageWatchList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new CompositeDisposable().add(
                                    mViewModel.addToWatchList(tvShow)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        activityTvshowDetailsBinding.imageWatchList.setImageResource(R.drawable.ic_check);
                                        Toast.makeText(getApplicationContext(), "Added to watch list", Toast.LENGTH_SHORT).show();
                                    })
                            );
                        }
                    });
                    activityTvshowDetailsBinding.imageWatchList.setVisibility(View.VISIBLE);

                    loadBasicTVShowDetails();
                }
            }
        });
    }

    private void loadImageSlider(String[] sliderImages) {
        activityTvshowDetailsBinding.sliderViewpager.setOffscreenPageLimit(1);
        activityTvshowDetailsBinding.sliderViewpager.setAdapter(new ImageSliderAdapter(sliderImages));
        activityTvshowDetailsBinding.sliderViewpager.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicator(sliderImages.length);
        activityTvshowDetailsBinding.sliderViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.background_slider_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            activityTvshowDetailsBinding.layoutSliderIndicator.addView(indicators[i]);
        }
        activityTvshowDetailsBinding.layoutSliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = activityTvshowDetailsBinding.layoutSliderIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTvshowDetailsBinding.layoutSliderIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.background_slider_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.background_slider_indicator_inactive
                ));
            }
        }
    }

    private void loadBasicTVShowDetails() {
        Intent intent = getIntent();
        activityTvshowDetailsBinding.setTvShowName(tvShow.getName());
        activityTvshowDetailsBinding.setStartedDate(tvShow.getStartDate());
        activityTvshowDetailsBinding.setStatus(tvShow.getStatus());
        activityTvshowDetailsBinding.setNetworkCountry(
                tvShow.getNetwork() + " ("
                        + tvShow.getCountry() + " )");

        activityTvshowDetailsBinding.tvShowName.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStartedDate.setVisibility(View.VISIBLE);
    }

    public static Intent newIntent(Context context, TVShow tvShow) {
        Intent intent = new Intent(context, TVShowDetailsActivity.class);
        intent.putExtra(EXTRA_TV_SHOW, tvShow);
        return intent;
    }
}
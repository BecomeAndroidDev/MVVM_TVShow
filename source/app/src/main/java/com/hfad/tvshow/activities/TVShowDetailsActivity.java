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

public class TVShowDetailsActivity extends AppCompatActivity {
    public static String EXTRA_TV_SHOW_ID = "id";
    public static String EXTRA_TV_SHOW_NAME = "name";
    public static String EXTRA_TV_SHOW_START_DATE = "start_date";
    public static String EXTRA_TV_SHOW_COUNTRY = "country";
    public static String EXTRA_TV_SHOW_NETWORK = "network";
    public static String EXTRA_TV_SHOW_STATUS = "status";

    private ActivityTvshowDetailsBinding activityTvshowDetailsBinding;
    private TVShowDetailsViewModel mViewModel;
    private BottomSheetDialog episodesBottomSheet;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;

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
        getTVShowDetails();
    }

    private void getTVShowDetails() {
        activityTvshowDetailsBinding.setIsLoading(true);
        String tvShowId = getIntent().getStringExtra(EXTRA_TV_SHOW_ID);
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
                                    String.format("Episodes | %s", getIntent().getStringExtra(EXTRA_TV_SHOW_NAME))
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
        activityTvshowDetailsBinding.setTvShowName(intent.getStringExtra(EXTRA_TV_SHOW_NAME));
        activityTvshowDetailsBinding.setStartedDate(intent.getStringExtra(EXTRA_TV_SHOW_START_DATE));
        activityTvshowDetailsBinding.setStatus(intent.getStringExtra(EXTRA_TV_SHOW_STATUS));
        activityTvshowDetailsBinding.setNetworkCountry(
                intent.getStringExtra(EXTRA_TV_SHOW_NETWORK) + " ("
                        + intent.getStringExtra(EXTRA_TV_SHOW_COUNTRY) + " )");

        activityTvshowDetailsBinding.tvShowName.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStartedDate.setVisibility(View.VISIBLE);
    }

    public static Intent newIntent(Context context, TVShow tvShow) {
        Intent intent = new Intent(context, TVShowDetailsActivity.class);
        intent.putExtra(EXTRA_TV_SHOW_ID, tvShow.getId());
        intent.putExtra(EXTRA_TV_SHOW_NAME, tvShow.getName());
        intent.putExtra(EXTRA_TV_SHOW_START_DATE, tvShow.getStartDate());
        intent.putExtra(EXTRA_TV_SHOW_COUNTRY, tvShow.getCountry());
        intent.putExtra(EXTRA_TV_SHOW_NETWORK, tvShow.getNetwork());
        intent.putExtra(EXTRA_TV_SHOW_STATUS, tvShow.getStatus());
        return intent;
    }
}
package com.hfad.tvshow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.tvshow.databinding.ItemContainerTvshowBinding;
import com.hfad.tvshow.listeners.TVShowsListener;
import com.hfad.tvshow.models.TVShow;

import java.util.List;

public class TVShowsAdapter extends RecyclerView.Adapter<TVShowsAdapter.TVShowsHolder>{
    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private TVShowsListener tvShowsListener;

    public TVShowsAdapter(List<TVShow> tvShows, TVShowsListener tvShowsListener) {
        this.tvShows = tvShows;
        this.tvShowsListener = tvShowsListener;
    }

    public void setTvShows(List<TVShow> tvShows) {
        this.tvShows = tvShows;
    }

    @NonNull
    @Override
    public TVShowsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemContainerTvshowBinding binding = ItemContainerTvshowBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new TVShowsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowsHolder holder, int position) {
        holder.bindingTVShow(tvShows.get(position));
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    class TVShowsHolder extends RecyclerView.ViewHolder{
        private ItemContainerTvshowBinding mBinding;

        public TVShowsHolder(@NonNull ItemContainerTvshowBinding binding) {
            super(binding.getRoot());
            mBinding= binding;
        }

        public void bindingTVShow(TVShow tvShow) {
            mBinding.setTvShow(tvShow);
            mBinding.executePendingBindings();
            mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvShowsListener.onTVShowsClicked(tvShow);
                }
            });
        }
    }
}

package com.hfad.tvshow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.tvshow.databinding.ItemContainerEposideBinding;
import com.hfad.tvshow.models.Episode;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder>{
    private List<Episode> episodes;

    public EpisodesAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemContainerEposideBinding binding = ItemContainerEposideBinding.inflate(inflater,
                parent, false);

        return new EpisodesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        holder.bindingEpisode(episodes.get(position));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    static class EpisodesViewHolder extends RecyclerView.ViewHolder {
        private ItemContainerEposideBinding mBinding;
        public EpisodesViewHolder(@NonNull ItemContainerEposideBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindingEpisode(Episode episode) {
            String title = "S";
            String season = episode.getSeason();
            if(season.length() == 1) {
                season = "0".concat(season);
            }

            String episodeNumber = episode.getEpisode();
            if(episodeNumber.length() == 1) {
                episodeNumber = "0".concat(episodeNumber);
            }
            episodeNumber = "E".concat(episodeNumber);
            title = title.concat(season).concat(episodeNumber);

            mBinding.setTitle(title);
            mBinding.setName(episode.getName());
            mBinding.setAirDate(episode.getAirDate());
        }
    }
}

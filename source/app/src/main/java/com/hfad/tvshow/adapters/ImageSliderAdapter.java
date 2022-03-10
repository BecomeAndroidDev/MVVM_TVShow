package com.hfad.tvshow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.tvshow.databinding.ItemContainerSliderImageBinding;
import com.hfad.tvshow.databinding.ItemContainerTvshowBinding;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>{
    private String[] images;

    public ImageSliderAdapter(String[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemContainerSliderImageBinding binding = ItemContainerSliderImageBinding.inflate(
                inflater, parent, false);
        return new ImageSliderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        holder.bindingSliderImage(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    static class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        private ItemContainerSliderImageBinding mBinding;
        public ImageSliderViewHolder(@NonNull ItemContainerSliderImageBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindingSliderImage(String imageUrl) {
            mBinding.setImageUrl(imageUrl);
        }
    }

}

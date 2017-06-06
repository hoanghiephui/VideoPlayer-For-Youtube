package com.video.youtuberplayer.ui.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.video.youtuberplayer.R;

/**
 * Created by hoanghiep on 6/6/17.
 */

public abstract class FooterAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected abstract boolean hasFooter();

    protected boolean isFooter(int position) {
        return hasFooter() && position == getItemCount() - 1;
    }

    @Override
    public int getItemCount() {
        return getRealItemCount() + (hasFooter() ? 1 : 0);
    }

    public abstract int getRealItemCount();

    /**
     * Basic ViewHolder for {@link FooterAdapter}. This holder is used to fill the location of
     * navigation bar.
     */
    protected static class FooterHolder extends RecyclerView.ViewHolder {

        private FooterHolder(View itemView) {
            super(itemView);
        }

        public static FooterHolder buildInstance(ViewGroup parent) {
            return new FooterHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false));
        }
    }
}


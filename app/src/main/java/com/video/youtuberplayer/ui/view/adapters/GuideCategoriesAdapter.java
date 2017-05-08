package com.video.youtuberplayer.ui.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.api.services.youtube.model.GuideCategory;
import com.video.youtuberplayer.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hoanghiep on 5/6/17.
 */

public class GuideCategoriesAdapter extends RecyclerView.Adapter<GuideCategoriesAdapter.GuideCategoriesViewHolder> {
  private List<GuideCategory> mCategories;
  private GuideCategoriesCallBack mCallbacks;
  private int mLayoutID;

  public GuideCategoriesAdapter(List<GuideCategory> categories, GuideCategoriesCallBack callbacks, int layoutID) {
    this.mCategories = categories;
    this.mCallbacks = callbacks;
    this.mLayoutID = layoutID;
  }

  @Override
  public GuideCategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new GuideCategoriesViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutID, parent, false));
  }

  @Override
  public void onBindViewHolder(GuideCategoriesViewHolder holder, int position) {
    holder.bindGuideCategory(mCategories.get(position));
  }

  @Override
  public int getItemCount() {
    return mCategories.size();
  }

  class GuideCategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.movie_deails_item_default_riple)
    MaterialRippleLayout mRippleView;
    @BindView(R.id.movie_deails_item_default_text_view)
    TextView mGeneroNameTextView;

    private GuideCategory categories;

    public GuideCategoriesViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      mRippleView.setOnClickListener(this);
    }

    public void bindGuideCategory(GuideCategory item) {
      this.categories = item;
      mGeneroNameTextView.setText(categories.getSnippet().getTitle());
    }

    @Override
    public void onClick(View view) {
      mCallbacks.onItemSelected(categories);
    }
  }

  public interface GuideCategoriesCallBack {
    void onItemSelected(GuideCategory item);
  }
}

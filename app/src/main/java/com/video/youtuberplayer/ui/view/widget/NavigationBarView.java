package com.video.youtuberplayer.ui.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.video.youtuberplayer.utils.DisplayUtils;

/**
 * Created by hoanghiep on 6/6/17.
 */

public class NavigationBarView extends View {

    public NavigationBarView(Context context) {
        super(context);
    }

    public NavigationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                DisplayUtils.getNavigationBarHeight(getResources()));
    }
}

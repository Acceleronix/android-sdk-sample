package com.acceleronix.app.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.acceleronix.app.demo.utils.DensityUtils;
public class BottomItemDecorationSystem extends RecyclerView.ItemDecoration {

    private Context mContext;
    public BottomItemDecorationSystem(Context context) {
        mContext = context;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom= DensityUtils.dp2px(mContext,1);

    }
}
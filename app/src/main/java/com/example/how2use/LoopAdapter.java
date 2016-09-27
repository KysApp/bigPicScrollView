package com.example.how2use;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bigpicscrollview.adapter.LoopPagerAdapter;
import com.example.bigpicscrollview.view.RollPagerView;

/**
 * Created by lhl on 2016/9/23.
 */

public class LoopAdapter extends LoopPagerAdapter{
    private int[] colors = {
            Color.BLUE,
            Color.GREEN,
            Color.RED,
            Color.YELLOW,
    };
    public LoopAdapter(RollPagerView viewPager) {
        super(viewPager);
    }

    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
//        view.setImageResource(imgs[position]);
        view.setBackgroundColor(colors[position]);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public int getRealCount() {
        return colors.length;
    }
}

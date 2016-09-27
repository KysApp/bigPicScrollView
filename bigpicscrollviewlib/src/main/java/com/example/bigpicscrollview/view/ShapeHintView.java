package com.example.bigpicscrollview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bigpicscrollview.interfaces.HintView;

import java.util.Map;

public abstract class ShapeHintView extends LinearLayout implements HintView {
    private ImageView[] mDots;
    private int length = 0;
    private int lastPosition = 0;

    private int dot_normal;
    private int dot_focus;

    public ShapeHintView(Context context) {
        super(context);
    }

    public ShapeHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public abstract Map<String,Integer> setHintViewBG();

    @Override
    public void initView(int length, int gravity, int spacing) {
        removeAllViews();
        lastPosition = 0;
        setOrientation(HORIZONTAL);
        switch (gravity) {
            case 0:
                setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;
            case 1:
                setGravity(Gravity.CENTER);
                break;
            case 2:
                setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                break;
        }

        this.length = length;
        mDots = new ImageView[length];

        Map<String,Integer> map = setHintViewBG();
        dot_focus = map.get("focusId");
        dot_normal = map.get("normalId");

        for (int i = 0; i < length; i++) {
            mDots[i] = new ImageView(getContext());
            LayoutParams dotlp;
            dotlp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            dotlp.setMargins(spacing/2, 0, spacing/2, 0);
            mDots[i].setLayoutParams(dotlp);
            mDots[i].setImageResource(dot_normal);
            addView(mDots[i]);
        }

        setCurrent(0);
    }

    @Override
    public void setCurrent(int current) {
        if (current < 0 || current > length - 1) {
            return;
        }
        mDots[lastPosition].setImageResource(dot_normal);
        mDots[current].setImageResource(dot_focus);
        lastPosition = current;
    }
}

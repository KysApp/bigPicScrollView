package com.example.bigpicscrollview.interfaces;

/**
 * Created by Administrator on 2016/9/27.
 */

public interface HintViewDelegate {
    void setCurrentPosition(int position, HintView hintView);

    void initView(int length, int gravity, int spacing, HintView hintView);
}

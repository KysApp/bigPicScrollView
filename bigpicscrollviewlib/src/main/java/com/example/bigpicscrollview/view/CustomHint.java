package com.example.bigpicscrollview.view;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lhl on 2016/9/26.
 */

public class CustomHint extends ShapeHintView {
    private Map<String,Integer> resid = new HashMap<>();
    public CustomHint(Context context, int focusResId, int normalResId) {
        super(context);
        resid.put("focusId",focusResId);
        resid.put("normalId",normalResId);
    }

    @Override
    public Map<String,Integer> setHintViewBG() {
        return resid;
    }
}

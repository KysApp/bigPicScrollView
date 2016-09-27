package com.example.how2use;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.bigpicscrollview.R;
import com.example.bigpicscrollview.interfaces.OnItemClickListener;
import com.example.bigpicscrollview.view.CustomHint;
import com.example.bigpicscrollview.view.RollPagerView;

public class MainActivity extends AppCompatActivity {
    private RollPagerView mRollViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        initView();
    }

    private void initView() {
        mRollViewPager = (RollPagerView) findViewById(R.id.rpv_rollpagerview);
        /*
        public CustomHint(Context context, int focusResId, int normalResId)
        focusResId 选中点的drawable id
        normalResId 未选中点的drawable id
         */
        CustomHint customHint = new CustomHint(this, R.drawable.point_focus, R.drawable.point_normal);
//        mRollViewPager.setHintView(null);//hide the indicator
        mRollViewPager.setAdapter(new LoopAdapter(mRollViewPager)).setHintView(customHint)
                .setHintPadding(0, 0, 0, 30)
                .setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

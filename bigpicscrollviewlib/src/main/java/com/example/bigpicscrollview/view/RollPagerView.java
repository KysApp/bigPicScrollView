package com.example.bigpicscrollview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.example.bigpicscrollview.R;
import com.example.bigpicscrollview.adapter.LoopPagerAdapter;
import com.example.bigpicscrollview.interfaces.HintView;
import com.example.bigpicscrollview.interfaces.HintViewDelegate;
import com.example.bigpicscrollview.interfaces.OnItemClickListener;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 支持轮播和提示的的viewpager
 */
public class RollPagerView extends RelativeLayout implements OnPageChangeListener {

    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private OnItemClickListener mOnItemClickListener;
    private GestureDetector mGestureDetector;

    private long mRecentTouchTime;
    //播放延迟
    private int delay;

    //hint位置
    private int gravity;

    //hint颜色
    private int color;

    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int spacing;

    private View mHintView;
    private Timer timer;

    private HintViewDelegate mHintViewDelegate = new HintViewDelegate() {
        @Override
        public void setCurrentPosition(int position, HintView hintView) {
            if (hintView != null)
                hintView.setCurrent(position);
        }

        @Override
        public void initView(int length, int gravity, int spacing, HintView hintView) {
            if (hintView != null)
                hintView.initView(length, gravity, spacing);
        }
    };


    public RollPagerView(Context context) {
        this(context, null);
    }

    public RollPagerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public RollPagerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
    }

    /**
     * 读取提示形式，提示位置以及播放延迟
     *
     * @param attrs 获取在xml中设置的参数
     */
    private void initView(AttributeSet attrs) {
        if (mViewPager != null) {
            removeView(mViewPager);
        }

        TypedArray type = getContext().obtainStyledAttributes(attrs, R.styleable.RollViewPager);
        gravity = type.getInteger(R.styleable.RollViewPager_rollviewpager_hint_gravity, 1);
        delay = type.getInt(R.styleable.RollViewPager_rollviewpager_play_delay, 0);
        color = type.getColor(R.styleable.RollViewPager_rollviewpager_hint_color, Color.parseColor("#00000000"));
        paddingLeft = (int) type.getDimension(R.styleable.RollViewPager_rollviewpager_hint_paddingLeft, 0);
        paddingRight = (int) type.getDimension(R.styleable.RollViewPager_rollviewpager_hint_paddingRight, 0);
        paddingTop = (int) type.getDimension(R.styleable.RollViewPager_rollviewpager_hint_paddingTop, 0);
        paddingBottom = (int) type.getDimension(R.styleable.RollViewPager_rollviewpager_hint_paddingBottom, 0);
        spacing = (int) type.getDimension(R.styleable.RollViewPager_rollviewpager_hint_spacing, 10);
        mViewPager = new ViewPager(getContext());
        mViewPager.setId(R.id.viewpager_inner);
        mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mViewPager);
        type.recycle();
        //手势处理
        mGestureDetector = new GestureDetector(getContext(), new SimpleOnGesture());
    }

    private class SimpleOnGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mOnItemClickListener != null) {
                if (mAdapter instanceof LoopPagerAdapter) {
                    mOnItemClickListener.onItemClick(mViewPager.getCurrentItem() % ((LoopPagerAdapter) mAdapter).getRealCount());
                } else {
                    mOnItemClickListener.onItemClick(mViewPager.getCurrentItem());
                }
            }
            return super.onSingleTapUp(e);
        }
    }

    private final static class TimeTaskHandler extends Handler {
        private WeakReference<RollPagerView> mRollPagerViewWeakReference;

        public TimeTaskHandler(RollPagerView rollPagerView) {
            this.mRollPagerViewWeakReference = new WeakReference<>(rollPagerView);
        }

        @Override
        public void handleMessage(Message msg) {
            RollPagerView rollPagerView = mRollPagerViewWeakReference.get();
            int cur = rollPagerView.getViewPager().getCurrentItem() + 1;
            if (cur >= rollPagerView.mAdapter.getCount()) {
                cur = 0;
            }
            rollPagerView.getViewPager().setCurrentItem(cur);
            rollPagerView.mHintViewDelegate.setCurrentPosition(cur, (HintView) rollPagerView.mHintView);
            if (rollPagerView.mAdapter.getCount() <= 1) rollPagerView.stopPlay();

        }
    }

    private TimeTaskHandler mHandler = new TimeTaskHandler(this);

    private static class WeakTimerTask extends TimerTask {
        private WeakReference<RollPagerView> mRollPagerViewWeakReference;

        public WeakTimerTask(RollPagerView mRollPagerView) {
            this.mRollPagerViewWeakReference = new WeakReference<>(mRollPagerView);
        }

        @Override
        public void run() {
            RollPagerView rollPagerView = mRollPagerViewWeakReference.get();
            if (rollPagerView != null) {
                if (rollPagerView.isShown() && System.currentTimeMillis() - rollPagerView.mRecentTouchTime > rollPagerView.delay) {
                    rollPagerView.mHandler.sendEmptyMessage(0);
                }
            } else {
                cancel();
            }
        }
    }

    /**
     * 开始播放
     * 仅当view正在显示 且 触摸等待时间过后 播放
     */
    private void startPlay() {
        if (delay <= 0 || mAdapter == null || mAdapter.getCount() <= 1) {
            return;
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        //用一个timer定时设置当前项为下一项
        timer.schedule(new WeakTimerTask(this), delay, delay);
    }

    private void stopPlay() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 提示点代理
     *
     * @param delegate 代理
     * @return this
     */
    public RollPagerView setHintViewDelegate(HintViewDelegate delegate) {
        this.mHintViewDelegate = delegate;
        return this;
    }

    /**
     * 初始化提示点
     *
     * @param hintview 传入的自定义提示点
     */
    private void initHint(HintView hintview) {
        if (mHintView != null) {
            removeView(mHintView);
        }
        if (hintview == null || !(hintview instanceof HintView)) {
            return;
        }
        mHintView = (View) hintview;
        loadHintView();
    }

    /**
     * 加载hintview的容器
     */
    private void loadHintView() {
        addView(mHintView);
        mHintView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mHintView.setLayoutParams(lp);

        mHintView.setBackgroundColor(color);

        mHintViewDelegate.initView(mAdapter == null ? 0 : mAdapter.getCount(), gravity, spacing, (HintView) mHintView);
    }


    /**
     * 设置viewager滑动动画持续时间
     *
     * @param during 滑动动画持续时间
     * @return this
     */
    public RollPagerView setAnimationDurtion(final int during) {
        try {
            // viePager平移动画事件
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            Scroller mScroller = new Scroller(getContext(),
                    // 动画效果与ViewPager的一致
                    new Interpolator() {
                        public float getInterpolation(float t) {
                            t -= 1.0f;
                            return t * t * t * t * t + 1.0f;
                        }
                    }) {

                @Override
                public void startScroll(int startX, int startY, int dx,
                                        int dy, int duration) {
                    // 如果手工滚动,则加速滚动
                    if (System.currentTimeMillis() - mRecentTouchTime > delay) {
                        duration = during;
                    } else {
                        duration /= 2;
                    }
                    super.startScroll(startX, startY, dx, dy, duration);
                }

                @Override
                public void startScroll(int startX, int startY, int dx,
                                        int dy) {
                    super.startScroll(startX, startY, dx, dy, during);
                }
            };
            mField.set(mViewPager, mScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 在代码中设置大图滚动间隔时间
     *
     * @param delay 间隔时间
     * @return this
     */
    public RollPagerView setPlayDelay(int delay) {
        this.delay = delay;
        startPlay();
        return this;
    }

    /**
     * 滚动大图暂停滚动
     */
    public void pause() {
        stopPlay();
    }

    /**
     * 滚动大图重新开始滚动
     */
    public void resume() {
        startPlay();
    }

    /**
     * 滚动大图是否在滚动
     *
     * @return 滚动大图是否在滚动
     */
    public boolean isPlaying() {
        return timer != null;
    }

    /**
     * 在Activity中对每个滚动大图进行监听
     *
     * @param listener 每个条目被点击时的监听
     * @return this
     */
    public RollPagerView setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        return this;
    }

    /**
     * 设置提示view的位置
     * @param left 左padding
     * @param top 上padding
     * @param right 右padding
     * @param bottom 下padding
     * @return this
     */
    public RollPagerView setHintPadding(int left, int top, int right, int bottom) {
        paddingLeft = left;
        paddingTop = top;
        paddingRight = right;
        paddingBottom = bottom;
        mHintView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        return this;
    }
    /**
     * 设置提示点的间隔
     * @param spacing 提示点间隔
     * @return this
     */
    public RollPagerView setHintSpacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    /**
     * 支持自定义hintview
     * 只需new一个实现HintView的View传进来
     * 会自动将你的view添加到本View里面。重新设置LayoutParams。
     *
     * @param hintview 传入的提示view
     * @return this
     */
    public RollPagerView setHintView(HintView hintview) {

        if (mHintView != null) {
            removeView(mHintView);
        }
        this.mHintView = (View) hintview;
        if (hintview != null && hintview instanceof View) {
            initHint(hintview);
        }
        return this;
    }

    /**
     * 获取真正的Viewpager
     *
     * @return 真正的viewpager
     */
    public ViewPager getViewPager() {
        return mViewPager;
    }

    /**
     * 为滚动大图设置Adapter
     *
     * @param adapter 滚动大图的adapter
     * @return this
     */
    public RollPagerView setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
        mAdapter = adapter;
        dataSetChanged();
        adapter.registerDataSetObserver(new JPagerObserver());
        return this;
    }

    /**
     * 用来实现adapter的notifyDataSetChanged通知HintView变化
     */
    private class JPagerObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }

    private void dataSetChanged() {
        if (mHintView != null) {
            mHintViewDelegate.initView(mAdapter.getCount(), gravity, spacing, (HintView) mHintView);
            mHintViewDelegate.setCurrentPosition(mViewPager.getCurrentItem(), (HintView) mHintView);
        }
        startPlay();
    }

    /**
     * 为了实现触摸时和过后一定时间内不滑动,这里拦截
     *
     * @param ev 手势
     * @return 是否将事件传递给父控件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mRecentTouchTime = System.currentTimeMillis();
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        mHintViewDelegate.setCurrentPosition(arg0, (HintView) mHintView);
    }

}

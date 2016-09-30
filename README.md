BigPicScrollView使用说明
=
#### 声明
        本项目是在RollViewPager这个项目的基础上进行改动，以方便自己对这个项目的研究，维护和使用，非常感谢Jude95的代码
####    [点击这里可以链接到Jude95的源码](https://github.com/Jude95/RollViewPager)

##### lib导入语句：compile 'com.example.bigpicscrollview:bigpicscrollviewlib:1.0.1'

### Step1：在你的ui中添加RollPagerView控件
    例如：
    <com.example.bigpicscrollview.view.RollPagerView
        android:id="@+id/rpv_rollpagerview"
        android:layout_width="match_parent"
        android:layout_height="180dp"/>
    控件可以定制以下参数
        app:rollviewpager_hint_gravity 提示点在大图下方的位置：left、center、right
        app:rollviewpager_hint_paddingRight
        app:rollviewpager_hint_paddingLeft
        app:rollviewpager_hint_paddingTop
        app:rollviewpager_hint_paddingBottom
        app:rollviewpager_play_delay 大图切换时间
        app:rollviewpager_hint_color 提示点父控件的颜色

### Step2：写出RollPagerView的适配器
    需要继承LoopPagerAdapter
    例如：
    public class LoopAdapter extends LoopPagerAdapter{
        public LoopAdapter(RollPagerView viewPager) {
            super(viewPager);
        }
        /**
        * 设置滚动图片参数
        * @param container 当前图片父控件
        * @param position 当前图片位置
        * @return 当前图片
        */
        @Override
        public View getView(ViewGroup container, int position) {
            return null;
        }
        /**
        * 设置滚动图片真实总数
        * @return 图片真实总数
         */
        @Override
        public int getRealCount() {
            return 0;
        }
    }


### Step 3: 创建提示点
    public CustomHint(Context context, int focusResId, int normalResId)
    focusResId 选中点的drawable id
    normalResId 未选中点的drawable id
    例如：
    CustomHint customHint = new CustomHint(this, R.drawable.point_focus, R.drawable.point_normal);

######注意，其中的customHint不一定非要使用lib包中，可以自定义hint，需继承ShapeHintView
######具体重写方法请参照lib中CustomHint的写法

### Step 4：在Activity中设置
    1.通过findViewById找到ui中的RollPagerView控件
    2.通过setAdapter将适配器设置给RollPagerView
    3.通过setHintView将提示点设置给RollPagerView
    例如：
    mRollViewPager = (RollPagerView) findViewById(yourViewId);
    CustomHint customHint = new CustomHint(this, R.drawable.point_focus, R.drawable.point_normal);
    mRollViewPager.setAdapter(new LoopAdapter(mRollViewPager)).setHintView(customHint);

######注意，Step 4中提到的两个set方法必须执行

### Step 5：其它设置
     /**
     * 设置滚动图片参数
     * @param during viewager滑动动画持续时间
     * @return this
     */
     public RollPagerView setAnimationDurtion(final int during)

    /**
    * 设置viewager 滚动间隔时间
    * @param during viewager滚动间隔时间
    * @return this
    */
    public RollPagerView setPlayDelay(int delay)

    /**
    * viewager 暂停滚动
    */
    public void pause()

    /**
    * viewager 重新开始滚动
    */
    public void resume()

    /**
    * viewager 是否在滚动
    */
    public void isPlaying()

    /**
    * 在Activity中对每个图片进行监听
    * @param listener 图片监听，请不要用系统的OnItemClickListener，用本lib封装的OnItemClickListener
    * @return this
    */
    public RollPagerView setOnItemClickListener(OnItemClickListener listener)

    /**
    * 设置提示view的内边距
    * @param left 左内边距
    * @param top 上内边距
    * @param right 右内边距
    * @param bottom 下内边距
    * @return this
    */
    public RollPagerView setHintPadding(int left, int top, int right, int bottom)

###### 具体使用请参考demo或Jude95的代码



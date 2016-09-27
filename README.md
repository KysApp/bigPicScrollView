BigPicScrollView使用说明
=
#### 声明
        本项目是在RollViewPager这个项目的基础上进行改动，以方便自己对这个项目的研究，维护和使用，非常感谢Jude95的代码
        [点击这里可以链接到Jude95的源码](https://github.com/Jude95/RollViewPager)

##### lib导入语句：compile ''

#### Step1：在你的ui中添加RollPagerView控件
    控件可以定制以下参数
        app:rollviewpager_hint_gravity 提示点在大图下方的位置：left、center、right
        app:rollviewpager_hint_paddingRight
        app:rollviewpager_hint_paddingLeft
        app:rollviewpager_hint_paddingTop
        app:rollviewpager_hint_paddingBottom
        app:rollviewpager_play_delay 大图切换时间
        app:rollviewpager_hint_color 提示点父控件的颜色
        app:rollviewpager_hint_alpha 提示点父控件的透明度

#### Step2：写出RollPagerView的适配器
    继承LoopPagerAdapter
    需要重写getView和getRealCount
        在getView中设置图片
        在getRealCount中设置图片总数

#### Step 3: 创建提示点
    public CustomHint(Context context, int focusResId, int normalResId)
    focusResId 选中点的drawable id
    normalResId 未选中点的drawable id

######注意，其中的customHint不一定非要使用lib包中，可以自定义hint，需继承ShapeHintView
######具体重写方法请参照lib中CustomHint的写法

#### Step 4：在Activity中设置
    1.通过findViewById找到ui中的RollPagerView控件
    2.通过setAdapter将适配器设置给RollPagerView
    3.通过setHintView将提示点设置给RollPagerView

######注意，Step 4中提到的两个set方法必须执行

#### Step 5：其它设置
    1.设置viewager滑动动画持续时间
        public RollPagerView setAnimationDurtion(final int during)
    2.设置viewager滚动间隔时间
        public RollPagerView setPlayDelay(int delay)
    3.viewager暂停滚动
        public void pause()
    3.viewager重新开始滚动
        public void resume()
    3.viewager是否在滚动
        public void isPlaying()
    3.在Activity中对每个图片进行监听
        public RollPagerView setOnItemClickListener(OnItemClickListener listener)
    3.设置提示view的位置
        public RollPagerView setHintPadding(int left, int top, int right, int bottom)
    3.设置提示view的透明度
        public RollPagerView setHintAlpha(int alpha)
        alpha 0为全透明  255为实心

###### 具体使用请参考demo或Jude95的代码



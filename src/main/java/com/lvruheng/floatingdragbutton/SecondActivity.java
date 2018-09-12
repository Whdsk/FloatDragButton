package com.lvruheng.floatingdragbutton;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SecondActivity extends BaseActivity {
    private RelativeLayout mFloatBtnWrapper;
    private AbsoluteLayout.LayoutParams mFloatBtnWindowParams;
    private AbsoluteLayout mFloatRootView;
    private RelativeLayout mMainLayout;
    private FloatTouchListener mFloatTouchListener;
    private Rect mFloatViewBoundsInScreens;
    private int mEdgePadding;
    private ImageView mImageView;
    private RelativeLayout rl_root;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mMainLayout = (RelativeLayout) findViewById(R.id.main_layout);
//       // addFloatBtn();
//      //  setTouchListener();
//    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 添加浮动按钮
     */
    private void addFloatBtn() {
        mFloatBtnWrapper = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.float_btn,null,false);
        mImageView = (ImageView) mFloatBtnWrapper.findViewById(R.id.iv_shine);
        rl_root=mFloatBtnWrapper.findViewById(R.id.rl_root);
        mFloatBtnWrapper.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        int width5=getWidgetWidth(mFloatBtnWrapper);
//        int height1=getWidgetHeight(mFloatBtnWrapper);
        mFloatRootView = new AbsoluteLayout(this);
        mMainLayout.addView(mFloatRootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        int width1=getScreenWidth();
//        int width3=mFloatBtnWrapper.getWidth();
//        int width2=getScreenWidth()/2-getWidgetWidth(rl_root)/2;
//        int height=getScreenHeight();
        final float scale = SecondActivity.this.getResources().getDisplayMetrics().density;
        mEdgePadding = (int) (10 * scale + 0.5);

    }

    /**
     * 设置触摸监听
     */
    private void setTouchListener() {

        mFloatRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFloatRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                rl_root.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                int width5=rl_root.getWidth();
                int height1=rl_root.getHeight();
                mFloatViewBoundsInScreens = new Rect();
                int[] mainLocation = new int[2];
                int marginTop = Math.max(mainLocation[1],mFloatBtnWrapper.getTop());
                int mImageView1=mImageView.getWidth();
                mMainLayout.getLocationOnScreen(mainLocation);
                //除以2代表最上面的那个点，显示圆的最上面一点
                int height12=getScreenHeight()-getWidgetHeight(mFloatBtnWrapper)*2-mEdgePadding*2;
                //使用View主动测量宽高
                // mFloatWidth=mFloatBtnWrapper.getWidth();
                //mFloatBtnWindowParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, getScreenWidth()/2-getWidgetWidth(rl_root)/2, getScreenHeight()-getWidgetHeight(rl_root));

                //获取在整个屏幕内的绝对坐标

                mFloatBtnWindowParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, getScreenWidth()/2-getWidgetWidth(mFloatBtnWrapper)/2, height12);
                mFloatRootView.addView(mFloatBtnWrapper, mFloatBtnWindowParams);
                mFloatViewBoundsInScreens.set(
                        mainLocation[0],
                        mainLocation[1],
                        mainLocation[0] + mMainLayout.getWidth(),
                        mMainLayout.getHeight() + mainLocation[1]);
                mFloatTouchListener = new FloatTouchListener(SecondActivity.this,mFloatViewBoundsInScreens,mFloatBtnWrapper,
                        mFloatBtnWindowParams,mainLocation[1],mEdgePadding);
                mFloatTouchListener.setFloatButtonCallback(new FloatTouchListener.FloatButtonCallback() {
                    @Override
                    public void onPositionChanged(int x, int y, int gravityX, float percentY) {

                    }

                    @Override
                    public void onTouch() {

                    }
                });
                mFloatRootView.setOnTouchListener(mFloatTouchListener);
                mFloatRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RotateAnimation rotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setDuration(1000);
                        rotateAnimation.setRepeatCount(3);
                        mImageView.startAnimation(rotateAnimation);
                        Toast.makeText(SecondActivity.this,"点击展现效果",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }



    /**
         * 获取屏幕的宽度
         *
         * @return
         */
    private int getScreenWidth() {
        WindowManager windowManager = getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        return defaultDisplay.getWidth();
    }

    /**
     * 获取屏幕的高度
     *
     * @return
     */
    private int getScreenHeight() {
        WindowManager windowManager = getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        return defaultDisplay.getHeight();
    }


    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    private int dip2px(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) (dp * density + 0.5f);
    }


    /**
     * px转dp
     *
     * @param px
     * @return
     */
    private int px2dip(int px) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }
    /**
     * 获取控件的宽
     * @param view
     * @return
     */
    public static int getWidgetWidth(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);//先度量
        int width = view.getMeasuredWidth();
        return width;
    }
    /**
     * 获取控件的高
     * @param view
     * @return
     */
    public static int getWidgetHeight(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);//先度量
        int height = view.getMeasuredHeight();
        return height;
    }
}

package com.lvruheng.floatingdragbutton;

import android.content.Intent;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class MainActivity extends BaseActivity implements View.OnTouchListener{
    private RelativeLayout mFloatBtnWrapper;
    private AbsoluteLayout.LayoutParams mFloatBtnWindowParams;
    private AbsoluteLayout mFloatRootView;
    private RelativeLayout mMainLayout;
    private FloatTouchListener mFloatTouchListener;
    private Rect mFloatViewBoundsInScreens;
    private int mEdgePadding;
    private ImageView mImageView;
    private RelativeLayout rl_root;
    private int startX = 0;
    private int startY = 0;
    private int mTop;
    private int mLeft;
    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private Button button4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mMainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        button4= (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });
//        addFloatBtn();
//        setTouchListener();
    }

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
  //      mFloatBtnWrapper.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        int width5=getWidgetWidth(mFloatBtnWrapper);
//        int height1=getWidgetHeight(mFloatBtnWrapper);
        mFloatRootView = new AbsoluteLayout(this);
        mMainLayout.addView(mFloatRootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        int width1=getScreenWidth();
//        int width3=mFloatBtnWrapper.getWidth();
//        int width2=getScreenWidth()/2-getWidgetWidth(rl_root)/2;
//        int height=getScreenHeight();
        final float scale = MainActivity.this.getResources().getDisplayMetrics().density;
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
//                rl_root.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                int width5=rl_root.getWidth();
//                int height1=rl_root.getHeight();
                mFloatViewBoundsInScreens = new Rect();
                int[] mainLocation = new int[2];
                int marginTop = Math.max(mainLocation[1],mFloatBtnWrapper.getTop());
                int mImageView1=mImageView.getWidth();
                mMainLayout.getLocationOnScreen(mainLocation);
                //除以2代表最上面的那个点，显示圆的最上面一点
                int height12=getScreenHeight()-getWidgetHeight(mFloatBtnWrapper)*2-mEdgePadding*2;
                int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                mImageView.measure(w, h);//测量控件的宽高
                mMeasuredWidth = mImageView.getMeasuredWidth();
                mMeasuredHeight = mImageView.getMeasuredHeight();
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
                mFloatTouchListener = new FloatTouchListener(MainActivity.this,mFloatViewBoundsInScreens,mFloatBtnWrapper,
                        mFloatBtnWindowParams,mainLocation[1],mEdgePadding);
                mFloatTouchListener.setFloatButtonCallback(new FloatTouchListener.FloatButtonCallback() {
                    @Override
                    public void onPositionChanged(int x, int y, int gravityX, float percentY) {

                    }

                    @Override
                    public void onTouch() {

                    }
                });
                rl_root.setOnTouchListener(mFloatTouchListener);
                //mFloatRootView.setOnTouchListener(MainActivity.this);

                rl_root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RotateAnimation rotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setDuration(1000);
                        rotateAnimation.setRepeatCount(3);
                        mImageView.startAnimation(rotateAnimation);
                        Toast.makeText(MainActivity.this,"点击展现效果",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //初始化开始位置
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                mTop = mImageView.getTop();
                mLeft = mImageView.getLeft();
                break;
            case MotionEvent.ACTION_MOVE:
                //手势移动的dX和dY为控件的marginLeft和marginTop
                int moveX = (int) event.getRawX();
                int moveY = (int) event.getRawY();
                int dX = moveX - startX;
                int dY = moveY - startY;
                setImageViewMargin(dX, dY);
                break;
            case MotionEvent.ACTION_UP:
                //初始值重置
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                mTop = mImageView.getTop();
                mLeft = mImageView.getLeft();
                break;
        }
        return true;
    }
    /**
     * 动态设置控件的marginTop 和 marginLeft的值
     *
     * @param dX x轴的偏移量
     * @param dY y轴的偏移量
     */
    private void setImageViewMargin(int dX, int dY) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
        int top = mTop + dY;
        int left = mLeft + dX;
        int l = getScreenWidth() - mMeasuredWidth;
        int t = getScreenHeight() - mMeasuredHeight - getStatusBarHeight();
        //设置left和top的边界值
        if (left < 0) {
            left = 0;
        } else if (left > l) {
            left = l;
        }
        if (top < 0) {
            top = 0;
        } else if (top > t) {
            top = t;
        }
        layoutParams.topMargin = top;
        layoutParams.leftMargin = left;
        mImageView.setLayoutParams(layoutParams);
    }
    @Override
    protected void onResume() {
        super.onResume();
//        int width3=mFloatBtnWrapper.getWidth();
//        int width4=getScreenWidth()/2+mFloatWidth;
//        mFloatBtnWindowParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, getScreenWidth()/2+mFloatWidth, 0);
//        mFloatRootView.addView(mFloatBtnWrapper, mFloatBtnWindowParams);
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
     * 获取状态栏的高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
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

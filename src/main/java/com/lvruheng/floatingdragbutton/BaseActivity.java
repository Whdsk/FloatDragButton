package com.lvruheng.floatingdragbutton;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by lenovocabulary on 2018/9/1.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnTouchListener{
    private RelativeLayout parentLinearLayout;//把父类activity和子类activity的view都add到这里
    private RelativeLayout mFloatBtnWrapper;
    private AbsoluteLayout.LayoutParams mFloatBtnWindowParams;
    private AbsoluteLayout mFloatRootView;
    private FloatTouchListener mFloatTouchListener;
    private Rect mFloatViewBoundsInScreens;
    private int mEdgePadding;
    private ImageView mImageView;
    private RelativeLayout rl_root;
    /**
     * 当前Activity渲染的视图View
     **/
    private View mContextView = null;
    private int mTop;
    private int mLeft;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int startX = 0;
    private int startY = 0;
    private int mStatusBarHeight;
//    private int start_x=0;
//
//    private int start_y=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
        initContentView(R.layout.float_btn);
        setContentView(getLayoutId());
        setTouchListener();
    }
    //获取布局文件
    public abstract int getLayoutId();
    /**
     * 初始化contentview
     */
    private void initContentView(int layoutResID) {
        //获取没有标题栏的位置 setContentView
//        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
//        viewGroup.removeAllViews();
        //状态栏高度     通过反射获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mStatusBarHeight = frame.top;
        if (mStatusBarHeight <= 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                mStatusBarHeight =getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        mFloatRootView = new AbsoluteLayout(this);
//        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        layoutParams.setMargins(0, mStatusBarHeight, 0, 0);
        mFloatRootView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //parentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        //viewGroup.addView(mFloatRootView);
         FrameLayout.LayoutParams layoutParams = createLayoutParams(0, mStatusBarHeight, 0, 0);
        //getDecorView既然是顶级视图，它包含整个屏幕，包括标题栏
        FrameLayout rootLayout = (FrameLayout) getWindow().getDecorView();
       // mFloatRootView.setLayoutParams(layoutParams);
        rootLayout.addView(mFloatRootView, layoutParams);
        // 动态添加view
        LayoutInflater.from(this).inflate(layoutResID, mFloatRootView, true);
    }

    private FrameLayout.LayoutParams createLayoutParams(int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    @Override
    public void setContentView(int layoutResID) {
        //parentLinearLayout.removeView(view);
        mFloatBtnWrapper = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.float_btn,null,false);
        mImageView = (ImageView) mFloatBtnWrapper.findViewById(R.id.iv_shine);
        rl_root=mFloatBtnWrapper.findViewById(R.id.rl_root);
        // mContextView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        int width5=getWidgetWidth(mFloatBtnWrapper);
//        int height1=getWidgetHeight(mFloatBtnWrapper);

        parentLinearLayout = new RelativeLayout(this);
        mFloatRootView.addView(parentLinearLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
//        int width1=getScreenWidth();
//        int width3=mFloatBtnWrapper.getWidth();
//        int width2=getScreenWidth()/2-getWidgetWidth(rl_root)/2;
//        int height=getScreenHeight();
        final float scale = this.getResources().getDisplayMetrics().density;
        mEdgePadding = (int) (10 * scale + 0.5);

        if(mContextView==null) {
            //把不是titleView的添加到parentLinearLayout布局里面
            mContextView = LayoutInflater.from(this).inflate(getLayoutId(), mFloatRootView, true);
        }
    }

//    @Override
//    public void setContentView(View view, ViewGroup.LayoutParams params) {
//
//        parentLinearLayout.addView(view, params);
//
//    }
     /*
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
                parentLinearLayout.getLocationOnScreen(mainLocation);
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

                if(Constants.start_x==0&&Constants.start_y==0) {
                    mFloatBtnWindowParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, getScreenWidth() / 2 - getWidgetWidth(mFloatBtnWrapper) / 2, height12);
                }else {
                    mFloatBtnWindowParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Constants.start_x, Constants.start_y);
                }
               if(mFloatBtnWrapper.getParent()!=null){
                    ViewGroup viewGroup = (ViewGroup) mFloatBtnWrapper.getParent();
                    viewGroup.removeView(mFloatBtnWrapper);
                    mFloatRootView.addView(mFloatBtnWrapper, mFloatBtnWindowParams);
                }else {
                    mFloatRootView.addView(mFloatBtnWrapper, mFloatBtnWindowParams);
                }
                mFloatViewBoundsInScreens.set(
                        mainLocation[0],
                        mainLocation[1],
                        mainLocation[0] + mFloatRootView.getWidth(),
                        mFloatRootView.getHeight() + mainLocation[1]);
                mFloatTouchListener = new FloatTouchListener(BaseActivity.this,mFloatViewBoundsInScreens,mFloatBtnWrapper,
                        mFloatBtnWindowParams,mainLocation[1],mEdgePadding);
                mFloatTouchListener.setFloatButtonCallback(new FloatTouchListener.FloatButtonCallback() {
                    @Override
                    public void onPositionChanged(int x, int y, int gravityX, float percentY) {
                        Log.i("cccccc",x+"");
                        Log.i("cccccc",y+"");
                        //记录上个Activity的悬浮位置
                        Constants.start_x=x;
                        Constants.start_y=y;
//                        Intent intent=new Intent(BaseActivity.this,BaseActivity.class);
//                        intent.putExtra("cccccc",x);
//                        intent.putExtra("cccccc",y);
//                        startActivity(intent);
                        //mFloatBtnWindowParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, getScreenWidth()/2-getWidgetWidth(mFloatBtnWrapper)/2, height12);
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
                        RotateAnimation rotateAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setDuration(1000);
                        rotateAnimation.setRepeatCount(3);
                        mImageView.startAnimation(rotateAnimation);
                        Toast.makeText(BaseActivity.this,"点击展现效果",Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onResume() {
        super.onResume();
//        int width3=mFloatBtnWrapper.getWidth();
//        int width4=getScreenWidth()/2+mFloatWidth;
//        mFloatBtnWindowParams = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, getScreenWidth()/2+mFloatWidth, 0);
//        mFloatRootView.addView(mFloatBtnWrapper, mFloatBtnWindowParams);
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

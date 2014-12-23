package com.example.activity.friendcircle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.example.imitation.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 14-12-16.
 */
public class MyCircleListView extends ListView implements AbsListView.OnScrollListener {

    private static final String className = MyCircleListView.class.getName();

    private final static int RELEASE = 0;
    private final static int PULL = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;

    private final static int RATIO = 3;

    private LayoutInflater inflater;
    private LinearLayout headView;
    private View footer;

    private TextView tvTipsTextView;
    private TextView tvLastUpdatedTextView;
    private ImageView ivArrowImageView;
    private ProgressBar headerProgressBar;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private boolean isLoadFull;
    private boolean isLoading;
    private boolean loadEnable = true;
    private boolean isRecord;
    private int startY;
    private int firstItemIndex;
    private int state;
    private boolean isBack;

    private int pageSize = 3;

    private int headContentWidth;
    private int headContentHeight;

    private OnRefreshListener refreshListener;
    private boolean isRefreshable;
    private TextView loadFull;
    private TextView noData;
    private TextView more;
    private ProgressBar loading;
    private int scrollState;

    private OnLoadListener onLoadListener;

    public MyCircleListView(Context context) {
        super(context);
        initial(context);
    }

    public MyCircleListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initial(context);
    }

    public MyCircleListView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        initial(context);
    }

    private void initial(Context context) {
        inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.head, null);
        ivArrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
        ivArrowImageView.setMinimumWidth(70);
        ivArrowImageView.setMinimumHeight(50);
        headerProgressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
        tvTipsTextView = (TextView) headView.findViewById(R.id.head_tipsTextView);
        tvLastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

        footer = (View) inflater.inflate(R.layout.listview_footer, null);
        loadFull = (TextView) footer.findViewById(R.id.loadFull);
        noData = (TextView) footer.findViewById(R.id.noData);
        more = (TextView) footer.findViewById(R.id.more);
        loading = (ProgressBar) footer.findViewById(R.id.loading);

        measureView(headView);
        measureView(footer);
        headContentHeight = headView.getMeasuredHeight();
        headContentWidth = headView.getMeasuredWidth();

        headView.setPadding(0, -1 * headContentHeight, 0, 0);
        headView.invalidate();

        addHeaderView(headView, null, false);
        addFooterView(footer);
        setOnScrollListener(this);

        animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(100);
        animation.setFillAfter(true);
        animation.setInterpolator(new LinearInterpolator());

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(100);
        reverseAnimation.setFillAfter(true);

        state = DONE;
        isRefreshable = false;
    }

    public void measureView(View headView) {
        ViewGroup.LayoutParams p = headView.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int widthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int heightSpec;
        if (lpHeight > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        headView.measure(widthSpec, heightSpec);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.scrollState = scrollState;
        ifNeedLoad(absListView, scrollState);
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int i2, int i3) {
        this.firstItemIndex = firstVisibleItem;
    }

    private void ifNeedLoad(AbsListView view, int scrollState) {
        if (!loadEnable) {
            return;
        }
        try {
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                    && !isLoading
                    && view.getLastVisiblePosition() == view
                    .getPositionForView(footer)
                    && !isLoadFull) {
                load();
                isLoading = true;
            }
        } catch (Exception e) {

        }
    }

    private void load() {
        if(onLoadListener != null){
            onLoadListener.load();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRefreshable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (firstItemIndex == 0 && !isRecord) {
                        isRecord = true;
                        startY = (int) event.getY();
                        Log.v(className, "在down时候记录当前位置");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (state != REFRESHING && state != LOADING) {
                        if (state == DONE) {
                            //什么都不做
                        }
                        if (state == PULL) {
                            state = DONE;
                            changeHeaderViewByState();
                            Log.v(className, "由下拉刷新状态，到done状态");
                        }
                        if (state == RELEASE) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                            Log.v(className, "由松开刷新状态，到done状态");
                        }
                    }
                    isRecord = false;
                    isBack = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) event.getY();
                    if (!isRecord && firstItemIndex == 0) {
                        Log.v(className, "在move时候记录下位置");
                        isRecord = true;
                        startY = tempY;
                    }
                    if (state != REFRESHING && isRecord && state != LOADING) {
                        if (state == RELEASE) {
                            setSelection(0);
                            if (((tempY - startY) / RATIO < headContentHeight) &&
                                    (tempY - startY) > 0) {
                                state = PULL;
                                changeHeaderViewByState();

                                Log.v(className, "由松开刷新状态转变到下拉刷新状态");
                            } else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                                Log.v(className, "由松开刷新状态转变到done状态");
                            } else {  // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
                                // 不用进行特别的操作，只用更新paddingTop的值就行了
                            }
                        }
                        if (state == PULL) {
                            setSelection(0);
                            if ((tempY - startY) / RATIO >= headContentHeight) {
                                state = RELEASE;
                                isBack = true;
                                changeHeaderViewByState();
                                Log.v(className, "由done或者下拉刷新状态转变到松开刷新");
                            } else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                                Log.v(className, "由DOne或者下拉刷新状态转变到done状态");
                            }
                        }

                        if (state == DONE) {
                            if (tempY - startY > 0) {
                                state = PULL;
                                changeHeaderViewByState();
                            }
                        }
                        if (state == PULL) {
                            headView.setPadding(0, -1 * headContentHeight +
                                    (tempY - startY) / RATIO, 0, 0);
                        }
                        if (state == RELEASE) {
                            headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight,
                                    0, 0);
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void onRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE:
                ivArrowImageView.setVisibility(View.VISIBLE);
                headerProgressBar.setVisibility(View.GONE);
                tvTipsTextView.setVisibility(View.VISIBLE);
                tvLastUpdatedTextView.setVisibility(View.VISIBLE);

                ivArrowImageView.clearAnimation();
                ivArrowImageView.startAnimation(animation);
                tvTipsTextView.setText("松开刷新");
                Log.v(className, "当前状态，松开刷新");
                break;
            case PULL:
                headerProgressBar.setVisibility(View.GONE);
                tvTipsTextView.setVisibility(View.VISIBLE);
                tvLastUpdatedTextView.setVisibility(View.VISIBLE);
                ivArrowImageView.setVisibility(View.VISIBLE);
                ivArrowImageView.clearAnimation();

                if (isBack) {
                    isBack = false;
                    ivArrowImageView.clearAnimation();
                    ivArrowImageView.startAnimation(reverseAnimation);
                    tvTipsTextView.setText("下拉刷新");
                } else {
                    tvTipsTextView.setText("下拉刷新");
                }
                Log.v(className, "当前状态，下拉刷新");
                break;
            case REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                headerProgressBar.setVisibility(View.VISIBLE);
                ivArrowImageView.setVisibility(View.GONE);
                ivArrowImageView.clearAnimation();
                tvTipsTextView.setText("正在刷新...");
                tvLastUpdatedTextView.setVisibility(View.VISIBLE);

                Log.v(className, "当前状态，正在刷新");
                break;
            case DONE:
                headView.setPadding(0, -1 * headContentHeight, 0, 0);
                headerProgressBar.setVisibility(View.GONE);
                ivArrowImageView.clearAnimation();
                ivArrowImageView.setImageResource(R.drawable.arrow_down);
                tvTipsTextView.setText("下拉刷新");
                tvLastUpdatedTextView.setVisibility(View.VISIBLE);
                Log.v(className, "当前状态，done");
                break;
        }
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        isRefreshable = true;
    }

    public void setOnLoadListener(OnLoadListener loadListener){
        this.onLoadListener = loadListener;
        this.loadEnable = true;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public interface  OnLoadListener{
        public void load();
    }
    public void onRefreshComplete() {
        state = DONE;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String date = format.format(new Date());
        tvLastUpdatedTextView.setText("最近更新: " + date);
        changeHeaderViewByState();
    }
    public void onLoadComplete(){
        isLoading = false;
    }

    public void setResultSize(int resultSize) {
        if (resultSize == 0) {
            isLoadFull = true;
            loadFull.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        } else if (resultSize > 0 && resultSize < pageSize) {
            isLoadFull = true;
            loadFull.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
            noData.setVisibility(View.GONE);
        } else if (resultSize == pageSize) {
            isLoadFull = false;
            loadFull.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        }

    }

    public void setAdapter(BaseAdapter adapter) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        String date = format.format(new Date());
        tvLastUpdatedTextView.setText("最近更新: " + date);
        super.setAdapter(adapter);
    }
}

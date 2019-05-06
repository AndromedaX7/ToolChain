package c.feature.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayoutJava extends ViewGroup {
    public FlowLayoutJava(Context context) {
        super(context);
    }

    public FlowLayoutJava(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayoutJava(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlowLayoutJava(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int totalHeight = 0;
        int maxViewHeight = 0;
        int currentWidth = 0;
        Log.e(TAG, "onLayout: l:" + l + "==t:" + t + "==r:" + r + "==b:" + b);
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);

            int viewWidth = view.getMeasuredWidth();
            int viewHeight = view.getMeasuredHeight();
            MarginLayoutParams p = (MarginLayoutParams) view.getLayoutParams();
            if (currentWidth + viewWidth > r) {
                currentWidth = 0;
                totalHeight += maxViewHeight;
                totalHeight+=p.topMargin;
                view.layout(currentWidth + p.leftMargin, totalHeight + p.topMargin, currentWidth + viewWidth + p.rightMargin, totalHeight + viewHeight + p.bottomMargin);
            } else {
                view.layout(currentWidth + p.leftMargin, totalHeight + p.topMargin, currentWidth + viewWidth + p.rightMargin, totalHeight + viewHeight + p.bottomMargin);
                currentWidth += viewWidth+p.rightMargin;
            }
            maxViewHeight = Math.max(viewHeight, maxViewHeight);

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new MarginLayoutParams(lp);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    private String TAG = "FlowLayout";
}

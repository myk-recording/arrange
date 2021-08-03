package com.example.myk.flowlayout;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private int mhorizontal = dp2px(16);
    private int mvertical = dp2px(8);
    private List<View> lineList = new ArrayList<>();
    private List<List<View>> allLineList;
    private List<Integer> lineHeightList = new ArrayList<>();

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (allLineList == null) {
            allLineList = new ArrayList<>();
        } else {
            allLineList.clear();
        }
        if (lineHeightList == null) {
            lineHeightList = new ArrayList<>();
        } else {
            lineHeightList.clear();
        }
        int childCount = getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);
        lineList = new ArrayList<>();
        int lineWidth = 0;
        int lineHeight = 0;
        int parentWidth = 0;
        int parentHeight = 0;
        allLineList.clear();
        lineHeightList.clear();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            LayoutParams childLayoutParams = childView.getLayoutParams();
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,paddingLeft + paddingRight,childLayoutParams.width);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,paddingTop + paddingBottom,childLayoutParams.height);
            childView.measure(childWidthMeasureSpec,childHeightMeasureSpec);
            int childMeasureWidth = childView.getMeasuredWidth();
            int childMeasureHeight = childView.getMeasuredHeight();
            if (childMeasureWidth + lineWidth + mhorizontal > selfWidth) {
                allLineList.add(lineList);
                lineHeightList.add(lineHeight);
                parentHeight = parentHeight + lineHeight + mvertical;
                parentWidth = Math.max(parentWidth,lineWidth + mhorizontal);
                lineList = new ArrayList<>();
                lineWidth = 0;
                lineHeight = 0;
            }
            lineList.add(childView);
            lineWidth += childMeasureWidth + mhorizontal;
            lineHeight = Math.max(lineHeight,childMeasureHeight);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int realWidth = (widthMode == MeasureSpec.EXACTLY) ? selfWidth : parentWidth;
            int realHeight = (heightMode == MeasureSpec.EXACTLY) ? selfHeight : parentHeight;
            setMeasuredDimension(realWidth,realHeight);
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int lineCount = allLineList.size();
        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        for (int j = 0; j < lineCount; j++) {
            List<View> lineViewList = allLineList.get(j);
            int count = lineViewList.size();
            for (int k = 0; k < count; k++) {
                View view = lineViewList.get(k);
                int right = pl + view.getMeasuredWidth();
                int bottom = pt + view.getMeasuredHeight();
                view.layout(pl,pt,right,bottom);
                pl = right + mhorizontal;
            }
            pl = getPaddingLeft();
            pt = pt + lineHeightList.get(j) + mvertical;
        }
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, Resources.getSystem().getDisplayMetrics());
    }
}

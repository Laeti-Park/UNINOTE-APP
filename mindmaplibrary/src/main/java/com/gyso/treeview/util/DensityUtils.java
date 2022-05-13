package com.gyso.treeview.util;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtils {

    public static int dp2px(Context context, float dpVal) {
        int result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources()
                .getDisplayMetrics());
        return result;
    }

    public static int sp2px(Context context, float spVal) {
        int result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources()
                .getDisplayMetrics());
        return result;
    }

    public static int px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int result = (int) (pxVal / scale);
        return result;
    }

    public static float px2sp(Context context, float pxVal) {
        int result = (int) (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
        return result;
    }

}

package wang.leal.moment.utils;

import android.view.View;

public class ViewUtil {

    public static boolean isTouchPointInView(float x, float y, View view){
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        return y >= top && y <= bottom && x >= left
                && x <= right;
    }

}

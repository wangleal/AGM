package wang.leal.moment.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.Field;

import wang.leal.moment.R;

public class DragEditText extends ConstraintLayout {
    private DragEdit etText;
    private int lastX, lastY;
    private int parentWidth, parentHeight;
    public DragEditText(Context context) {
        super(context);
        initView();
    }

    public DragEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DragEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private int rootViewVisibleHeight;
    private void initView(){
        etText = new DragEdit(getContext());
        etText.setGravity(Gravity.CENTER);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,getResources().getDisplayMetrics());
        etText.setTextSize(textSize);
        etText.setTextColor(Color.WHITE);
        etText.setBackground(null);
        addView(etText, LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etText.setGravity(Gravity.CENTER);
        etText.setMinWidth(100);
        setCursorDrawableColor(etText, R.drawable.shape_camera_editor_text_cursor);
        LayoutParams etParams = (LayoutParams) etText.getLayoutParams();
        etParams.topToTop = LayoutParams.PARENT_ID;
        etParams.bottomToBottom = LayoutParams.PARENT_ID;
        etText.setOnEditorActionListener((v, actionId, event) -> (event.getKeyCode()== KeyEvent.KEYCODE_ENTER));
        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            getWindowVisibleDisplayFrame(r);
            int visibleHeight = r.height();
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight;
                return;
            }
            //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (rootViewVisibleHeight == visibleHeight) {
                return;
            }
            //根视图显示高度变小超过200，可以看作软键盘显示了
            if (rootViewVisibleHeight - visibleHeight > 200) {
                keyBoardIsShow();
                rootViewVisibleHeight = visibleHeight;
                return;
            }
            //根视图显示高度变大超过200，可以看作软键盘隐藏了
            if (visibleHeight - rootViewVisibleHeight > 200) {
                keyBoardIsHide();
                rootViewVisibleHeight = visibleHeight;
            }
        });
    }

    private void keyBoardIsShow(){
        ((View)getParent()).setBackgroundColor(Color.parseColor("#b4000000"));
    }

    private void keyBoardIsHide(){
        etText.clearFocus();
        ((View)getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = (int) ev.getRawX();
            lastY = (int) ev.getRawY();
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) event.getRawX() - lastX;
                int deltaY = (int) event.getRawY() - lastY;
                if (etText.isFocused()){
                    return super.onTouchEvent(event);
                }else {
                    if (parentWidth == 0) {
                        ViewGroup mViewGroup = (ViewGroup) getParent();
                        parentWidth = mViewGroup.getWidth();
                        parentHeight = mViewGroup.getHeight();
                    }

                    if (getTranslationX() < -getLeft() && deltaX < 0) deltaX = 0;
                    else if (getTranslationX() > (parentWidth - getRight()) && deltaX > 0) deltaX = 0;
                    if (getTranslationY() < -getTop() && deltaY < 0) deltaY = 0;
                    else if (getTranslationY() > (parentHeight - getBottom()) && deltaY > 0) deltaY = 0;

                    setTranslationX(getTranslationX() + deltaX);
                    setTranslationY(getTranslationY() + deltaY);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    private static class DragEdit extends AppCompatEditText {

        private int downX, downY;

        public DragEdit(Context context) {
            super(context);
        }

        public DragEdit(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DragEdit(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) event.getRawX();
                    downY = (int) event.getRawY();
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int upX = (int) event.getRawX() - downX;
                    int upY = (int) event.getRawY() - downY;
                    if (Math.abs(upX) <= ViewConfiguration.get(getContext()).getScaledTouchSlop() && Math.abs(upY) <= ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }

    public void textLayoutClickOut(){
        if (etText.isFocused()){
            hideKeyBoard();
        }else {
            showKeyBoard();
        }
    }

    private void hideKeyBoard(){
        if (etText!=null&&etText.isFocused()){
            IBinder token = etText.getWindowToken();
            InputMethodManager inputMethodManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager!=null){
                inputMethodManager.hideSoftInputFromWindow(token,0);
            }
        }
    }

    public void showKeyBoard(){
        if (etText!=null&&!etText.isFocused()){
            etText.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager!=null){
                inputMethodManager.showSoftInput(etText,InputMethodManager.SHOW_FORCED);
            }
        }
    }

    private void setCursorDrawableColor(EditText editText, int drawable) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");//获取这个字段
            fCursorDrawableRes.setAccessible(true);
            fCursorDrawableRes.set(editText,drawable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
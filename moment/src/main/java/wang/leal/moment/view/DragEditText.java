package wang.leal.moment.view;

import android.content.Context;
import android.graphics.Color;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
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

    private void initView(){
        addEdit();
    }

    public void keyBoardIsShow(int diff){

    }

    public void keyBoardIsHide(int diff){
        etText.clearFocus();
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

    private boolean isSelected = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (etText.isFocused()){
                    return super.onTouchEvent(event);
                }else {
                    int deltaX = (int) event.getRawX() - lastX;
                    int deltaY = (int) event.getRawY() - lastY;
//                    offsetLeftAndRight(deltaX);
//                    offsetTopAndBottom(deltaY);
                    float x = getX() + deltaX;
                    float y = getY() + deltaY;
                    moveEdit(x,y);
                    if (callback!=null){
                        callback.onShow();
                        isSelected = callback.isSelected(event.getRawX(),event.getRawY());
                    }
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (callback!=null){
                    callback.onGone();
                    if (isSelected){
                        callback.onDelete();
                        isSelected = false;
                    }
                }
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

    public void showTextSize(int sizeOfSP){
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sizeOfSP,getResources().getDisplayMetrics());
        if (etText!=null){
            etText.setTextSize(textSize);
        }
    }

    private void addEdit(){
        if (etText!=null){
            removeView(etText);
        }
        etText = new DragEdit(getContext());
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,18,getResources().getDisplayMetrics());
        etText.setTextSize(textSize);
        etText.setTextColor(Color.WHITE);
        etText.setBackground(null);
        addView(etText, LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etText.setOnEditorActionListener((v, actionId, event) -> (event.getKeyCode()== KeyEvent.KEYCODE_ENTER));
        etText.setGravity(Gravity.CENTER);
        etText.setMinWidth(100);
        setCursorDrawableColor(etText, R.drawable.shape_camera_editor_text_cursor);
        LayoutParams etParams = (LayoutParams) etText.getLayoutParams();
        etParams.topToTop = LayoutParams.PARENT_ID;
        etParams.bottomToBottom = LayoutParams.PARENT_ID;
        etParams.startToStart = LayoutParams.PARENT_ID;
        etParams.endToEnd = LayoutParams.PARENT_ID;
        etText.setLayoutParams(etParams);
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                moveEdit(getX(),getY());
            }
        });
    }

    private void moveEdit(float x,float y){
        ViewGroup mViewGroup = (ViewGroup) getParent();
        int parentWidth = mViewGroup.getWidth();
        int parentHeight = mViewGroup.getHeight();
        if (x<0){
            x=0;
        }
        if (y<0){
            y=0;
        }
        if ((x+etText.getWidth())>parentWidth){
            x = parentWidth-etText.getWidth();
        }
        if ((y+etText.getHeight())>parentHeight){
            y = parentHeight-etText.getHeight();
        }
        setX(x);
        setY(y);
    }

    private Callback callback;
    public void setCallback(Callback callback){
        this.callback = callback;
    }
    public interface Callback{
        void onShow();
        void onGone();
        boolean isSelected(float x,float y);
        void onDelete();
    }
}

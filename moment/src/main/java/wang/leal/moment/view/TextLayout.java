package wang.leal.moment.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import wang.leal.moment.R;
import wang.leal.moment.utils.ViewUtil;

public class TextLayout extends ConstraintLayout {
    private ImageView ivSave;
    private ConstraintLayout clBottom;
    private DragEditText dragText;
    private ImageView ivCover;
    private ImageView ivTextDelete;
    private int rootViewVisibleHeight;
    public TextLayout(Context context) {
        super(context);
        initView();
    }

    public TextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_moment_editor_text_layout,this);
        addDragEditText();
        ivSave = findViewById(R.id.iv_save_text);
        ivSave.setOnClickListener(v -> {
            if (dragText!=null){
                dragText.textLayoutClickOut();
            }
        });
        setOnClickListener(v -> {
            if (dragText!=null){
                dragText.textLayoutClickOut();
            }
        });

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
                keyBoardIsShow(rootViewVisibleHeight - visibleHeight);
                rootViewVisibleHeight = visibleHeight;
                return;
            }
            //根视图显示高度变大超过200，可以看作软键盘隐藏了
            if (visibleHeight - rootViewVisibleHeight > 200) {
                keyBoardIsHide(visibleHeight - rootViewVisibleHeight);
                rootViewVisibleHeight = visibleHeight;
            }
        });
        clBottom = findViewById(R.id.cl_bottom);
        RadioGroup rgTextSize = findViewById(R.id.rg_size);
        rgTextSize.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId==R.id.rb_big){
                if (dragText!=null){
                    dragText.showTextSize(18);
                }
            }else if (checkedId==R.id.rb_median){
                if (dragText!=null){
                    dragText.showTextSize(16);
                }
            }else if (checkedId==R.id.rb_small){
                if (dragText!=null){
                    dragText.showTextSize(14);
                }
            }
        });
        ivCover = findViewById(R.id.iv_cover);
        ivTextDelete = findViewById(R.id.iv_text_delete);
    }

    public void showCover(Bitmap bitmap){
        if (bitmap==null){
            ivCover.setVisibility(GONE);
        }else {
            ivCover.setVisibility(VISIBLE);
            ivCover.setImageBitmap(bitmap);
        }
    }

    private void keyBoardIsShow(int diff){
        if (dragText!=null){
            setBackgroundColor(Color.parseColor("#b4000000"));
            clBottom.setVisibility(VISIBLE);
            clBottom.setTranslationY(-diff);
            dragText.keyBoardIsShow(diff);
        }
    }

    private void keyBoardIsHide(int diff){
        if (dragText!=null){
            setBackgroundColor(Color.TRANSPARENT);
            clBottom.setTranslationY(diff);
            clBottom.setVisibility(GONE);
            dragText.keyBoardIsHide(diff);
        }
    }

    public void showEdit(){
        if (dragText!=null){
            dragText.textLayoutClickOut();
        }
    }

    private void addDragEditText(){
        if (dragText!=null){
            removeView(dragText);
        }
        dragText = new DragEditText(getContext());
        LayoutParams params = (LayoutParams) dragText.getLayoutParams();
        if (params==null){
            params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        }
        params.bottomToBottom = LayoutParams.PARENT_ID;
        params.topToTop = LayoutParams.PARENT_ID;
        params.startToStart = LayoutParams.PARENT_ID;
        params.endToEnd = LayoutParams.PARENT_ID;
        addView(dragText,0,params);
        dragText.setCallback(new DragEditText.Callback() {
            @Override
            public void onShow() {
                if (ivTextDelete!=null){
                    ivTextDelete.setImageResource(R.drawable.ic_camera_editor_text_delete);
                    ivTextDelete.setVisibility(VISIBLE);
                }
                if (callback!=null){
                    callback.onGone();
                }
            }

            @Override
            public void onGone() {
                if (ivTextDelete!=null){
                    ivTextDelete.setVisibility(INVISIBLE);
                }
                if (callback!=null){
                    callback.onShow();
                }
            }

            @Override
            public boolean isSelected(float x, float y) {
                if(ViewUtil.isTouchPointInView(x,y,ivTextDelete)){
                    ivTextDelete.setImageResource(R.drawable.ic_camera_editor_text_delete_selected);
                    ivTextDelete.setVisibility(VISIBLE);
                    return true;
                }
                return false;
            }

            @Override
            public void onDelete() {
                addDragEditText();
            }
        });
    }

    private Callback callback;
    public void setCallback(Callback callback){
        this.callback = callback;
    }

    public interface Callback{
        void onShow();
        void onGone();
    }
}

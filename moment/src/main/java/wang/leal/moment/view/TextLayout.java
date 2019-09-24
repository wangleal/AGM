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

public class TextLayout extends ConstraintLayout {
    private ImageView ivSave;
    private ConstraintLayout clBottom;
    private DragEditText dragText;
    private ImageView ivCover;
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
        ivSave = findViewById(R.id.iv_save);
        ivSave.setOnClickListener(v -> {
            if (dragText!=null){
                dragText.textLayoutClickOut();
            }
        });
        dragText = findViewById(R.id.det_text);
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
    }

    public void showCover(Bitmap bitmap){
        ivCover.setImageBitmap(bitmap);
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
}

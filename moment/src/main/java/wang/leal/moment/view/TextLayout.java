package wang.leal.moment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import wang.leal.moment.R;

public class TextLayout extends ConstraintLayout {
    private ImageView ivSave;
    private DragEditText dragText;
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
        ivSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dragText = findViewById(R.id.det_text);
        setOnClickListener(v -> {
            if (dragText!=null){
                dragText.textLayoutClickOut();
            }
        });
    }

    public void showEdit(){
        if (dragText!=null){
            dragText.textLayoutClickOut();
        }
    }
}

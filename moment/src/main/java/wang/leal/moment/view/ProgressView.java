package wang.leal.moment.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ProgressView extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;

    private int type;//0默认状态，1开始录制，2锁住

    private RectF oval = new RectF();  //用于定义的圆弧的形状和大小的界限

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView(){
        paint = new Paint();

        //获取自定义属性和默认值
        roundColor = Color.WHITE;
        roundProgressColor = Color.RED;
        roundWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,getResources().getDisplayMetrics());
        max = 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        int centre = getWidth()/2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth/2)-2; //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth-2); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        /**
         * 画圆弧 ，画圆环的进度
         */

        //设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
        oval.set(centre - radius-1, centre - radius-1, centre
                + radius+1, centre + radius+1);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        if(progress !=0){
            canvas.drawArc(oval, 90, 360 * progress / max, true, paint);  //根据进度画圆弧
        }
    }
}

package wang.leal.moment.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class ProgressView extends View {

    private Paint paint;
    private float roundWidth;
    private int progress;
    private int type = 0;//0默认状态，1过度状态，2开始录制，3锁住
    private RectF oval = new RectF();  //用于定义的圆弧的形状和大小的界限
    private int transitionWidth = 0;
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
        roundWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(type==0){
            //画默认圆环
            float defaultWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,90,getResources().getDisplayMetrics());
            int radius = (int) (defaultWidth/2-roundWidth); //圆环的半径
            int centre = getWidth()/2; //获取圆心的x坐标
            paint.setColor(Color.WHITE); //设置圆环的颜色
            paint.setStyle(Paint.Style.STROKE); //设置空心
            paint.setStrokeWidth(roundWidth); //设置圆环的宽度
            paint.setAntiAlias(true);  //消除锯齿
            canvas.drawCircle(centre, centre, radius, paint); //画出圆环

            //画默认内圆
            float defaultInnerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,75,getResources().getDisplayMetrics());
            radius = (int) (defaultInnerWidth/2);
            paint.setColor(Color.parseColor("#38FFFFFF"));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centre, centre, radius, paint); //画出圆
        }else if (type==1){
            //画默认圆环
            int centre = getWidth()/2; //获取圆心的x坐标
            int radius = (int) (transitionWidth/2 - roundWidth); //圆环的半径
            paint.setColor(Color.WHITE); //设置圆环的颜色
            paint.setStyle(Paint.Style.STROKE); //设置空心
            paint.setStrokeWidth(roundWidth); //设置圆环的宽度
            paint.setAntiAlias(true);  //消除锯齿
            canvas.drawCircle(centre, centre, radius, paint); //画出圆环
        }else if (type==2||type==3){
            //画默认圆环
            int centre = getWidth()/2; //获取圆心的x坐标
            int radius = (int) (centre - roundWidth); //圆环的半径
            paint.setColor(Color.WHITE); //设置圆环的颜色
            paint.setStyle(Paint.Style.STROKE); //设置空心
            paint.setStrokeWidth(roundWidth); //设置圆环的宽度
            paint.setAntiAlias(true);  //消除锯齿
            canvas.drawCircle(centre, centre, radius, paint); //画出圆环

            //设置进度是实心还是空心
            paint.setStrokeWidth(roundWidth); //设置圆环的宽度
            paint.setColor(Color.RED);  //设置进度的颜色
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            oval.set(centre - radius, centre - radius, centre
                    + radius, centre + radius);
            paint.setStyle(Paint.Style.STROKE);

            if(progress !=0){
                canvas.drawArc(oval, -90, 360 * progress / 1000, false, paint);  //根据进度画圆弧
            }
        }
        if (type==2){
            //画内部圆形
            float innerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,54,getResources().getDisplayMetrics());
            int radius = (int) (innerWidth/2);
            int centre = getWidth()/2; //获取圆心的x坐标
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centre, centre, radius, paint); //画出圆
        }else if (type==3){
            //画内部矩形
            float innerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics());
            int centre = getWidth()/2; //获取圆心的x坐标
            paint.setColor(Color.RED);  //设置进度的颜色
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(centre-innerWidth/2,centre-innerWidth/2,centre+innerWidth/2,centre+innerWidth/2,paint);
        }
    }

    public void showDefault(){
        if (valueAnimator!=null){
            valueAnimator.cancel();
        }
        type = 0;
        transitionWidth=0;
        progress=0;
        invalidate();
    }
    private ValueAnimator valueAnimator;
    public void showTransition(){
        if (valueAnimator!=null){
            valueAnimator.cancel();
        }
        type=1;
        float defaultWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,90,getResources().getDisplayMetrics());
        valueAnimator = ValueAnimator.ofInt((int) defaultWidth, getWidth()).setDuration(500);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            transitionWidth = (int) animation.getAnimatedValue();
            postInvalidate();
        });
        valueAnimator.start();
    }

    public void showRecord(){
        if (valueAnimator!=null){
            valueAnimator.cancel();
        }
        type = 2;
        valueAnimator = ValueAnimator.ofInt(0,1000).setDuration(15*1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            progress = (int) animation.getAnimatedValue();
            postInvalidate();
            if (progress==1000){
                showDefault();
                complete();
            }
        });
        valueAnimator.start();
    }

    private void complete(){
        if (valueAnimator!=null){
            valueAnimator.cancel();
        }
        if (callback!=null){
            callback.onComplete();
        }
    }

    public void showLock(){
        if (valueAnimator!=null){
            valueAnimator.cancel();
        }
        type = 3;
        invalidate();
    }

    private Callback callback;
    public void setCallback(Callback callback){
        this.callback = callback;
    }
    public interface Callback{
        void onComplete();
    }

}

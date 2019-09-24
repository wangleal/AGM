package wang.leal.moment.transcoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;

public class ImageTranscoder {

    public static Bitmap mosaic(Context context,Bitmap originBitmap){
        int imageWidth = originBitmap.getWidth();
        int imageHeight = originBitmap.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(originBitmap, 0, 0, null);
        float gridWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                20, context.getResources().getDisplayMetrics());
        Bitmap gridMosaic = getGridMosaic(originBitmap,gridWidth);
        canvas.drawBitmap(gridMosaic, 0, 0, null);
        canvas.save();
        return bitmap;
    }

    private static Bitmap getGridMosaic(Bitmap originBitmap,float gridWidth) {
        int imageWidth = originBitmap.getWidth();
        int imageHeight = originBitmap.getHeight();
        if (imageWidth <= 0 || imageHeight <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int horCount = (int) Math.ceil(imageWidth / gridWidth);
        int verCount = (int) Math.ceil(imageHeight / gridWidth);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (int horIndex = 0; horIndex < horCount; ++horIndex) {
            for (int verIndex = 0; verIndex < verCount; ++verIndex) {
                float l = gridWidth * horIndex;
                float t = gridWidth * verIndex;
                float r = l + gridWidth;
                if (r > imageWidth) {
                    r = imageWidth;
                }
                float b = t + gridWidth;
                if (b > imageHeight) {
                    b = imageHeight;
                }
                int color = originBitmap.getPixel((int) l, (int) t);
                RectF rect = new RectF(l, t, r, b);
                paint.setColor(color);
                canvas.drawRect(rect, paint);
            }
        }
        canvas.save();
        return bitmap;
    }
}

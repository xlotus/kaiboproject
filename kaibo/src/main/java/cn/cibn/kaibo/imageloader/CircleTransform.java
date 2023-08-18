package cn.cibn.kaibo.imageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;


public class CircleTransform extends BitmapTransformation {
    private Paint mBorderPaint;
    private float mBorderWidth;
    private int mBorderColor;

    public CircleTransform() {
    }

    public CircleTransform(float borderWidth, int borderColor) {
        mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;
        mBorderColor = borderColor;
        mBorderPaint = new Paint();
        mBorderPaint.setDither(true);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) {
            return null;
        }
        int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        if (mBorderPaint != null) {
            float borderRadius = r - mBorderWidth / 2;
            canvas.drawCircle(r, r, borderRadius, mBorderPaint);
        }
        return result;
    }


    public String getId() {
        return "CircleTransform";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CircleTransform))
            return false;
        CircleTransform target = (CircleTransform) o;
        return mBorderWidth == target.getBorderWidth() && mBorderColor == target.getBorderColor();
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            messageDigest.update(getId().getBytes(CHARSET));
        }
    }
}

package cn.cibn.kaibo.imageloader;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;


public class GrayTransform extends BitmapTransformation {

    public GrayTransform() {
        super();
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return ImageLoadHelper.convertGrayImg(toTransform);
    }


    public String getId() {
        return "GrayTransform";
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

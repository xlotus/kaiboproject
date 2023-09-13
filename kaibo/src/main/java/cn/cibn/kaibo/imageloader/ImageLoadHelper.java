package cn.cibn.kaibo.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.tv.lib.core.Logger;

public class ImageLoadHelper {
    public static final DiskCacheStrategy DEFAULT_CACHE_STRATEGY = DiskCacheStrategy.AUTOMATIC;
    public static final DrawableTransitionOptions sCrossFade = new DrawableTransitionOptions().crossFade(new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true));
    private static final String TAG = "ImageLoadHelper";

    public static void loadImage(ImageView imageView, String picUrl, boolean isGray, int holder) {
        try {
            RequestOptions options = new RequestOptions().placeholder(0)
                    .diskCacheStrategy(ImageLoadHelper.DEFAULT_CACHE_STRATEGY);
            if (isGray) {
                options = options.transform(new GrayTransform());
            }
            if (holder > 0) {
                options = options.placeholder(holder);
            }
            loadWithOptions(Glide.with(imageView), picUrl, imageView, options);
        } catch (Exception e) {
            Logger.e(TAG, "load url failed: ", e);
        }
    }

    public static void loadImage(ImageView imageView, String picUrl, boolean isGray) {
        loadImage(imageView, picUrl, isGray, 0);
    }

    public static void loadImage(ImageView imageView, String picUrl, int nRound, boolean isGray) {
        try {
            RequestOptions options = new RequestOptions().placeholder(0)
                    .transform(new RoundedCorners(nRound))
                    .diskCacheStrategy(ImageLoadHelper.DEFAULT_CACHE_STRATEGY);
            if (isGray) {
                options = options.transform(new GrayTransform());
            }
            loadWithOptions(Glide.with(imageView), picUrl, imageView, options);
        } catch (Exception e) {
            Logger.e(TAG, "load url failed: ", e);
        }
    }

    public static void loadCircleImage(ImageView imageView, String picUrl, boolean isGray) {
        try {
            RequestOptions options = new RequestOptions().placeholder(0)
                    .transform(new CircleTransform())
                    .diskCacheStrategy(ImageLoadHelper.DEFAULT_CACHE_STRATEGY);
            if (isGray) {
                options = options.transform(new GrayTransform());
            }

            loadWithOptions(Glide.with(imageView), picUrl, imageView, options);
        } catch (Exception e) {
            Logger.e(TAG, "load url failed: ", e);
        }
    }
    public static void loadResource(ImageView imageView, int resourceId, boolean isGray) {
        try {
            RequestOptions options = new RequestOptions().placeholder(0)
                    .diskCacheStrategy(ImageLoadHelper.DEFAULT_CACHE_STRATEGY);
            if (isGray) {
                options = options.transform(new GrayTransform());
            }
            Glide.with(imageView).load(resourceId).apply(options).transition(sCrossFade).into(imageView);
        } catch (Exception e) {
            Logger.e(TAG, "load url failed: ", e);
        }
    }

    public static void loadResource(ImageView imageView, int resourceId) {
        loadResource(imageView, resourceId, false);
    }

    public static void loadWithOptions(RequestManager requestManager, String source, ImageView destination, RequestOptions options) {
        requestManager.load(source).apply(options).transition(sCrossFade).into(destination);
    }

    public static void loadGif(ImageView imageView, int resourceId, boolean isGray) {
        try {
            RequestOptions options = new RequestOptions().placeholder(0)
                    .diskCacheStrategy(ImageLoadHelper.DEFAULT_CACHE_STRATEGY);
            if (isGray) {
                options = options.transform(new GrayTransform());
            }
            Glide.with(imageView).load(resourceId).apply(options).transition(sCrossFade).into(imageView);
        } catch (Exception e) {
            Logger.e(TAG, "load url failed: ", e);
        }
    }

    /**
     * 彩图转换成灰色图片
     *
     * @param img
     * @return
     */
    public static Bitmap convertGrayImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey;
                if (pixels[width * i + j] == 0) {
                    continue;
                } else grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.44 + (float) green * 0.45 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        //创建空的bitmap时，格式一定要选择ARGB_4444,或ARGB_8888,代表有Alpha通道，RGB_565格式的不显示灰度
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }
}

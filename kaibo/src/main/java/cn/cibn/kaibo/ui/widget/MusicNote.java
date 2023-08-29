package cn.cibn.kaibo.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import cn.cibn.kaibo.R;

/**
 *
 */

public class MusicNote extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;

    /**
     * 心的个数
     */
    private ArrayList<ZanBean> zanBeen = new ArrayList<>();
    private Paint p;
    /**
     * 负责绘制的工作线程
     */
    private DrawThread drawThread;

    private Bitmap bitmap; //hand
    private float handX;
    private float handY;

    public MusicNote(Context context) {
        this(context, null);
    }

    public MusicNote(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicNote(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        p = new Paint();
        p.setAntiAlias(true);
        drawThread = new DrawThread();

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.like_hand);
    }

    /**
     * 点赞动作  添加心的函数 控制画面最大心的个数
     */
    public void addZanXin(ZanBean zanBean) {
        zanBeen.add(zanBean);
        if (zanBeen.size() > 30) {
            zanBeen.remove(0);
        }
        start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (drawThread == null) {
            drawThread = new DrawThread();
        }
        if(!drawThread.isRun){
            drawThread.start();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (drawThread != null) {
            drawThread.isRun = false;
            drawThread = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        handX = (float) (getWidth() - bitmap.getWidth()) / 2 + getContext().getResources().getDimensionPixelSize(R.dimen.dp_20);
        handY = getHeight() - bitmap.getHeight();
    }

    class DrawThread extends Thread {
        boolean isRun = false;

        @Override
        public void run() {
            super.run();
            isRun = true;
            while (isRun) {
                Canvas canvas = null;
                try {
                    synchronized (String.class) {
                        canvas = surfaceHolder.lockCanvas();
                        if (canvas == null) {
                            continue;
                        }
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        boolean isEnd = true;

                        if (zanBeen.size() > 0) {
                            canvas.drawBitmap(bitmap, handX, handY, p);
                        }

                        for (int i = 0; i < zanBeen.size(); i++) {
                            isEnd = zanBeen.get(i).isEnd;
                            zanBeen.get(i).draw(canvas, p);
                        }
                        if (isEnd) {
                            isRun = false;
                            drawThread = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        if (drawThread != null) {

//            for (int i = 0; i < zanBeen.size(); i++) {
//                zanBeen.get(i).pause();
//            }
            for (int i = 0; i < zanBeen.size(); i++) {
                zanBeen.get(i).stop();
            }

            drawThread.isRun = false;
            drawThread = null;
        }

    }

    public void start() {
        if (drawThread == null) {
//            for (int i = 0; i < zanBeen.size(); i++) {
//                zanBeen.get(i).resume();
//            }
            drawThread = new DrawThread();
            drawThread.start();
        }
    }

}

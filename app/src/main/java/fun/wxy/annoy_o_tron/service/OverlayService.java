package fun.wxy.annoy_o_tron.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fun.wxy.annoy_o_tron.DevFragment;
import fun.wxy.annoy_o_tron.R;
import fun.wxy.annoy_o_tron.utils.U;

public class OverlayService extends Service {
    private static final String TAG = OverlayService.class.getSimpleName();

    public static OverlayService instance;
    private WindowManager windowManager;
    private Context context;
    public MyImageView imageView;
    private ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
    private boolean isScheduleRunning = false;

    public OverlayService() {
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate()");
        super.onCreate();
        this.instance = this;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        context = this.getBaseContext();

        imageView = new MyImageView(this);
        Random rnd = new Random();
        int x = rnd.nextInt(2) == 1 ? 0x66ff0000 : 0x6600ff00;
        System.out.println(x);
        imageView.setBackgroundColor(x);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager.addView(imageView, params);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand()");
        super.onStartCommand(intent, flags, startId);

        // run every x secs.
        if (!isScheduleRunning) {
            scheduleTaskExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    System.out.println("do something every N secs");
                    Intent intent = new Intent();
                    intent.setAction(DevFragment.REDRAW_OVERLAY);
                    sendBroadcast(intent);
                }
            }, 2, 2, TimeUnit.SECONDS);
            isScheduleRunning = true;
        }




        return startId;
        // return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind()");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestory()");
        super.onDestroy();

        scheduleTaskExecutor.shutdown();
        isScheduleRunning = false;

        if (imageView != null) {
            windowManager.removeView(imageView);
            imageView = null;
        }
    }

    public class MyImageView extends ImageView {
        private Canvas canvas;
        public MyImageView(Context context) {
            super(context);
        }
        public MyImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        public MyImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(40);
            canvas.drawText("## Overlay ##", 250, 50, paint);
            this.canvas = canvas;
        }

        public Canvas getCanvas() {
            return canvas;
        }
    }
}

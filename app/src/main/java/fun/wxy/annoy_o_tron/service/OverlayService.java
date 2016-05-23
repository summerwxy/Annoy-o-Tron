package fun.wxy.annoy_o_tron.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fun.wxy.annoy_o_tron.receiver.OverlayReceiver;

public class OverlayService extends Service {
    private static final String TAG = OverlayService.class.getSimpleName();

    public static OverlayService instance;
    private WindowManager windowManager;
    public MyImageView imageView;
    private ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
    private boolean isScheduleRunning = false;
    public boolean isReceiverRunning = false;

    public OverlayService() {
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate()");
        super.onCreate();
        this.instance = this;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageView = new MyImageView(this);
        // imageView.setBackgroundColor(0x66ff0000);
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
            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (!isReceiverRunning) {
                        isReceiverRunning = true;
                        Log.v(TAG, "sendBroadcast() -> OverlayReceiver.REDRAW_ZOO_KEEPER_OVERLAY");
                        // sendBroadcast
                        Intent intent = new Intent();
                        intent.setAction(OverlayReceiver.REDRAW_ZOO_KEEPER_OVERLAY);
                        sendBroadcast(intent);
                    }
                }
            }, 1000, 200, TimeUnit.MILLISECONDS);

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
            canvas.drawText("### Zoo Keeper Overlay ###", 50, 50, paint);
        }

    }
}

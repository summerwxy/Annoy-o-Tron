package fun.wxy.annoy_o_tron.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {
    private static final String TAG = TimerService.class.getSimpleName();

    private boolean isScheduleRunning = false;
    private ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

    public TimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate()");
        super.onCreate();
        // run every x secs.
        if (!isScheduleRunning) {
            scheduleTaskExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    System.out.println("do something every N secs xxxxxxx");
                    ImageView iv = OverlayService.instance.imageView;
                    System.out.println(iv);
                    iv.setBackgroundColor(0x33ff0000);
                    System.out.println("set in task..okay");
                }
            }, 2, 2, TimeUnit.SECONDS);
            isScheduleRunning = true;
        }
    }


    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestory()");
        super.onDestroy();
        scheduleTaskExecutor.shutdown();
        isScheduleRunning = false;
    }
}

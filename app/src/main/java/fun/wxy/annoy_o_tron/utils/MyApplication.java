package fun.wxy.annoy_o_tron.utils;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化 LeanCloud
        AVOSCloud.initialize(this, "SrPuPujzJSjLEedh0LPu62rL-gzGzoHsz", "exCjXTtWmorrEe7Y9enUkqc8");
    }
}

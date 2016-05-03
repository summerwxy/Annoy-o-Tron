package fun.wxy.annoy_o_tron.dao;

import android.content.Context;

import de.greenrobot.dao.async.AsyncSession;

/**
 * Created by 0_o on 2016/5/3.
 */
public class DBHelper {

    private static DBHelper INSTANCE = null;

    public static DBHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBHelper(context);
        }
        return INSTANCE;
    }

    private static final String DB_NAME = "annoy-o-tron.db";
    private DaoSession daoSession;
    private AsyncSession asyncSession;

    private DBHelper(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public AsyncSession getAsyncSession() {
        return asyncSession;
    }

}

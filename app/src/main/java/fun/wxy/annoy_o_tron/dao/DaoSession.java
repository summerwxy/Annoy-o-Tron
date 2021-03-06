package fun.wxy.annoy_o_tron.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import fun.wxy.annoy_o_tron.dao.Test;
import fun.wxy.annoy_o_tron.dao.ShipHeader;
import fun.wxy.annoy_o_tron.dao.Ship;

import fun.wxy.annoy_o_tron.dao.TestDao;
import fun.wxy.annoy_o_tron.dao.ShipHeaderDao;
import fun.wxy.annoy_o_tron.dao.ShipDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig testDaoConfig;
    private final DaoConfig shipHeaderDaoConfig;
    private final DaoConfig shipDaoConfig;

    private final TestDao testDao;
    private final ShipHeaderDao shipHeaderDao;
    private final ShipDao shipDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        testDaoConfig = daoConfigMap.get(TestDao.class).clone();
        testDaoConfig.initIdentityScope(type);

        shipHeaderDaoConfig = daoConfigMap.get(ShipHeaderDao.class).clone();
        shipHeaderDaoConfig.initIdentityScope(type);

        shipDaoConfig = daoConfigMap.get(ShipDao.class).clone();
        shipDaoConfig.initIdentityScope(type);

        testDao = new TestDao(testDaoConfig, this);
        shipHeaderDao = new ShipHeaderDao(shipHeaderDaoConfig, this);
        shipDao = new ShipDao(shipDaoConfig, this);

        registerDao(Test.class, testDao);
        registerDao(ShipHeader.class, shipHeaderDao);
        registerDao(Ship.class, shipDao);
    }
    
    public void clear() {
        testDaoConfig.getIdentityScope().clear();
        shipHeaderDaoConfig.getIdentityScope().clear();
        shipDaoConfig.getIdentityScope().clear();
    }

    public TestDao getTestDao() {
        return testDao;
    }

    public ShipHeaderDao getShipHeaderDao() {
        return shipHeaderDao;
    }

    public ShipDao getShipDao() {
        return shipDao;
    }

}

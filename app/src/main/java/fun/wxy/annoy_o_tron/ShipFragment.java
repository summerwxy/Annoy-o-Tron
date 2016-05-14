package fun.wxy.annoy_o_tron;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.adrianwalker.multilinestring.Multiline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fun.wxy.annoy_o_tron.dao.DBHelper;
import fun.wxy.annoy_o_tron.dao.DaoSession;
import fun.wxy.annoy_o_tron.dao.Ship;
import fun.wxy.annoy_o_tron.dao.ShipDao;
import fun.wxy.annoy_o_tron.dao.ShipHeader;
import fun.wxy.annoy_o_tron.dao.ShipHeaderDao;
import fun.wxy.annoy_o_tron.utils.U;


public class ShipFragment extends Fragment {

    public final static int MESSAGE_WHAT_REDRAW_RECYCLER_VIEW = 56131516;
    public ShipFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ship, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextInputEditText editText = (TextInputEditText) getActivity().findViewById(R.id.ship_date);

        // default date
        Calendar cal = Calendar.getInstance();
        editText.setText(U.formatDate(cal.getTime()));

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // disable input method
                editText.setInputType(InputType.TYPE_NULL);
                return false;
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString();
                Date date = U.parseDate(str);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        editText.setText(U.formatDate(cal.getTime()));
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        final Button btn = (Button) getActivity().findViewById(R.id.download_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String date = editText.getText().toString();

                DaoSession daoSession = DBHelper.getInstance(getContext()).getDaoSession();
                QueryBuilder<ShipHeader> builder = daoSession.getShipHeaderDao().queryBuilder();
                builder.where(ShipHeaderDao.Properties.Ship_date.eq(date));
                if (builder.count() > 0) {
                    Snackbar.make(getView(), "日期：" + date + " 已下载，删除后可重新下载。", Snackbar.LENGTH_LONG).show();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            loadShipDataFromMSSql2Sqlite(date);
                            Message msg = mHandler.obtainMessage();
                            msg.what = MESSAGE_WHAT_REDRAW_RECYCLER_VIEW;
                            msg.sendToTarget();
                        }
                    }.start();
                }
            }
        });
        redrawRecyclerView();
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MESSAGE_WHAT_REDRAW_RECYCLER_VIEW) {
                redrawRecyclerView();
            }
            super.handleMessage(msg);
        }
    };

    /**
     use AIS20121019100529
     select FCustName as 门店, FPrtclsName as 中分类, FItemNumber as 品号, FItemName as 品名, sum(FOrderQty) as 订单数量, sum(FQty) as 发货数量, FOrdClsName as 订单类型, FAuxTaxPrice as 单价, FBillNo as 出库单号, FStatus
     into #aa
     from (select FCustName, FPrtClsName, FOrderBillNO, FItemNumber, FItemName, FOrderQty, FQty, FInterID, FOrdClsName, FAuxTaxPrice, FBillNo, FStatus from
       (SELECT t1.FOrderBillNO, t3.FName FCustName, t2.FNumber FItemNumber, t2.FName FItemName, t1.FOrderQty, t1.FQty, t.FInterID, isnull(t4.FName,'') FOrdClsName, isnull(t5.FName,'') FPrtClsName, t6.FAuxTaxPrice,t8.FBillNo, isnull(t1.FStatus,0) as FStatus
       FROM t_AWE_SalesDelivery t
       INNER JOIN t_AWE_SalesDeliveryEntry t1 ON t.FInterID = t1.FInterID
       INNER JOIN SEOrderEntry t6 ON t1.FOrderInterID = t6.FInterID and t1.FOrderEntryID = t6.FEntryID
       left join ICStockBillEntry t7 on t1.FICStockInterID = t7.FInterID and t1.FICStockEntryID = t7.FEntryID
       left join ICStockBill t8 on t7.FInterID = t8.FInterID
       INNER JOIN t_ICItem t2 ON t.FItemID = t2.FItemID
       inner join (select t2.FItemID,t1.FName
         from t_Item t1
         inner join t_Item t2 on t1.fitemid = t2.FParentID
         where t1.FDetail = 0 And t1.FItemClassID = 4
         and t2.FDetail=1 and t2.FItemClassID =4) t5 on t.FItemID=t5.FItemID
       INNER JOIN t_Organization t3 ON t1.FCustomerID = t3.FItemID
       left join (select FInterID, FID, FName from t_SubMessage where FTypeID=10010) t4 on t1.FOrdCls = t4.FInterID
       WHERE 1=1 and t2.F_101 in (select Finterid from t_submessage where FName = '常态产品' and FTypeID = 10009)
     AND CONVERT(VARCHAR(20), t1.FDeliveryDate,120) >= ? -- '2016-04-21 00:00:00'  --@变量
     AND CONVERT(VARCHAR(20), t1.FDeliveryDate,120) <= ? -- '2016-04-21 23:59:59'  --@变量
     union all
     SELECT t1.FOrderBillNO, t3.FName FCustName, t2.FNumber FItemNumber, t2.FName FItemName, t1.FOrderQty, t1.FQty, t.FInterID, isnull(t4.FName, '') FOrdClsName, isnull(k1.FName, '') FPrtClsName, t6.FAuxTaxPrice, t8.FBillNo, isnull(t1.FStatus, 0) as FStatus
     FROM t_AWE_SalesDelivery t
     INNER JOIN t_AWE_SalesDeliveryEntry t1 ON t.FInterID = t1.FInterID
     INNER JOIN SEOrderEntry t6 ON t1.FOrderInterID = t6.FInterID and t1.FOrderEntryID = t6.FEntryID
     left join ICStockBillEntry t7 on t1.FICStockInterID = t7.FInterID and t1.FICStockEntryID = t7.FEntryID
     left join ICStockBill t8 on t7.FInterID = t8.FInterID
     INNER JOIN t_ICItem t2 ON t.FItemID = t2.FItemID
     inner join (select t2.FItemID, t1.FName
       from t_Item t1
       inner join t_Item t2 on t1.fitemid = t2.FParentID
       where t1.FDetail = 0 And t1.FItemClassID = 4
       and t2.FDetail = 1 and t2.FItemClassID = 4) t5 on t.FItemID = t5.FItemID
     INNER JOIN t_Organization t3 ON t1.FCustomerID = t3.FItemID
     left join (select FInterID,FID,FName from t_SubMessage where FTypeID=10009) k1 on t2.F_101 = k1.FInterID
     left join (select FInterID,FID,FName from t_SubMessage where FTypeID=10010) t4 on t1.FOrdCls = t4.FInterID
     WHERE 1 = 1 and t2.F_101 not in (select Finterid from t_submessage where FName = '常态产品' and FTypeID = 10009)
     AND CONVERT(VARCHAR(20), t1.FDeliveryDate, 120) >= ? -- '2016-04-21 00:00:00'   --@变量
     AND CONVERT(VARCHAR(20), t1.FDeliveryDate, 120) <= ? -- '2016-04-21 23:59:59'   --@变量
     ) k ) kk
     group by FCustName, FPrtclsName, FItemNumber, FItemName, FOrdClsName, FAuxTaxPrice, FBillNo, FStatus
     order by FBillNo

     -- select * from #aa where 品号 in ('11.02.0017', '16.11.0001', '16.11.0002') order by 品号, 门店
     select * from #aa order by 品号, 门店
     */
    @Multiline static String sql;

    private void loadShipDataFromMSSql2Sqlite(String date) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        // greendao
        DaoSession daoSession = DBHelper.getInstance(getContext()).getDaoSession();
        daoSession.getDatabase().beginTransaction();
        try {
            // save to ShipHeader
            ShipHeader sh = new ShipHeader();
            sh.setShip_date(date);
            sh.setCreated_date(new Date());
            daoSession.getShipHeaderDao().insert(sh);

            // query from ms sql server
            conn = U.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, date + " 00:00:00");
            stmt.setString(2, date + " 23:59:59");
            stmt.setString(3, date + " 00:00:00");
            stmt.setString(4, date + " 23:59:59");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Ship it = new Ship();
                it.setShip_date(date);
                it.setStore_name(rs.getString("门店"));
                it.setCategory(rs.getString("中分类"));
                it.setItem_no(rs.getString("品号"));
                it.setItem_name(rs.getString("品名"));
                it.setOrder_qty(rs.getInt("订单数量"));
                it.setShip_qty(rs.getInt("发货数量"));
                it.setOrder_type(rs.getString("订单类型"));
                it.setPrice(rs.getDouble("单价"));
                it.setShip_no(rs.getString("出库单号"));
                it.setStatus(rs.getString("FStatus"));
                daoSession.getShipDao().insert(it);
            }
            daoSession.getDatabase().setTransactionSuccessful();

            Snackbar.make(getView(), "日期：" + date + " 下载完毕！", Snackbar.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            daoSession.getDatabase().endTransaction();
        }
    }

    private void redrawRecyclerView() {
        DaoSession daoSession = DBHelper.getInstance(getContext()).getDaoSession();
        QueryBuilder<ShipHeader> builder = daoSession.getShipHeaderDao().queryBuilder();
        List<ShipHeader> list = builder.orderDesc(ShipHeaderDao.Properties.Ship_date).list();
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        ShipHeadersAdapter adapter = new ShipHeadersAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }




    // ===========================================================================
    // view holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView shipDate;
        public TextView createDate;
        public Button deleteBtn;
        public Button detailBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            shipDate = (TextView) itemView.findViewById(R.id.ship_date);
            createDate = (TextView) itemView.findViewById(R.id.created_date);
            deleteBtn = (Button) itemView.findViewById(R.id.delete_btn);
            detailBtn = (Button) itemView.findViewById(R.id.detail_btn);
        }
    }


    // adapter
    public class ShipHeadersAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<ShipHeader> shipHeader;

        public ShipHeadersAdapter(List<ShipHeader> sh){
            shipHeader = sh;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_ship_date_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ShipHeader sh = shipHeader.get(position);
            TextView shipDate = holder.shipDate;
            shipDate.setText(sh.getShip_date());
            TextView createdDate = holder.createDate;
            createdDate.setText(sh.getCreated_date().toString());
            Button deleteBtn = holder.deleteBtn;
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // greendao
                    DaoSession daoSession = DBHelper.getInstance(v.getContext()).getDaoSession();
                    daoSession.getDatabase().beginTransaction();
                    try {
                        QueryBuilder<ShipHeader> builder = daoSession.getShipHeaderDao().queryBuilder();
                        builder.where(ShipHeaderDao.Properties.Ship_date.eq(sh.getShip_date()));
                        builder.buildDelete().executeDeleteWithoutDetachingEntities();

                        QueryBuilder<Ship> builder2 = daoSession.getShipDao().queryBuilder();
                        builder2.where(ShipDao.Properties.Ship_date.eq(sh.getShip_date()));
                        builder2.buildDelete().executeDeleteWithoutDetachingEntities();

                        daoSession.getDatabase().setTransactionSuccessful();
                    } finally {
                        daoSession.getDatabase().endTransaction();
                    }

                    Message msg = mHandler.obtainMessage();
                    msg.what = ShipFragment.MESSAGE_WHAT_REDRAW_RECYCLER_VIEW;
                    msg.sendToTarget();

                    Snackbar.make(getView(), "日期：" + sh.getShip_date() + " 删除完毕！", Snackbar.LENGTH_LONG).show();
                }
            });
            Button detailBtn = holder.detailBtn;
            detailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    Fragment frag = new ShipDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("date", sh.getShip_date());
                    frag.setArguments(bundle);
                    MainActivity.replaceFragment(frag, manager, v.getRootView());
                }
            });

        }

        @Override
        public int getItemCount() {
            return shipHeader.size();
        }

    }


}

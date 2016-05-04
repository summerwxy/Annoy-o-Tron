package fun.wxy.annoy_o_tron.list;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import fun.wxy.annoy_o_tron.R;
import fun.wxy.annoy_o_tron.ShipFragment;
import fun.wxy.annoy_o_tron.dao.DBHelper;
import fun.wxy.annoy_o_tron.dao.DaoSession;
import fun.wxy.annoy_o_tron.dao.Ship;
import fun.wxy.annoy_o_tron.dao.ShipDao;
import fun.wxy.annoy_o_tron.dao.ShipHeader;
import fun.wxy.annoy_o_tron.dao.ShipHeaderDao;

/**
 * Created by 0_o on 2016/5/3.
 */
public class ShipList {

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
    public class ShipHeadersAdapter extends RecyclerView.Adapter<ShipList.ViewHolder> {
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
                    // TODO: delete function
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

                    // TODO: redraw  這邊會出錯 應該跟自己把 ShipFragment new 出來有關係.
                    System.out.println("delete okay...redraw ui");
                    Message msg = new ShipFragment().mHandler.obtainMessage();
                    msg.what = ShipFragment.MESSAGE_WHAT_REDRAW_RECYCLER_VIEW;
                    msg.sendToTarget();

                }
            });
            Button detailBtn = holder.detailBtn;
            detailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: go detail fragment view
                    System.out.println("show detail");
                }
            });

        }

        @Override
        public int getItemCount() {
            return shipHeader.size();
        }

    }

}

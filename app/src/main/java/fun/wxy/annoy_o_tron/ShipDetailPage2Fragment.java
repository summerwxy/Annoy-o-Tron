package fun.wxy.annoy_o_tron;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fun.wxy.annoy_o_tron.dao.Ship;


public class ShipDetailPage2Fragment extends Fragment {

    String itemName = null;
    List<Ship> data = null;

    public ShipDetailPage2Fragment() {
        // Required empty public constructor
    }

    public void setData(String itemName, List<Ship> data) {
        this.itemName = itemName;
        this.data = data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ship_detail_page2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvItemName = (TextView) view.findViewById(R.id.tv_item_name);
        tvItemName.setText(itemName);

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.rv_page2);
        ShipDetailPage2Adapter adapter = new ShipDetailPage2Adapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    // ===========================================================================
    // view holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvStore;
        public ViewHolder(View itemView) {
            super(itemView);
            tvStore = (TextView) itemView.findViewById(R.id.tv_store);
        }

    }

    // adapter
    public class ShipDetailPage2Adapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Ship> data;

        public ShipDetailPage2Adapter(List<Ship> data){
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_ship_detail_page2, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (position < getItemCount() - 1) {
                final Ship it = data.get(position);
                holder.tvStore.setText(it.getStore_name() + " * " + it.getShip_qty());
                holder.tvStore.setGravity(Gravity.LEFT);
                holder.tvStore.setBackgroundColor(0xffffffff);
            } else {
                holder.tvStore.setText("===== end of data =====");
                holder.tvStore.setGravity(Gravity.CENTER);
                holder.tvStore.setBackgroundColor(0xffff5555);
            }
        }

        @Override
        public int getItemCount() {
            return data.size() + 1; // 加上 end of data
        }

    }
}

package fun.wxy.annoy_o_tron;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;


public class ShipDetailPage1Fragment extends Fragment {

    private List<String[]> data;

    public ShipDetailPage1Fragment() {
        // Required empty public constructor
    }

    public void setData(List<String[]> data) {
        this.data = data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ship_detail_page1, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_page1);
        ShipDetailPage1Adapter adapter = new ShipDetailPage1Adapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // ===========================================================================
    // view holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        public Button btn;
        public ViewHolder(View itemView) {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.btn);
        }

    }

    // adapter
    public class ShipDetailPage1Adapter extends RecyclerView.Adapter<ViewHolder> {
        private List<String[]> data;

        public ShipDetailPage1Adapter(List<String[]> data){
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_ship_detail_page1_button, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (position < data.size()) {
                final String[] it = data.get(position);
                Button btn = holder.btn;
                btn.setText(it[0] + "  " + it[1]);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewPager pager = (ViewPager) v.getRootView().findViewById(R.id.ship_detail_pager);
                        pager.setCurrentItem(position + 1);
                    }
                });
                btn.getBackground().setColorFilter(null);
            } else { // 多一個 end of data
                Button btn = holder.btn;
                btn.setText("===== end of data =====");
                btn.getBackground().setColorFilter(0xffff5555, android.graphics.PorterDuff.Mode.MULTIPLY );
            }
        }

        @Override
        public int getItemCount() {
            return data.size() + 1; // 多一個 end of data
        }

    }

}

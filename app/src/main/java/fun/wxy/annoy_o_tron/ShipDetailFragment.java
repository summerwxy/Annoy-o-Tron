package fun.wxy.annoy_o_tron;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;
import fun.wxy.annoy_o_tron.dao.DBHelper;
import fun.wxy.annoy_o_tron.dao.DaoSession;
import fun.wxy.annoy_o_tron.dao.Ship;
import fun.wxy.annoy_o_tron.dao.ShipDao;


public class ShipDetailFragment extends Fragment {

    private List<String[]> itemData = new ArrayList<>();
    private Map<String, List<Ship>> storeData = new HashMap<>();
    private String date = null;

    public ShipDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ship_detail, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            date = getArguments().getString("date");
        }

        DaoSession daoSession = DBHelper.getInstance(getContext()).getDaoSession();
        QueryBuilder<Ship> builder = daoSession.getShipDao().queryBuilder();
        builder.where(ShipDao.Properties.Ship_date.eq(date));
        // builder.orderAsc(ShipDao.Properties.Item_no);
        // builder.orderAsc(ShipDao.Properties.Store_name); // 中文排序方式與SQL不一樣, 不要用這個排序
        builder.orderAsc(ShipDao.Properties.Id); // 下載資料時, 已經排過序, 所以就用 id 去排序
        List<Ship> rs = builder.list();

        String lastItemNo = null;
        for (int i = 0; i < rs.size(); i++) {
            Ship it = rs.get(i);
            if (lastItemNo == null || !lastItemNo.equals(it.getItem_no())) {
                String[] foo = {it.getItem_no(), it.getItem_name()};
                itemData.add(foo);
                storeData.put(it.getItem_no(), new ArrayList<Ship>());
            }
            storeData.get(it.getItem_no()).add(it);
            lastItemNo = it.getItem_no();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button topBack = (Button) view.findViewById(R.id.top_back);
        TextView topDate = (TextView) view.findViewById(R.id.top_date);
        Button topItem = (Button) view.findViewById(R.id.top_item);
        final ViewPager pager = (ViewPager) view.findViewById(R.id.ship_detail_pager);

        topDate.setText(date);
        // 選擇日期
        topBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.renderFragment(null, "frag_" + R.id.navi_ship, getFragmentManager(), v.getRootView());
            }
        });
        topItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0);
            }
        });


        // 必須放在 找資料之後
        MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                ShipDetailPage1Fragment frag = new ShipDetailPage1Fragment();
                frag.setData(itemData);
                return frag;
            } else if (position > 0 && position < getCount()) {
                ShipDetailPage2Fragment frag = new ShipDetailPage2Fragment();
                String[] it = itemData.get(position - 1);
                String itemNo = it[0] + " " + it[1];
                List<Ship> data = storeData.get(it[0]);
                frag.setData(itemNo, data);
                return frag;
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return itemData.size() + 1; // 多一個物品清單
        }


    }



}

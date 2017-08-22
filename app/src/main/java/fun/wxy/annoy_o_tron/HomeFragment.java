package fun.wxy.annoy_o_tron;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) view.findViewById(R.id.btn_test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = null;
                String tag = "frag_" + R.id.navi_wevideo;
                if (getActivity().getSupportFragmentManager().findFragmentByTag(tag) == null) {
                    frag = new WeVideoFragment();
                }
                MainActivity.renderFragment(frag, tag, getFragmentManager(), v.getRootView());
            }
        });


        Button btn2 = (Button) view.findViewById(R.id.btn_dev);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = null;
                String tag = "frag_" + R.id.navi_dev;
                if (getActivity().getSupportFragmentManager().findFragmentByTag(tag) == null) {
                    frag = new DevFragment();
                }
                MainActivity.renderFragment(frag, tag, getFragmentManager(), v.getRootView());
            }
        });

        Button btn3 = (Button) view.findViewById(R.id.btn_run_sql);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = null;
                String tag = "frag_" + R.id.navi_run_sql;
                if (getActivity().getSupportFragmentManager().findFragmentByTag(tag) == null) {
                    frag = new RunSqlFragment();
                }
                MainActivity.renderFragment(frag, tag, getFragmentManager(), v.getRootView());
            }
        });

    }
}

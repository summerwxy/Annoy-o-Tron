package fun.wxy.annoy_o_tron;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class WeVideoFragment extends Fragment {

    public WeVideoFragment() {
        // Required empty public constructor
    }

    public static WeVideoFragment newInstance(String param1, String param2) {
        WeVideoFragment fragment = new WeVideoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_we_video, container, false);
    }

}

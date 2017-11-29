package fun.wxy.annoy_o_tron;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.android.gms.internal.zzs.TAG;


public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

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

        Button btn4 = (Button) view.findViewById(R.id.btn_firebase);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                myRef.setValue("Hello, World!");
                System.out.println("Hello, World!!");
                */

//                Intent myIntent = new Intent(view.getContext(), GoogleSignInActivity.class);
//                view.getContext().startActivity(myIntent);

            }
        });


    }





}

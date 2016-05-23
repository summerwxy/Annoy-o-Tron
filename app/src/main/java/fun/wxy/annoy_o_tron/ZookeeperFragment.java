package fun.wxy.annoy_o_tron;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import fun.wxy.annoy_o_tron.receiver.OverlayReceiver;
import fun.wxy.annoy_o_tron.service.OverlayService;
import fun.wxy.annoy_o_tron.utils.U;


public class ZookeeperFragment extends Fragment {
    private static final String TAG = ZookeeperFragment.class.getSimpleName();

    private boolean isOverlayReceiverRegister = false;


    public ZookeeperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_zookeeper, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 檢查有沒有寫的權限
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "i have not Manifest.permission.WRITE_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
        // 檢查能不能在其他 app 之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, 1234);
                Toast.makeText(getContext(), "allow this app over others app.", Toast.LENGTH_LONG).show();
            }
        }
        // 先抓一次 獲取 root 權限
        U.snapshot("dummy.png");


        final OverlayReceiver overlayReceiver =  new OverlayReceiver();
        Button startOverlayService = (Button) getActivity().findViewById(R.id.btn_start_overlay_service);
        startOverlayService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onclick: Start OverlayService");
                Intent intent = new Intent(getActivity(), OverlayService.class);
                getActivity().startService(intent);
                // register
                getActivity().registerReceiver(overlayReceiver, new IntentFilter(OverlayReceiver.REDRAW_ZOO_KEEPER_OVERLAY));
                isOverlayReceiverRegister = true;
            }
        });

        Button stopOverlayService = (Button) getActivity().findViewById(R.id.btn_stop_overlay_service);
        stopOverlayService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onclick: Stop OverlayService");
                Intent intent = new Intent(getActivity(), OverlayService.class);
                getActivity().stopService(intent);
                // unregister
                if (isOverlayReceiverRegister) {
                    getActivity().unregisterReceiver(overlayReceiver);
                    isOverlayReceiverRegister = false;
                }
            }
        });

    }

}

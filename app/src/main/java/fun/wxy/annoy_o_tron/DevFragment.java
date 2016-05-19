package fun.wxy.annoy_o_tron;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fun.wxy.annoy_o_tron.service.OverlayService;
import fun.wxy.annoy_o_tron.utils.U;


public class DevFragment extends Fragment {
    private static final String TAG = DevFragment.class.getSimpleName();


    public DevFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dev, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "i have not Manifest.permission.WRITE_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    111);
        }



        Button screenshot = (Button) getActivity().findViewById(R.id.btn_screenshot);
        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                String fn = DateFormat.format("yyyy-MM-dd_hh_mm_ss", now).toString();
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fn + ".jpg";

                try {
                    View view = getActivity().getWindow().getDecorView().getRootView();
                    view.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
                    view.setDrawingCacheEnabled(false);

                    File imageFile = new File(path);
                    FileOutputStream stream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                    stream.flush();
                    stream.close();

                    Log.v(TAG, "Image Saved okay!! path: " + path);
                    // 刷新 media cache, 這樣透過 MTP 才可以看到檔案
                    MediaScannerConnection.scanFile(getContext(), new String[] { imageFile.getAbsolutePath() }, null, null);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        Button startOverlayService = (Button) getActivity().findViewById(R.id.btn_start_overlay_service);
        startOverlayService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onclick: Start OverlayService");
                Intent intent = new Intent(getActivity(), OverlayService.class);
                getActivity().startService(intent);
            }
        });

        Button stopOverlayService = (Button) getActivity().findViewById(R.id.btn_stop_overlay_service);
        stopOverlayService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onclick: Stop OverlayService");
                Intent intent = new Intent(getActivity(), OverlayService.class);
                getActivity().stopService(intent);
            }
        });

        Button testButton = (Button) getActivity().findViewById(R.id.btn_dev_test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "click test button");
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/zk2.png";
                File imgFile = new  File(path);
                if(imgFile.exists()){
                    Log.v(TAG, "image file exist");
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    // TODO: find pixel location in image
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setColor(Color.RED);
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                    Canvas canvas = new Canvas(mutableBitmap);

                    int len = canvas.getWidth() / 8;

                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            int x = len * i;
                            int y = 500 + len * j; // 500 for Nexus 5X
                            canvas.drawCircle(x, y, 5, paint); // TODO: REMOVE - paint a circle
                            BLOCK block = detectIcon(bitmap, x, y, len);
                            System.out.println(block);
                        }
                        System.out.println("-----------------");
                    }


                    ImageView iv = (ImageView) getActivity().findViewById(R.id.iv_dev_image);
                    iv.setAdjustViewBounds(true);
                    iv.setImageBitmap(mutableBitmap);
                }

            }
        });
    }

    private enum BLOCK {
        Hippo, Giraffe, Panda, Ape, Crocodile, Lion, Elephant,
        Remove_Hippo, Remove_Giraffe, Remove_Panda, Remove_Ape, Remove_Crocodile, Remove_Lion, Remove_Elephant,
        UNKNOW
    }

    // TODO: finish this
    private BLOCK detectIcon(Bitmap bitmap, int x, int y, int len) {
        BLOCK result = BLOCK.UNKNOW;
        // pass bg color when picked
        Set<String> bgSet = new HashSet<>();
        bgSet.add("#FAFCDC");
        bgSet.add("#FAFCDE");
        bgSet.add("#FBF8CB");
        bgSet.add("#FBF8CD");
        bgSet.add("#FCF5C0");
        bgSet.add("#FCF5C2");

        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < 200; i++) {
            String rgb = getColorString(bitmap, x, y, len);
            if (bgSet.contains(rgb)) { // 取到非主要顏色 就跳過
                continue; // pass it
            }
            if (!map.containsKey(rgb)) {
                map.put(rgb, 0);
            }
            map.put(rgb, map.get(rgb) + 1);
        }

        map = U.sortByValueDesc(map);
        boolean isRemove = map.containsKey("#00D880");

        if (map.containsKey("#480C60") && map.containsKey("#D070A8")) {
            result = (isRemove) ? BLOCK.Remove_Hippo : BLOCK.Hippo;
        } else if (map.containsKey("#481800") && map.containsKey("#F8F804")) {
            result = (isRemove) ? BLOCK.Remove_Giraffe : BLOCK.Giraffe;
        } else if (map.containsKey("#181818") && map.containsKey("#F9F8F8")) {
            result = (isRemove) ? BLOCK.Remove_Panda : BLOCK.Panda;
        } else if (map.containsKey("#501000") && map.containsKey("#F86046")) {
            result = (isRemove) ? BLOCK.Remove_Ape : BLOCK.Ape;
        } else if (map.containsKey("#013820") && map.containsKey("#48D000")) {
            result = (isRemove) ? BLOCK.Remove_Crocodile : BLOCK.Crocodile;
        } else if (map.containsKey("#400C00") && map.containsKey("#F8A800")) {
            result = (isRemove) ? BLOCK.Remove_Lion : BLOCK.Lion;
        } else if (map.containsKey("#082078") && map.containsKey("#80C8F8")) {
            result = (isRemove) ? BLOCK.Remove_Elephant : BLOCK.Elephant;
        } else {
            Log.v(TAG, map.toString());
        }
        return result;
    }

    private String getColorString(Bitmap bitmap, int x, int y, int len) {
        int xp = (int) (Math.random() * len);
        int yp = (int) (Math.random() * len);
        int colorInt = bitmap.getPixel(x + xp, y + yp);
        return String.format("#%06X", (0xFFFFFF & colorInt));
    }


}

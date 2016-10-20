package fun.wxy.annoy_o_tron;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


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

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 檢查有沒有 Camera 權限
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "i have not Manifest.permission.CAMERA");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
        }

        mCamera = getCameraInstance();
        if (mCamera == null) {
            Toast.makeText(getContext(), "no camera permission!", Toast.LENGTH_LONG).show();
        } else {
            mCameraPreview = new CameraPreview(getContext(), mCamera);
            final FrameLayout preview = (FrameLayout) getActivity().findViewById(R.id.camera_preview);
            preview.addView(mCameraPreview);

            // set width == height
            ViewTreeObserver vto = preview.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    preview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int len = Math.min(preview.getWidth(), preview.getHeight());
                    preview.setLayoutParams(new LinearLayout.LayoutParams(len, len));
                }
            });

        }





        Button btn = (Button) getActivity().findViewById(R.id.button_capture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: xx
            }
        });
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.v(TAG, "camera is not available (in use or does not exist)");
        }
        return c;
    }


    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // install a surfaceholder.callback so we get notified when the underlying surface is created and destoryed
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on android version prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // the surface has benn created, now tell the camera where to draw the preview
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                mCamera.setDisplayOrientation(90);
                mCamera.autoFocus(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // if your preview can change or rotate, take care of those events here.
            // make sure to stop the preview before resizing or reformatting it
            if (mHolder.getSurface() == null) {
                return;
            }
            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or reformatting changes here
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.d(TAG, "error starting camera preview: " + e.getMessage());
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. take care of releasing the camera preview in your activity
        }
    }


    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };
}

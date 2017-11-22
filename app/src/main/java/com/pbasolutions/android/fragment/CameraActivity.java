package com.pbasolutions.android.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pbasolutions.android.R;
import com.pbasolutions.android.utils.BaseAlbumDirFactory;
import com.pbasolutions.android.utils.CameraUtil;

public class CameraActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    private Button capture/*, switchCamera*/;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private static  final int FOCUS_AREA_SIZE = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        initialize();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        cameraFront = getIntent().getBooleanExtra("isFront", false);
    }

    private int checkFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (checkFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
//                switchCamera.setVisibility(View.GONE);
            }
            if (cameraFront)
                mCamera = Camera.open(findFrontFacingCamera());
            else mCamera = Camera.open(findBackFacingCamera());
            mPicture = getPictureCallback();
            setupCameraParams(mCamera);
            mPreview.refreshCamera(mCamera);
        }
    }

    private Camera setupCameraParams(Camera mCamera) {
        if (mCamera == null) return mCamera;

        try {
            mCamera.setDisplayOrientation(90);
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            int widthPixels = getResources().getDisplayMetrics().widthPixels;
            int heightPixels = getResources().getDisplayMetrics().heightPixels;
            Camera.Size optimalSize = getOptimalPreviewSize(sizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
            if (widthPixels >= optimalSize.height)
                params.setPreviewSize(optimalSize.width, optimalSize.height);
            else {
                double ratio = heightPixels / widthPixels;
                double width = optimalSize.height * ratio;
                params.setPreviewSize(Integer.parseInt(width+""), optimalSize.height);
            }
            params.setRotation(90);
            mCamera.setParameters(params);
            cameraPreview.getLayoutParams().height = optimalSize.width;
            cameraPreview.getLayoutParams().width = optimalSize.height;
            cameraPreview.requestLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.07;
        final double HEIGHT_TOLERANCE = 200;
        double targetRatio = (double) w / h;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        int targetHeight = w;

        // Find size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
//            if (Math.abs(size.height - targetHeight) < minDiff) {
            if (Math.abs(size.height - targetHeight) <= HEIGHT_TOLERANCE && Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void initialize() {
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captureListener);

//        switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
//        switchCamera.setOnClickListener(switchCameraListener);

        cameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    focusOnTouch(event);
                }
                return true;
            }
        });
    }

    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null ) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                if (parameters.getMaxNumMeteringAreas() > 0) {
                    Rect rect = calculateFocusArea(event.getX(), event.getY());

                    if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    else if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO))
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    else parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                    List<Camera.Area> focusAreas = new ArrayList<>();
                    focusAreas.add(new Camera.Area(rect, 600));
                    parameters.setFocusAreas(focusAreas);
                    parameters.setMeteringAreas(focusAreas);

                    mCamera.setParameters(parameters);
                    mCamera.autoFocus(mAutoFocusTakePictureCallback);
                } else {
                    mCamera.autoFocus(mAutoFocusTakePictureCallback);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / cameraPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / cameraPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize/2 > 1000){
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize/2;
            } else {
                result = -1000 + focusAreaSize/2;
            }
        } else{
            result = touchCoordinateInCameraReper - focusAreaSize/2;
        }
        return result;
    }

//    OnClickListener switchCameraListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            //get the number of cameras
//            int camerasNumber = Camera.getNumberOfCameras();
//            if (camerasNumber > 1) {
//                //release the old camera instance
//                //switch camera, from the front and the back and vice versa
//
//                releaseCamera();
//                chooseCamera();
//            } else {
//                Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
//                toast.show();
//            }
//        }
//    };

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                setupCameraParams(mCamera);
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                setupCameraParams(mCamera);
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private PictureCallback getPictureCallback() {
        PictureCallback picture = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast toast = Toast.makeText(myContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
                    toast.show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("pic_path", pictureFile.getAbsolutePath());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    OnClickListener captureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamera.takePicture(null, null, mPicture);
        }
    };

    Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus","success!");
            } else {
                // do something...
                Log.i("tap_to_focus","fail!");
            }
        }
    };

    //make picture and save to a folder
    private static File getOutputMediaFile() {
//        File mediaFile = CameraUtil.setUpPhotoFile(context.mAlbumStorageDirFactory);
//        context.mCurrentPhotoPath = mediaFile.getAbsolutePath();
//        CameraUtil.galleryAddPic(context.mCurrentPhotoPath, getActivity());
        File mediaFile = null;

        try {
            mediaFile = CameraUtil.setUpPhotoFile(new BaseAlbumDirFactory());

//            //make a new file directory inside the "sdcard" folder
//            File mediaStorageDir = new File(Environment.getDataDirectory().getAbsolutePath(), "JCG Camera");
//
//            //if this "JCGCamera folder does not exist
//            if (!mediaStorageDir.exists()) {
//                //if you cannot make this folder return
//                if (!mediaStorageDir.mkdirs()) {
//                    return null;
//                }
//            }
//
//            //take the current timeStamp
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            //and make a media file:
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mediaFile;
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
package com.pbasolutions.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;
import android.widget.ImageView;

import com.pbasolutions.android.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pbadell on 10/23/15.
 */
public class CameraUtil {

    /**
     * Class name tag.
     */
    private static final String TAG = "CameraUtil";

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final String ALBUM_NAME = "Pandora Album";
    public static final int CAPTURE_PROF_PIC = 1;
    public static final int CAPTURE_ATTACH_1 = 2;
    public static final int CAPTURE_ATTACH_2 = 3;
    public static final int CAPTURE_ATTACH_3 = 4;
    public static final int CAPTURE_ATTACH_4 = 5;
    public static final int CAPTURE_ATTACH_5 = 6;
    public static final int CAPTURE_ATTACH_6 = 7;
    public static final int CAPTURE_ATTACH_7 = 8;
    public static final int CAPTURE_ATTACH_8 = 9;
    public static final int CAPTURE_ATTACH_9 = 10;
    public static final int CAPTURE_ATTACH_10 = 11;

    public static void  setPic(final ImageView mImageView, final String mCurrentPhotoPath) {
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */
        mImageView.post(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap = resizeImage(mImageView.getMeasuredWidth(),
                        mImageView.getMeasuredHeight(), mCurrentPhotoPath);
		/* Associate the Bitmap to the ImageView */
                mImageView.setImageBitmap(bitmap);
            }
        });
    }

    private static Bitmap resizeImage(int targetW, int targetH, String mCurrentPhotoPath){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        bmOptions.inDither = true;
		/* Decode the JPEG file into a Bitmap */
        Bitmap scaledBmp = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException ioe) {
            return scaledBmp;
        }

        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString == null? ExifInterface.ORIENTATION_NORMAL : Integer.parseInt(orientString);

        int rotateAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotateAngle = 90;
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotateAngle = 180;
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotateAngle = 270;

        if (rotateAngle > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateAngle);

            return Bitmap.createBitmap(scaledBmp, 0, 0, scaledBmp.getWidth(), scaledBmp.getHeight(), matrix, true);
        }
        else {
            return scaledBmp;
        }
    }
    private static void galleryAddPic(String mCurrentPhotoPath, Activity activity) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    public static  void handleBigCameraPhoto(ImageView imageView, String mCurrentPhotoPath, Activity activity) {
        if (mCurrentPhotoPath != null) {
            setPic(imageView, mCurrentPhotoPath);
            addPathToPic(imageView, mCurrentPhotoPath);
            galleryAddPic(mCurrentPhotoPath, activity);
        }
    }

    private static void addPathToPic(ImageView imageView, String mCurrentPhotoPath) {
        imageView.setTag(mCurrentPhotoPath);
    }

    private static File getAlbumDir(AlbumStorageDirFactory mAlbumStorageDirFactory) {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(ALBUM_NAME);

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(TAG, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }


    private static File createImageFile(AlbumStorageDirFactory mAlbumStorageDirFactory) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir(mAlbumStorageDirFactory);
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;

    }

    public static File setUpPhotoFile(AlbumStorageDirFactory mAlbumStorageDirFactory) throws IOException {
        File f = null;

        f = createImageFile(mAlbumStorageDirFactory);
        return f;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap .compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    public static void urlToBitmap(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
    }

    /**
     * Method to get Base64 string from an image path.
     * @param path
     * @return
     */
    public static String imageToBase64(String path){
        if (path != null) {
            Bitmap immagex = resizeImage(500, 500, path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            File file = new File(path);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                baos.writeTo(outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
            return imageEncoded;
       }
            return null;
    }

    /**
     *
     * @param path
     * @param imageView
     */
    public static void loadPicture(String path, ImageView imageView) {
        if (path != null) {
            setPic(imageView, path);
        }
    }

}

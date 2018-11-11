package com.cudocomm.troubleticket.takephoto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import com.cudocomm.troubleticket.util.Constants;
import com.cudocomm.troubleticket.util.Logcat;
import com.cudocomm.troubleticket.util.Preferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakeImage  {

    private static AlbumStorageDirFactory mAlbumStorageDirFactory;
    private Activity act;
    private Bitmap mBitmap;
    private String mCurrentPhotoPath;
    private String mPathName;
    private String picturePath;
    private String thumbnailName;

    private Preferences preferences;

//    add
    private Fragment fragment;

    static {
        TakeImage.mAlbumStorageDirFactory = null;
    }

    public TakeImage(final Activity act, Preferences preferences) {
        this.act = act;
        this.preferences = preferences;
    }

    public TakeImage(final Activity act, Fragment fragment, Preferences preferences) {
        this.act = act;
        this.fragment = fragment;
        this.preferences = preferences;
    }

    private void saveThumbnail(final String s) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(s, options);
        options.inSampleSize = convertSizeImage(options, 300, 300);
        options.inJustDecodeBounds = false;
        final Bitmap decodeFile = BitmapFactory.decodeFile(s, options);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        decodeFile.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        final File file = new File(TakeImage.mAlbumStorageDirFactory.getAlbumStorageDir("tts/thumbs"), System.currentTimeMillis() + ".jpg");

        try {
            file.createNewFile();
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            this.setPathThumbName(file.toString());
            fileOutputStream.close();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }

    public void checkSdcard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    public int convertSizeImage(final BitmapFactory.Options bitmapFactory$Options, int round, final int n) {
        final int outHeight = bitmapFactory$Options.outHeight;
        final int outWidth = bitmapFactory$Options.outWidth;
        int round2 = 1;
        if (outHeight > n || outWidth > round) {
            round2 = Math.round(outHeight / n);
            round = Math.round(outWidth / round);
            if (round2 >= round) {
                return round;
            }
        }
        return round2;
    }

    public File createImageFile() throws IOException {
        String s;
        if (mPathName != null) {
            s = this.mPathName;
        }
        else {
            s = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        }
        Logcat.i("PHOTO TEMP FILE");
        final File tempFile = File.createTempFile(s, ".png", getAlbumDir());
        setPathName(tempFile.toString());
        return tempFile;
    }

    public void dispatchTakePictureIntent(final int n, ImageView imageView) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        while (true) {
            try {
                final File setUpPhotoFile = this.setUpPhotoFile();
                this.mCurrentPhotoPath = setUpPhotoFile.getAbsolutePath();

                Uri photoURI;
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    photoURI = FileProvider.getUriForFile(act, "com.cudocomm.troubleticket.provider", setUpPhotoFile);
                } else {
                    photoURI = Uri.fromFile(setUpPhotoFile);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(setUpPhotoFile));
                preferences.savePreferences(Constants.IV_TARGET_DT, imageView.getId());

//                this.fragment.startActivityForResult(intent, n);
                act.startActivityForResult(intent, n);
            }
            catch (IOException ex) {
                ex.printStackTrace();
                this.mCurrentPhotoPath = null;
                continue;
            }
            break;
        }
    }

    public void dispatchUploadPictureIntent(final int n) {
        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        final File file = new File("");
        this.mCurrentPhotoPath = file.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        this.act.startActivityForResult(intent, n);
    }

    public Bitmap fromGallery(final Intent intent) {
        final Cursor loadInBackground = new CursorLoader(this.act, intent.getData(), new String[] { "_data" }, null, null, null).loadInBackground();
        final int columnIndexOrThrow = loadInBackground.getColumnIndexOrThrow("_data");
        loadInBackground.moveToFirst();
        final String string = loadInBackground.getString(columnIndexOrThrow);
        final BitmapFactory.Options bitmapFactory$Options = new BitmapFactory.Options();
        bitmapFactory$Options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(string, bitmapFactory$Options);
        int inSampleSize;
        for (inSampleSize = 1; bitmapFactory$Options.outWidth / inSampleSize / 2 >= 200 && bitmapFactory$Options.outHeight / inSampleSize / 2 >= 200; inSampleSize *= 2) {}
        bitmapFactory$Options.inSampleSize = inSampleSize;
        bitmapFactory$Options.inJustDecodeBounds = false;
        this.setPathName(string);
        return BitmapFactory.decodeFile(string, bitmapFactory$Options);
    }

    public void galleryAddPic() {
        final Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(this.mCurrentPhotoPath)));
        this.act.sendBroadcast(intent);
    }

    @SuppressLint({ "LongLogTag" })
    public File getAlbumDir() {
        File file = null;
        Logcat.i("External Storage is " + Environment.getExternalStorageState());
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            final File albumStorageDir = TakeImage.mAlbumStorageDirFactory.getAlbumStorageDir("tts");
            if (albumStorageDir != null && !albumStorageDir.mkdirs() && !albumStorageDir.exists()) {
                Log.d("TAKE PHOTO AND CHOOSE IMAGE", "failed to create directory");
                return null;
            }
            final File albumStorageDir2 = TakeImage.mAlbumStorageDirFactory.getAlbumStorageDir("tts/thumbs");
            file = albumStorageDir;
            if (albumStorageDir2 != null) {
                file = albumStorageDir;
                if (!albumStorageDir2.mkdirs()) {
                    file = albumStorageDir;
                    if (!albumStorageDir2.exists()) {
                        Log.d("TAKE PHOTO AND CHOOSE IMAGE", "failed to create directory");
                        return null;
                    }
                }
            }
        }
        else {
            Log.v("TAKE PHOTO AND CHOOSE IMAGE", "External storage is not mounted READ/WRITE.");
        }
        return file;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getPathName() {
        return mPathName;
    }

    public String getPathThumbName() {
        return thumbnailName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public Object handleCameraPhoto(final ImageView imageView, final PhotoFrom photoFrom, final Intent intent) {
        Object setPic;
        if (mCurrentPhotoPath != null && PhotoFrom.CAMERA == photoFrom) {
            galleryAddPic();
            mCurrentPhotoPath = null;
            saveThumbnail(getPathName());
            setPic = setPic(imageView, getPathName());
        } else {
            setPic = intent;
            if (mCurrentPhotoPath != null) {
                setPic = intent;
                if (PhotoFrom.GALLERY == photoFrom) {
                    try {
                        final Uri data = intent.getData();
                        final String[] array = { "_data" };
                        final Cursor query = this.act.getContentResolver().query(data, array, null, null, null);
                        query.moveToFirst();
                        this.setPicturePath(query.getString(query.getColumnIndex(array[0])));
                        query.close();
                        this.mCurrentPhotoPath = null;
                        return this.setPic(imageView, this.getPicturePath());
                    }
                    catch (Exception ex) {
                        return intent;
                    }
                }
            }
        }
        return setPic;
    }

    public void setPathName(final String mPathName) {
        this.mPathName = mPathName;
    }

    public void setPathThumbName(final String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }

    public Object setPic(final ImageView imageView, final String s) {
        final BitmapFactory.Options bitmapFactory$Options = new BitmapFactory.Options();
        bitmapFactory$Options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(s, bitmapFactory$Options);
        bitmapFactory$Options.inSampleSize = this.convertSizeImage(bitmapFactory$Options, 150, 150);
        bitmapFactory$Options.inJustDecodeBounds = false;
        final Bitmap decodeFile = BitmapFactory.decodeFile(s, bitmapFactory$Options);
        if (imageView != null) {
            imageView.setImageBitmap(decodeFile);
        }
        return this.mBitmap = decodeFile;
    }

    public void setPicturePath(final String picturePath) {
        this.picturePath = picturePath;
    }

    public File setUpPhotoFile() throws IOException {
        final File imageFile = this.createImageFile();
        this.mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    public enum PhotoFrom
    {
        CAMERA,
        GALLERY
    }
}

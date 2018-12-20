package kz.iitu.cloudy.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import kz.iitu.cloudy.R;
import kz.iitu.cloudy.databinding.ActivityPhotoBinding;
import kz.iitu.cloudy.model.Photo;

public class PhotoActivity extends AppCompatActivity
        implements View.OnClickListener, Target {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String ARG_LINK = "link";
    private static final String ARG_PHOTO_ID = "photo_id";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_HASHTAG = "hashtag";

    private String mLink;
    private String mUsername;
    private String mPhotoId;

    private ActivityPhotoBinding mPhotoBinding;
    
    public static void start(Context context, String link,
                             String username, String photoId,
                             String hashtag) {
        Intent starter = new Intent(context, PhotoActivity.class);
        starter.putExtra(ARG_LINK, link);
        starter.putExtra(ARG_USERNAME, username);
        starter.putExtra(ARG_PHOTO_ID, photoId);
        starter.putExtra(ARG_HASHTAG, hashtag);

        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_photo);

        mLink = getIntent().getStringExtra(ARG_LINK);
        mUsername = getIntent().getStringExtra(ARG_USERNAME);
        mPhotoId = getIntent().getStringExtra(ARG_PHOTO_ID);

        setupViews();
    }

    private void setupViews() {
        Picasso.get()
                .load(mLink)
                .into(mPhotoBinding.photoView);

        mPhotoBinding.orderPhotoButton.setOnClickListener(this);
        mPhotoBinding.downloadButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_photo_button:
                OrderPhotoActivity.start(this, mUsername, mPhotoId,
                        getIntent().getStringExtra(ARG_HASHTAG));
                break;
            case R.id.download_button:
                verifyStoragePermissions(this);
                Picasso.get().load(mLink).into(this);
        }
    }

    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String appPath = Environment.getExternalStorageDirectory().getPath() + "/Cloudy";
                File appFolder = new File(appPath);

                if (!appFolder.exists()) {
                    appFolder.mkdir();
                }

                String hashPath = appPath + "/" + getIntent().getStringExtra(ARG_HASHTAG);

                File hashFolder = new File(hashPath);
                if (!hashFolder.exists()) {
                    hashFolder.mkdir();
                }

                File file = new File(hashPath + "/" + mPhotoId + ".jpg");

                try {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
                    ostream.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PhotoActivity.this, "Фотография успешно сохранена в папку Cloudy/"
                                    + getIntent().getStringExtra(ARG_HASHTAG), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Picasso.get().load(mLink).into(this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

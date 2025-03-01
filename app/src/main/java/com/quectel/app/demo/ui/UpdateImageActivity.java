package com.quectel.app.demo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.quectel.app.demo.R;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.utils.CameraUtils;
import com.quectel.app.demo.utils.GetImgFromAlbum;
import com.quectel.app.demo.utils.GetPhotoFromPhotoAlbum;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.quecnetwork.httpservice.IHttpUpLoadFile;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UpdateImageActivity  extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_update_headimage;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg);

    }
    @Override
    protected void initData() {

    }

    private static final int OPEN_PHOTO_REQUEST_CODE = 1001;
    @OnClick({R.id.iv_back,R.id.bt_change})
    public void buttonClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.bt_change:
                CameraUtils.openPhotoAlbum(activity, OPEN_PHOTO_REQUEST_CODE);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_PHOTO_REQUEST_CODE&&resultCode == RESULT_OK) {
            String photoPath = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                photoPath =  GetImgFromAlbum.getRealPathFromUri(activity,data.getData());
                Uri mImageCaptureUri = Uri.parse(photoPath);
                Bitmap photoBmp = null;
                if (mImageCaptureUri != null) {
                    try {
                        photoBmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
                        Bitmap finalPhotoBmp = photoBmp;
                        Observable.create(new ObservableOnSubscribe<File>() {
                            @Override
                            public void subscribe(ObservableEmitter<File> emitter) throws Exception {

                                String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                if (!storageDir.exists()) {
                                    storageDir.mkdir();
                                }
                                File  tempFile = new File(storageDir, imageName+".jpg");
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                                finalPhotoBmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                                bos.flush();
                                bos.close();
                                emitter.onNext(tempFile);
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<File>() {
                            @SuppressLint("CheckResult")
                            @Override
                            public void accept(File file) throws Exception {
                                startLoading();
                                UserServiceFactory.getInstance().getService(IUserService.class).uploadFile(file, new IHttpUpLoadFile() {
                                    @Override
                                    public void onSuccess(String result) {
                                        finishLoading();
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (obj.getInt("code") == 200) {
                                                String headImg = obj.getJSONObject("data").getString("src");
                                                if(!TextUtils.isEmpty(headImg))
                                                {
                                                    UserServiceFactory.getInstance().getService(IUserService.class).updateUserHeadImage(headImg, new IHttpCallBack() {
                                                        @Override
                                                        public void onSuccess(String result) {
                                                            JSONObject  obj = null;
                                                            try {
                                                                obj = new JSONObject(result);
                                                                if (obj.getInt("code") == 200) {
                                                                    Intent intent = new Intent();
                                                                    setResult(1111,intent);
                                                                    finish();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                        @Override
                                                        public void onFail(Throwable throwable) {

                                                        }
                                                    });
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    @Override
                                    public void onFail(Throwable e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onLoading(long total, long current) {

                                    }

                                    @Override
                                    public void onFinished() {
                                        System.out.println("onFinished--file:");
                                    }
                                });

                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            else
            {
                photoPath = GetPhotoFromPhotoAlbum.getRealPathFromUri(activity, data.getData());
                UserServiceFactory.getInstance().getService(IUserService.class).uploadFile(new File(photoPath), new IHttpUpLoadFile() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.getInt("code") == 200) {
                                String headImg = obj.getJSONObject("data").getString("src");
                                if(!TextUtils.isEmpty(headImg))
                                {
                                    UserServiceFactory.getInstance().getService(IUserService.class).updateUserHeadImage(headImg, new IHttpCallBack() {
                                        @Override
                                        public void onSuccess(String result) {
                                            JSONObject  obj = null;
                                            try {
                                                obj = new JSONObject(result);
                                                if (obj.getInt("code") == 200) {
                                                    Intent intent = new Intent();
                                                    setResult(1111,intent);
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        @Override
                                        public void onFail(Throwable throwable) {

                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onFail(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onLoading(long total, long current) {

                    }

                    @Override
                    public void onFinished() {
                        System.out.println("onFinished--file:");
                    }
                });
            }
        }
    }
}

package com.quectel.app.demo.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.quectel.app.demo.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;

public abstract class BaseActivity extends SupportActivity {
    protected Activity activity;
    
    protected boolean fullscreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getFullScreenFlg()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        
        if (getScreenOnFlg()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        
        if (getContentLayout() != 0) {
            setContentView(getContentLayout());
        }

        activity = this;

        addHeadColor();
        
        ButterKnife.bind(this);

        EventBus.getDefault().register(activity);
        
        initData();
    }
    
    public Activity getMyActivity() {
        return activity;
    }
    
    @Subscribe
    public void onEvent(Object event) {
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();

    }
    
    @Override
    protected void onPause() {
        super.onPause();

    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    

    public boolean getFullScreenFlg() {
        return false;
    }

    public boolean getScreenOnFlg() {
        return false;
    }

    protected abstract int getContentLayout();
    protected abstract void addHeadColor();
    

    protected abstract void initData();

    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    Dialog mDialog = null;
    public  void startLoading()
    {
        if(mDialog==null)
        {
            mDialog = MyUtils.createDialog(activity);
            mDialog.show();
        }
    }

    public  void finishLoading()
    {
        if(mDialog!=null)
        {
            mDialog.dismiss();
            mDialog = null;
        }

    }

}

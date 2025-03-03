package com.acceleronix.app.demo.fragmentbase;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;


public abstract class BaseMainFragment extends SupportFragment {
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    @Override
    public boolean onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            _mActivity.finish();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(_mActivity, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        getNeedArguments(bundle);
        EventBus.getDefault().register(this);
    }

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutView(), container, false);
        unbinder = ButterKnife.bind(this, view);
        processBusiness();
        return view;
    }


    @Subscribe
    public void onEvent(Object object) {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    protected abstract void getNeedArguments(Bundle bundle);
    protected abstract int getLayoutView();
    protected abstract void processBusiness();

    Dialog mDialog = null;
    public  void startLoading()
    {
        if(mDialog==null)
        {
            mDialog = MyUtils.createDialog(getActivity());
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

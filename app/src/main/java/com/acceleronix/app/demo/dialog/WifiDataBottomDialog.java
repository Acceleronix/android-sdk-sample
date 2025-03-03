package com.acceleronix.app.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.databinding.SmartConfigViewWifiConfigBinding;


public class WifiDataBottomDialog extends Dialog {

    private Context context;
    private SmartConfigViewWifiConfigBinding binding;
    private OnConfirmClickListener onConfirmClickListener;
    private int position;

    public WifiDataBottomDialog(@NonNull Context context) {
        this(context, R.style.smart_config_BottomDialog);
        this.context = context;
    }

    public WifiDataBottomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected WifiDataBottomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setSSidAndPwd(String ssid, String pwd) {
        binding.etWifiName.setText(ssid);
        binding.etWifiPwd.setText(pwd);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.smart_config_BottomAnim);

        binding = SmartConfigViewWifiConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLayout();
        initEvent();
    }


    private void initLayout() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = wm.getDefaultDisplay().getWidth();
        getWindow().setAttributes(params);
    }

    public void setOnConfirmClickListener(OnConfirmClickListener confirmClickListener) {
        if (confirmClickListener != null) {
            this.onConfirmClickListener = confirmClickListener;
        }
    }

    private void initEvent() {
        binding.ivQuit.setOnClickListener(view -> {
            if (isShowing()) {
                cancel();
            }
        });


        binding.ivChangeWifi.setOnClickListener(view -> {
            context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        });

        binding.cbPswShow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.etWifiPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                binding.etWifiPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        binding.btnNext.setOnClickListener(view -> {
            if (onConfirmClickListener != null) {
                onConfirmClickListener.onConfirm(binding.etWifiName.getText().toString().trim(),
                        binding.etWifiPwd.getText().toString(), position);
            }
        });

    }


    public void setPosition(int position) {
        this.position = position;
    }

    public interface OnConfirmClickListener {
        void onConfirm(String ssid, String pwd, int position);
    }

}

package com.quectel.app.demo.widget;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.quectel.app.demo.R;
public class PayBottomDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private int layoutRes;
    private View view;
    private int[] clickIds;
    private OnBottomItemClickListener listener;

    public PayBottomDialog(Context context, int layoutRes, int[] clickIds) {
        super(context, R.style.dialog_full);
        this.context = context;
        this.layoutRes = layoutRes;
        this.clickIds = clickIds;
    }

    public PayBottomDialog(Activity mActivity, View view, int[] clickIds) {
        super(mActivity, R.style.dialog_full);
        this.context = mActivity;
        this.view = view;
        this.clickIds = clickIds;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogBottomAnimation);
        if (view != null) {
            setContentView(view);
        } else {
            setContentView(layoutRes);
        }
        getWindow().setLayout(-1, -1);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        if (clickIds != null) {
            for (int i = 0; i < clickIds.length; i++) {
                int clickId = clickIds[i];
                findViewById(clickId).setOnClickListener(this);
            }
        }
    }

    public View getView() {
        return view == null ? getLayoutInflater().inflate(layoutRes, (ViewGroup) null) : view;
    }

    public void onClick(View view) {
        this.listener.onBottomItemClick(this, view);
    }

    public void setOnBottomItemClickListener(OnBottomItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnBottomItemClickListener {
        void onBottomItemClick(PayBottomDialog payBottomDialog, View view);
    }

    public void bottmShow() {
        if (!isShowing()) {
            show();
        }
    }

}

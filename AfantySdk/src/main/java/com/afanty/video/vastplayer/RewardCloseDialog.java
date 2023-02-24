package com.afanty.video.vastplayer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.afanty.R;
import com.afanty.utils.DeviceUtils;

public class RewardCloseDialog extends Dialog {
    private Context mContext;

    private OnConfirmClickListener onConfirmClickListener;
    private OnCancelClickListener onCancelClickListener;

    public RewardCloseDialog(@NonNull Context context) {
        super(context, R.style.aft_columbus_player_reward_dialog);

        this.mContext = context;
    }

    public interface OnConfirmClickListener {
        void doConfirm();
    }

    public interface OnCancelClickListener {
        void doCancel();
    }

    public RewardCloseDialog setConfirmButton(OnConfirmClickListener onClickListener) {
        this.onConfirmClickListener = onClickListener;
        return this;
    }

    public RewardCloseDialog setCancelButton(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.aft_reward_close_dialog, null);

        setContentView(view);

        Button btnConfirm = view.findViewById(R.id.btn_player_close_confirm);
        Button btnCancel = view.findViewById(R.id.btn_player_close_cancel);

        btnConfirm.setOnClickListener(new CustomDialogClickListener());
        btnCancel.setOnClickListener(new CustomDialogClickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int screenWidth = DeviceUtils.getScreenWidthByWindow(mContext);
        int screenHeight = DeviceUtils.getScreenHeightByWindow(mContext);

        if (screenHeight > screenWidth) {
            lp.width = (int) (screenWidth * 0.92);
        } else {
            lp.width = (int) (screenWidth * 0.5);
        }

        dialogWindow.setAttributes(lp);
    }


    private class CustomDialogClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_player_close_confirm) {
                if (onConfirmClickListener != null) {
                    onConfirmClickListener.doConfirm();
                }
            } else if (view.getId() == R.id.btn_player_close_cancel) {
                dismiss();
                if (onCancelClickListener != null) {
                    onCancelClickListener.doCancel();
                }
            }
        }
    }
}

package com.badonlabs.brigham.moonlit.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.badonlabs.brigham.moonlit.R;

import io.particle.android.sdk.cloud.ParticleDevice;

public class DeviceFragment extends Fragment {

    private ImageView mStatusIcon;
    private TextView mStatusMsg;
    private ParticleDevice mMotionSensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container);
        mStatusIcon = (ImageView) view.findViewById(R.id.statusIcon);
        mStatusMsg = (TextView) view.findViewById(R.id.statusMsg);
        updateDeviceStatus();
        return view;
    }

    public ParticleDevice getMotionSensor() {
        return mMotionSensor;
    }

    public void setMotionSensor(ParticleDevice motionSensor) {
        mMotionSensor = motionSensor;
        updateDeviceStatus();
    }

    public void updateDeviceStatus() {
        if (mMotionSensor == null || mStatusIcon == null) {
            return;
        }

        Window window = getActivity().getWindow();
        View view = window.getDecorView();
        if (mMotionSensor.isConnected()) {
            if (mMotionSensor.isRunningTinker()) {
                view.setBackgroundColor(getResources().getColor(R.color.colorStatusDeveloper));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(getResources().getColor(R.color.colorStatusDeveloperDark));
                }
                mStatusMsg.setText(R.string.status_tinker);
                mStatusIcon.setImageResource(R.drawable.ic_developer_mode_48dp);
            } else if (mMotionSensor.isFlashing()) {
                view.setBackgroundColor(getResources().getColor(R.color.colorStatusWarning));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(getResources().getColor(R.color.colorStatusWarningDark));
                }
                mStatusMsg.setText(R.string.status_flashing);
                mStatusIcon.setImageResource(R.drawable.ic_update_48dp);
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.colorStatusOk));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(getResources().getColor(R.color.colorStatusOkDark));
                }
                mStatusMsg.setText(R.string.status_connected);
                mStatusIcon.setImageResource(R.drawable.ic_connected_48dp);
            }
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.colorStatusWarning));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorStatusWarningDark));
            }
            mStatusMsg.setText(R.string.status_disconnected);
            mStatusIcon.setImageResource(R.drawable.ic_disconnected_48dp);
        }
    }
}
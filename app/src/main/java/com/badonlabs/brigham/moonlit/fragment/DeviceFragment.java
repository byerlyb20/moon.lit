package com.badonlabs.brigham.moonlit.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.badonlabs.brigham.moonlit.DeviceManager;
import com.badonlabs.brigham.moonlit.MainActivity;
import com.badonlabs.brigham.moonlit.R;

import java.util.List;

import io.particle.android.sdk.cloud.ParticleDevice;

public class DeviceFragment extends Fragment {

    private ImageView mStatusIcon;
    private TextView mStatusMsg;
    private int mPosition;
    private ParticleDevice mMotionSensor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        // Get ParticleDevice instance
        mPosition = args.getInt("position");
        DeviceManager.getInstance(getContext()).getDevices(new DeviceManager.OnDevicesReceived() {
            @Override
            public void onDevicesReceived(List<ParticleDevice> devices) {
                mMotionSensor = devices.get(mPosition);
                updateDeviceView();
                if (isVisible()) {
                    updateActivityView();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActivityView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        mStatusIcon = (ImageView) view.findViewById(R.id.statusIcon);
        mStatusMsg = (TextView) view.findViewById(R.id.statusMsg);
        updateDeviceView();

        return view;
    }

    public void updateActivityView() {
        if (mMotionSensor == null) {
            return;
        }

        DeviceManager.getInstance(getContext()).setCurrentDevicePosition(mPosition);
        MainActivity activity = (MainActivity) getActivity();
        activity.currentFragment = this;
        activity.showProgress(false);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mMotionSensor.getName());
        }
        Window window = activity.getWindow();
        View view = window.getDecorView();

        Menu menu = activity.getMenu();
        if (menu != null) {
            MenuItem settingsMenuItem = activity.getMenu().findItem(R.id.settings);
            if (mMotionSensor.isConnected()) {
                settingsMenuItem.setVisible(true);
            } else {
                settingsMenuItem.setVisible(false);
            }
        }

        if (mMotionSensor.isConnected()) {
            if (mMotionSensor.isRunningTinker()) {
                view.setBackgroundColor(getResources().getColor(R.color.colorStatusDeveloper));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(getResources().getColor(R.color.colorStatusDeveloperDark));
                }
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.colorStatusOk));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(getResources().getColor(R.color.colorStatusOkDark));
                }
            }
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.colorStatusWarning));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorStatusWarningDark));
            }
        }
    }

    public void updateDeviceView() {
        // Check to make sure the motion sensor and the layout have initialized
        if (mMotionSensor == null || mStatusIcon == null) {
            return;
        }

        if (mMotionSensor.isConnected()) {
            if (mMotionSensor.isRunningTinker()) {
                mStatusMsg.setText(R.string.status_tinker);
                mStatusIcon.setImageResource(R.drawable.ic_developer_mode_48dp);
            } else {
                mStatusMsg.setText(R.string.status_connected);
                mStatusIcon.setImageResource(R.drawable.ic_connected_48dp);
            }
        } else {
            mStatusMsg.setText(R.string.status_disconnected);
            mStatusIcon.setImageResource(R.drawable.ic_disconnected_48dp);
        }
    }

    public ParticleDevice getMotionSensor() {
        return mMotionSensor;
    }
}
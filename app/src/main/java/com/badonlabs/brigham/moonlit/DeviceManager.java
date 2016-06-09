package com.badonlabs.brigham.moonlit;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;

public class DeviceManager {

    private static DeviceManager deviceManager;
    private Context mContext;
    private int mCurrentDevice = 0;
    private List<ParticleDevice> mDevices;

    public DeviceManager(Context context) {
        mContext = context;
    }

    public static synchronized DeviceManager getInstance(Context context) {
        if (deviceManager != null) {
            return deviceManager;
        } else {
            ParticleCloudSDK.init(context);
            deviceManager = new DeviceManager(context);
            return deviceManager;
        }
    }

    public void getDevices(final OnDevicesReceived listener) {
        if (mDevices != null) {
            listener.onDevicesReceived(mDevices);
        } else {
            new GetDevicesTask(new OnDevicesReceived() {
                @Override
                public void onDevicesReceived(List<ParticleDevice> devices) {
                    mDevices = devices;
                    listener.onDevicesReceived(devices);
                }
            }).execute();
        }
    }

    public int getCurrentDevicePosition() {
        return mCurrentDevice;
    }

    public void setCurrentDevicePosition(int currentDevice) {
        mCurrentDevice = currentDevice;
    }

    public interface OnDevicesReceived {
        void onDevicesReceived(List<ParticleDevice> devices);
    }

    public class GetDevicesTask extends AsyncTask<Void, Void, List<ParticleDevice>> {

        protected OnDevicesReceived mListener;

        public GetDevicesTask(OnDevicesReceived listener) {
            mListener = listener;
        }

        @Override
        protected List<ParticleDevice> doInBackground(Void... params) {
            try {
                if (!ParticleCloudSDK.getCloud().isLoggedIn()) {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    return null;
                }
                return ParticleCloudSDK.getCloud().getDevices();
            } catch (ParticleCloudException e) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra("error", true);
                mContext.startActivity(intent);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<ParticleDevice> devices) {
            if (devices != null) {
                mListener.onDevicesReceived(devices);
            }
        }
    }
}

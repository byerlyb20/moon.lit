package com.badonlabs.brigham.moonlit.prefs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.badonlabs.brigham.moonlit.DeviceManager;
import com.badonlabs.brigham.moonlit.R;
import com.larswerkman.lobsterpicker.LobsterPicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

public class ColorPreference extends DialogPreference {

    private ParticleDevice mMotionSensor;
    private ProgressBar mProgress;
    private LobsterPicker mColorPicker;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        DeviceManager.getInstance(getContext()).getDevices(new DeviceManager.OnDevicesReceived() {
            @Override
            public void onDevicesReceived(List<ParticleDevice> devices) {
                mMotionSensor = devices.get(DeviceManager.getInstance(getContext())
                        .getCurrentDevicePosition());

                if (mColorPicker != null) {
                    setColor();
                }
            }
        });

        setPersistent(false);

        setDialogLayoutResource(R.layout.dialog_color);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        mColorPicker = (LobsterPicker) view.findViewById(R.id.colorPicker);

        if (mMotionSensor != null) {
            setColor();
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            Async.executeAsync(mMotionSensor, new Async.ApiWork<ParticleDevice, Void>() {
                public Void callApi(ParticleDevice motionSensor)
                        throws ParticleCloudException, IOException {
                    int color = mColorPicker.getColor();
                    ArrayList<String> parameters = new ArrayList<>();
                    parameters.add(Integer.toString(color));
                    try {
                        mMotionSensor.callFunction("setLEDColor", parameters);
                    } catch (ParticleDevice.FunctionDoesNotExistException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public void onSuccess(Void nothing) {

                }

                @Override
                public void onFailure(ParticleCloudException e) {

                }
            });
        }
    }

    private void setColor() {
        Async.executeAsync(mMotionSensor, new Async.ApiWork<ParticleDevice, Integer>() {
            public Integer callApi(ParticleDevice motionSensor)
                    throws ParticleCloudException, IOException {
                try {
                    return motionSensor.getIntVariable("ledColor");
                } catch (ParticleDevice.VariableDoesNotExistException e) {
                    e.printStackTrace();
                    return 0;
                }
            }

            @Override
            public void onSuccess(Integer color) {
                mColorPicker.setColor(color);
                setLoading(false);
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                setColor();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            mProgress.setVisibility(View.VISIBLE);
            mColorPicker.setVisibility(View.GONE);
        } else {
            mProgress.setVisibility(View.GONE);
            mColorPicker.setVisibility(View.VISIBLE);
        }
    }
}

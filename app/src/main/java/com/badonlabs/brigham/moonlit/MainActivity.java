package com.badonlabs.brigham.moonlit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.badonlabs.brigham.moonlit.fragment.DeviceFragment;

import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity {

    private DeviceFragment mDeviceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDeviceFragment = (DeviceFragment) getFragmentManager().findFragmentById(R.id.fragment);
        ParticleCloudSDK.init(this);
        new GetDevicesTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                ParticleDevice motionSensor = mDeviceFragment.getMotionSensor();
                if (motionSensor != null) {
                    Async.executeAsync(motionSensor, new Async.ApiWork<ParticleDevice, Void>() {

                        public Void callApi(@NonNull ParticleDevice particleDevice) throws ParticleCloudException {
                            particleDevice.refresh();
                            return null;
                        }

                        @Override
                        public void onSuccess(@NonNull Void value) {
                            mDeviceFragment.updateDeviceStatus();
                            Log.v("MainActivity", "Refresh success");
                        }

                        @Override
                        public void onFailure(@NonNull ParticleCloudException e) {
                            Log.v("MainActivity", "Refresh fail");
                        }
                    });
                }
                break;
        }
        return true;
    }

    public class GetDevicesTask extends AsyncTask<Void, Void, List<ParticleDevice>> {

        GetDevicesTask() {
        }

        @Override
        protected List<ParticleDevice> doInBackground(Void... params) {
            try {
                Log.v("MainActivity", "Is logged in: " + ParticleCloudSDK.getCloud().isLoggedIn());
                if (!ParticleCloudSDK.getCloud().isLoggedIn()) {
                    Log.v("MainActivity", "Starting login activity");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return null;
                }
                return ParticleCloudSDK.getCloud().getDevices();
            } catch (ParticleCloudException e) {
                e.printStackTrace();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("error", true);
                startActivity(intent);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<ParticleDevice> devices) {
            if (devices != null) {
                if (devices.size() == 0) {
                    // Set up a new device
                } else {
                    mDeviceFragment.setMotionSensor(devices.get(0));
                    getSupportActionBar().setTitle(mDeviceFragment.getMotionSensor().getName());
                }
            }
        }
    }
}

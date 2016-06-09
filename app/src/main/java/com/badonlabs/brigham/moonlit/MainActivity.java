package com.badonlabs.brigham.moonlit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.badonlabs.brigham.moonlit.adapter.DeviceFragmentAdapter;
import com.badonlabs.brigham.moonlit.fragment.DeviceFragment;

import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {

    public DeviceFragment currentFragment;
    private DeviceFragmentAdapter mAdapter;
    private ViewPager mViewPager;
    private ProgressBar mProgress;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        showProgress(true);

        DeviceManager.getInstance(this).getDevices(new DeviceManager.OnDevicesReceived() {
            @Override
            public void onDevicesReceived(List<ParticleDevice> devices) {
                FragmentManager fm = getSupportFragmentManager();
                mAdapter = new DeviceFragmentAdapter(fm, devices);
                mViewPager.setAdapter(mAdapter);
                CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
                if (indicator != null) {
                    indicator.setViewPager(mViewPager);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        if (currentFragment != null) {
            MenuItem settingsMenuItem = menu.findItem(R.id.settings);
            if (currentFragment.getMotionSensor().isConnected()) {
                settingsMenuItem.setVisible(true);
            } else {
                settingsMenuItem.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return true;
    }

    private void refresh() {
        try {
            final ParticleDevice motionSensor = currentFragment.getMotionSensor();
            Async.executeAsync(motionSensor, new Async.ApiWork<ParticleDevice, Void>() {

                public Void callApi(@NonNull ParticleDevice particleDevice)
                        throws ParticleCloudException {
                    particleDevice.refresh();

                    return null;
                }

                @Override
                public void onSuccess(@NonNull Void value) {
                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout)
                            findViewById(R.id.coordinator);
                    if (coordinatorLayout != null) {
                        Snackbar.make(coordinatorLayout, R.string.refresh_success,
                                Snackbar.LENGTH_SHORT).show();
                    }
                    currentFragment.updateDeviceView();
                    currentFragment.updateActivityView();
                }

                @Override
                public void onFailure(@NonNull ParticleCloudException e) {
                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout)
                            findViewById(R.id.coordinator);
                    if (coordinatorLayout != null) {
                        Snackbar.make(coordinatorLayout, R.string.refresh_error,
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (NullPointerException e) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout)
                    findViewById(R.id.coordinator);
            if (coordinatorLayout != null) {
                Snackbar.make(coordinatorLayout, R.string.refresh_error,
                        Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public void showProgress(final boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (currentFragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            if (show) {
                fm.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .hide(currentFragment)
                        .commit();
            } else {
                fm.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .show(currentFragment)
                        .commit();
            }
        }
    }

    public Menu getMenu() {
        return mMenu;
    }
}

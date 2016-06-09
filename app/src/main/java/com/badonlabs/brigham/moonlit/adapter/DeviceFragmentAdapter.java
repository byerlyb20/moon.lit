package com.badonlabs.brigham.moonlit.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.badonlabs.brigham.moonlit.fragment.DeviceFragment;

import java.util.List;

import io.particle.android.sdk.cloud.ParticleDevice;

public class DeviceFragmentAdapter extends FragmentStatePagerAdapter {

    private int mLength;

    public DeviceFragmentAdapter(FragmentManager fragmentManager, List<ParticleDevice> devices) {
        super(fragmentManager);
        mLength = devices.size();
    }

    @Override
    public Fragment getItem(int position) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return mLength;
    }

}

package com.bfr.sdkv2vision;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bfr.sdkv2vision.fragments.camera;
import com.bfr.sdkv2vision.fragments.detection;
import com.bfr.sdkv2vision.fragments.facerecog;
import com.bfr.sdkv2vision.fragments.motion;
import com.bfr.sdkv2vision.fragments.qrcode;
import com.bfr.sdkv2vision.fragments.tracking;

public class MyViewPager extends FragmentStateAdapter {
    public MyViewPager(@NonNull MainActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new camera();
            case 1:
                return new detection();
            case 2:
                return new motion();
            case 3:
                return new tracking();
            case 4:
                return new facerecog();
            case 5:
                return new qrcode();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}

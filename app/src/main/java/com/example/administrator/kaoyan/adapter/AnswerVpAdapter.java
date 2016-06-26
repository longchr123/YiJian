package com.example.administrator.kaoyan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.administrator.kaoyan.fragment.XiTiTestFragment;

import java.util.List;

/**
 * Created by ASUSK557 on 2016/2/17.
 */
public class AnswerVpAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;

    public AnswerVpAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }


    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}

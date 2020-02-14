package com.xishitong.supermember.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-10 16:48
 * description :会费明细页面viewpager适配器
 */
public class DetailOfMembershipAdapter extends FragmentPagerAdapter {
    private static final String[] TITLE_LIST = {"全部","会费充值","会费消费"};
    private List<Fragment> fragments;
    public DetailOfMembershipAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (TITLE_LIST == null || position >= TITLE_LIST.length) ? "" : TITLE_LIST[position];
    }
}

package cn.cystal.app.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-10 16:48
 * description :会费明细页面viewpager适配器
 */
public class DetailOfMembershipAdapter extends FragmentPagerAdapter {
    private static final String[] TITLE_LIST = {"全部", "会费充值", "会费消费"};
    private List<Fragment> fragments;

    public DetailOfMembershipAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @NotNull
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

//    @Override
//    public long getItemId(int position) {
//        return fragments.get(position).hashCode();
//    }
//
//    @Override
//    public int getItemPosition(@NonNull Object object) {
//        if (fragments.contains(object)) {
//            // 如果当前 item 未被 remove，则返回 item 的真实 position
//            return fragments.indexOf(object);
//        } else {
//            // 否则返回状态值 POSITION_NONE
//            return POSITION_NONE;
//        }
//    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (TITLE_LIST == null || position >= TITLE_LIST.length) ? "" : TITLE_LIST[position];
    }
}

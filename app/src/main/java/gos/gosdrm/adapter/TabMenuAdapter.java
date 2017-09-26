package gos.gosdrm.adapter;

/*
* 适配器
* */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TabMenuAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragmentList;

    //getChildFragmentMange

    public TabMenuAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

    }


    public void setFragmentList(ArrayList<Fragment> fragmentList){
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {

        return fragmentList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destroy" + position);

        super.destroyItem(container, position, object);
    }



}




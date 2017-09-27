package gos.gosdrm.data;

import android.support.v4.app.Fragment;

/**
 * Created by wuxy on 2017/9/27 0027.
 */

public class HomeMenuItem {
    private Fragment fragment;
    private int name;
    private int icon;

    public HomeMenuItem(Fragment fragment, int name) {
        this.fragment = fragment;
        this.name = name;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}

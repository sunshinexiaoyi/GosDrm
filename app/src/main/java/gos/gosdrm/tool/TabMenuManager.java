package gos.gosdrm.tool;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

public class TabMenuManager {
    private ArrayList<TabMenuItem> TabMenu = new ArrayList<>();

    public class TabMenuItem{

        public Class fragment;
        public Fragment instance = null;

        public TabMenuItem(Class fragment){
            this.fragment = fragment;
        }

    }

    /**
     * @param fragment  菜单fragment
     */
    public void addTabMenuItem(Class fragment) {

        TabMenu.add(new TabMenuItem(fragment));
    }

    public int getTabMenuNum() {
        return TabMenu.size();
    }


    public ArrayList<TabMenuItem> getTabMenu(){
        return TabMenu;
    }

}


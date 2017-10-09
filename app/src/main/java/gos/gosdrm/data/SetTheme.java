package gos.gosdrm.data;

/**
 * 设置主题
 * Created by wuxy on 2017/10/9 0009.
 */

public class SetTheme {
    public final static int GOSPELL = 0;    //高斯贝尔
    public final static int BLACk = 1;      //酷黑

    private int theme = GOSPELL; //默认主题为 高斯贝尔

    public SetTheme() {
    }

    public SetTheme(int theme) {
        this.theme = theme;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }
}

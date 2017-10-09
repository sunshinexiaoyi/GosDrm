package gos.gosdrm.data;

/**
 * 设置频道源
 * Created by wuxy on 2017/10/9 0009.
 */

public class SetSource {
    public final static int NETWORK  = 0;        //网络源
    public final static int LOCAL = 1;      //本地源

    private int source = NETWORK;   //默认网络源

    public SetSource() {
    }

    public SetSource(int source) {
        this.source = source;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }
}

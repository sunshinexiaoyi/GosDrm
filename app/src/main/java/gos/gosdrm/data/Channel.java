package gos.gosdrm.data;

/**
 * 频道类
 * Created by wuxy on 2017/9/25.
 */

public class Channel {
    private int channelId;
    private String channelName;
    private String icon;
    private String liveUrl;

    public Channel(){}

    public Channel(int channelId, String channelName) {
        this.channelId = channelId;
        this.channelName = channelName;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }
}

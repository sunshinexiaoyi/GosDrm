package gos.gosdrm.define;

/**
 * Created by wuxy on 2017/9/26 0026.
 */

public interface PlayerUrl {
     String channelRequestUrl = "http://192.168.1.84:8090/ottserver/IPLiveInfo/getAllChannel?clientId=boss00042&pageSize=20&pageNumber=1";

    //测试 mpd地址
     String[] mpdUrl = new String[]{
            "http://192.168.1.109:8091/vod/dpQWU5Hh/dpQWU5Hh.mpd" ,
            "http://192.168.1.109:8091/vod/ubUK7aMf/ubUK7aMf.mpd" ,
            "http://192.168.1.109:8091/vod/b2LRsrUr/b2LRsrUr.mpd" ,
            "http://192.168.1.109:8091/vod/vkYYMu8a/vkYYMu8a.mpd" ,
            "http://192.168.1.109:8091/vod/63sKaUW7/63sKaUW7.mpd" ,
            "http://192.168.1.109:8091/vod/euMPursb/euMPursb.mpd" ,
            "http://192.168.1.109:8091/vod/2YNt73Cf/2YNt73Cf.mpd" ,
            "http://192.168.1.109:8091/vod/Qrsd5dKp/Qrsd5dKp.mpd" ,
            "http://192.168.1.109:8091/vod/8fNeKVRi/8fNeKVRi.mpd" ,
            "http://192.168.1.109:8091/vod/ehXtPpcW/ehXtPpcW.mpd"
    } ;

    String[] hlsUrl = new String[]{
            "http://192.168.1.109:1028/static_hls/out/langyb_encrypt/program.m3u8"

    };

}

package gos.gosdrm.data;

/**
 * Created by QXTX-OBOOK on 2017/10/6.
 */

public class SettingItem {
    private int nameId;
    private int logoImgId;
    private Class<?> activity;

    public SettingItem(int nameId, int logoImgId, Class<?> activity) {
        this.nameId = nameId;
        this.logoImgId = logoImgId;
        this.activity = activity;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    public int getLogoImgId() {
        return logoImgId;
    }

    public void setLogoImgId(int logoImgId) {
        this.logoImgId = logoImgId;
    }

    public Class<?> getActivity() {
        return activity;
    }

    public void setActivity(Class<?> activity) {
        this.activity = activity;
    }
}

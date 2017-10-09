package gos.gosdrm.data;

/**
 * Created by QXTX-OBOOK on 2017/10/6.
 */

public class SettingItem {
    private int logoImgId;
    private String itemName;

    public SettingItem(int logoImgId, String itemName) {
        this.logoImgId = logoImgId;
        this.itemName = itemName;
    }

    public void setLogoImgId(int logoImgId) {
        this.logoImgId = logoImgId;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getLogoImgId() {
        return logoImgId;
    }
    public String getItemName() {
        return itemName;
    }
}

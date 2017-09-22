package gos.gosdrm.data;

import java.util.ArrayList;

/**
 * 从服务器返回的节目表
 * Created by wuxy on 2017/9/22.
 */

public class ProgramRet {
    private int total;  //总数
    private ArrayList<Program> rows;//节目列表

    public ProgramRet() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<Program> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Program> rows) {
        this.rows = rows;
    }
}

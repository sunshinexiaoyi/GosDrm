package gos.gosdrm.data;

/**
 * 服务器返回 -- 头部
 * Created by wuxy on 2017/9/25.
 */

public class ReturnHeader {
    private String message;
    private String status;
    public ReturnHeader(){}

    public ReturnHeader(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

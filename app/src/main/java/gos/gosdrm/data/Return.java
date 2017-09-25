package gos.gosdrm.data;

/**
 * Created by wuxy on 2017/9/25.
 */

public class Return <T>{
    private ReturnHeader header;
    private T body;

    public Return() {
    }

    public ReturnHeader getHeader() {
        return header;
    }

    public void setHeader(ReturnHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }


}

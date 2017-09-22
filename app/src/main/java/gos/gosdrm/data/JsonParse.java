package gos.gosdrm.data;

import com.alibaba.fastjson.JSON;

/**
 * Created by wuxy on 2017/9/22.
 */

public class JsonParse {
    public  static ProgramRet getProgramRet(String jsonStr){
        return JSON.parseObject(jsonStr,ProgramRet.class);
    }
}

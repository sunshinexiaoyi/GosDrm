package gos.gosdrm.tool;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by wuxy on 2017/9/25.
 */

public class BaseHibernateDao<T> {

    private Class<T> clazz;

    public BaseHibernateDao(){
        //当前对象的直接超类的 Type
        Type genericSuperclass = getClass().getGenericSuperclass();
        if(genericSuperclass instanceof ParameterizedType){
            //参数化类型
            ParameterizedType parameterizedType= (ParameterizedType) genericSuperclass;
            //返回表示此类型实际类型参数的 Type 对象的数组
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            this.clazz= (Class<T>)actualTypeArguments[0];
        }else{
            this.clazz= (Class<T>)genericSuperclass;
        }
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
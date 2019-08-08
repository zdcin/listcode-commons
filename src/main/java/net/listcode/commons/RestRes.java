package net.listcode.commons;

import lombok.Data;

import java.io.Serializable;

/**
 * Rest接口返回的包装对象
 * @param <T>
 */
@Data
public class RestRes<T> implements Serializable {
    private static final long serialVersionUID = -8524070495317253930L;
    private static final int SUCCESS_CODE = 0;

    /**返回码*/
    private int code;

    /**返回对象信息*/

    private T data;

    /**提示信息*/
    private String msg;

    /**异常栈信息*/
    private String trace;



    private static boolean isOpenTrace = false;
    /**
     * 全局设置，根据生产，测试等环境不同，决定是否返回trace信息
     * @param isOpenTrace
     */
    public static void init(boolean isOpenTrace) {
        RestRes.isOpenTrace = isOpenTrace;
    }


    private RestRes(int code,String msg,String trace,T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        if(RestRes.isOpenTrace){
            this.trace = trace;
        }
    }


    /**
     * 请求成功，无返回数据
     * @param <T>
     * @return
     */

    public static<T> RestRes<T> success(){
        return new RestRes(SUCCESS_CODE,null,null,null);
    }

    /**
     * 请求成功，有返回数据
     * @param <T>
     * @return
     */
    public static<T> RestRes<T> success(T data){
        return new RestRes(SUCCESS_CODE,"","",data);
    }

    /**
     * 请求失败
     * @param code
     * @param msg
     * @param <T>
     * @return
     */
    public static<T> RestRes<T> fail(int code, String msg){
        return new RestRes(code,msg,"",null);
    }

    /**
     * 请求失败
     * @param code
     * @param msg
     * @param e
     * @param <T>
     * @return
     */
    public static<T> RestRes<T> fail(int code, String msg, Exception e){
        return new RestRes(code,msg,toTrace(e),null);
    }

    /**
     * 请求失败
     * @param e
     * @param <T>
     * @return
     */
    public static<T> RestRes<T> exception(Exception e){
        return fail(-1, null, e);
    }


    private static String toTrace(Exception e) {
        if (!RestRes.isOpenTrace) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement s : trace) {
            result.append("\t    at ").append(s).append("\r\n");
        }
        return result.toString();
    }

}
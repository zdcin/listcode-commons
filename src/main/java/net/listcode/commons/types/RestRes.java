package net.listcode.commons.types;

import lombok.Data;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Rest接口返回的包装对象
 * @param <T>
 *
 *     TODO :可以根据常见的错误,指定几个错误方法,比如notfound, fobiden, serverError, input_error, 依赖系统错误等, 参考http 错误码,以及hutool
 */
@Data
public class RestRes<T> implements Serializable {
    private static final long serialVersionUID = -8524070495317253930L;
    private static final int SUCCESS_CODE = 0;
    private static final int UNDEFINED_ERROR_CODE = -1;

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


    private RestRes(int code, String msg, String trace, T data) {
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
        return new RestRes(SUCCESS_CODE, null, null, data);
    }

    /**
     * 请求失败
     * @param code
     * @param msg
     * @param <T>
     * @return
     */
    public static<T> RestRes<T> fail(int code, String msg){
        return new RestRes(code, msg, null, null);
    }

    /**
     * 请求失败
     * @param code
     * @param msg
     * @param throwable
     * @param <T>
     * @return
     */
    public static<T> RestRes<T> fail(int code, String msg, Throwable throwable){
        return new RestRes(code, msg, toTrace(throwable), null);
    }

    /**
     * 请求失败
     * @param code
     * @param msg
     * @param trace
     * @param <T>
     * @return
     */
    public static<T> RestRes<T> fail(int code, String msg, String trace){
        return new RestRes(code, msg, trace, null);
    }

    /**
     * 请求失败
     * @param throwable
     * @param <T>
     * @return
     */
    public static<T> RestRes<T> exception(Throwable throwable){
        return fail(UNDEFINED_ERROR_CODE, null, throwable);
    }


    private static String toTrace(Throwable throwable) {
        if (!RestRes.isOpenTrace || throwable == null) {
            return null;
        }
//        StringBuilder result = new StringBuilder();
//        result.append(e.getMessage()).append("\r\n");
//        StackTraceElement[] trace = e.getStackTrace();
//        for (StackTraceElement s : trace) {
//            result.append("\tat ").append(s).append("\r\n");
//        }
//        StringWriter sw = null;
        //PrintWriter pw = null;
        try (StringWriter stringWriter = new StringWriter()){
            try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
                throwable.printStackTrace(printWriter);
                printWriter.flush();
                stringWriter.flush();
                return stringWriter.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

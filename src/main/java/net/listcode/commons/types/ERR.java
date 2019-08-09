package net.listcode.commons.types;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author LeoZhang
 */
@Data
public class ERR implements Serializable {
    private static final long serialVersionUID = 5576233681915546398L;

    public ERR(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public ERR(int code, String msg, Throwable throwable) {
        this.code = code;
        this.msg = msg;
        this.throwable = throwable;
    }
    private int code;
    private String msg;
    private Throwable throwable;


    public <T> RestRes<T> toRestRes() {
        return RestRes.fail(this.getCode(), this.getMsg(), this.getThrowable());
    }

    public RuntimeException toException() {
        return new RuntimeException(String.format("Error[code, msg]=[%d, %s]", this.getCode(), this.getMsg()), this.getThrowable());
    }

//    public static ERR build(ERR_CODE ecode) {
//        return new ERR(ecode.getCode(), ecode.getMsg());
//    }
//
//    public static ERR build(ERR_CODE ecode, String appendMsg) {
//        return new ERR(ecode.getCode(), ecode.getMsg() + appendMsg);
//    }
//
//    public static ERR build(ERR_CODE ecode, String appendMsg, Throwable throwable) {
//        return new ERR(ecode.getCode(), ecode.getMsg() + appendMsg, throwable);
//    }
//
//    public static ERR build(ERR_CODE ecode, Throwable throwable) {
//        return new ERR(ecode.getCode(), ecode.getMsg(), throwable);
//    }

}

package net.listcode.commons.types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author LeoZhang
 */
public class Errors {

    private Errors(){}

    private static Map<Integer, ERR_CODE> errorCodeDict = new ConcurrentHashMap<>();

    private static  ERR_CODE createAndCheck(int code, String msg) {
        //code =0 不检查，msg 空不检查，只检查 code重复
        ERR_CODE e = new ERR_CODE(code, msg);
        if (errorCodeDict.containsKey(e.getCode())) {
            throw new IllegalStateException("Error code duplicated, code=" + code);
        } else {
            return e;
        }
    }

    private static ERR_CODE getFromDict(int code) {
        return errorCodeDict.get(code);
    }

    public static final ERR_CODE E1_NO_PERMISSION = createAndCheck(1, "无访问权限");
    public static final ERR_CODE E2_DATA_DUPLICATE = createAndCheck(2, "数据重复");

}

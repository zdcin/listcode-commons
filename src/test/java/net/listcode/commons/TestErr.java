package net.listcode.commons;

import net.listcode.commons.types.ERR;
import net.listcode.commons.types.Errors;
import net.listcode.commons.types.RestRes;

public class TestErr {
    public static void main(String[] args) {
        ERR myerr = Errors.E1_NO_PERMISSION.build("id=1, 类形是cover");
        RestRes restRes = myerr.toRestRes();
        Exception e = myerr.toException();
    }
}

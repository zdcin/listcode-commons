package net.listcode.commons;

import net.listcode.commons.types.ERR;
import net.listcode.commons.types.Errors;
import net.listcode.commons.types.RestRes;

public class TestErr {
    public static void main(String[] args) {
        ERR myerr = Errors.NO_PERMISSION.build();
        RestRes restRes = myerr.toRestRes();
        Exception e = myerr.toException();
    }
}

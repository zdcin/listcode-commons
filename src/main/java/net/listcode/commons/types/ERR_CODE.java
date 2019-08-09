package net.listcode.commons.types;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 *
 * @author LeoZhang
 */
@AllArgsConstructor
@Value
public class ERR_CODE {
    private int code;
    private String msg;

    public ERR build() {
        return new ERR(this.getCode(), this.getMsg());
    }

    public ERR build(String appendMsg) {
        return new ERR(this.getCode(), this.getMsg() + appendMsg);
    }

    public ERR build(String appendMsg, Throwable throwable) {
        return new ERR(this.getCode(), this.getMsg() + appendMsg, throwable);
    }

    public ERR build(Throwable throwable) {
        return new ERR(this.getCode(), this.getMsg(), throwable);
    }
}

package com.roc.his.api.exception;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HisException extends RuntimeException {
    private String msg;
    private int code = 500;

    public HisException(Exception e) {
        super(e);
        this.msg = "业务异常";
    }

    public HisException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public HisException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public HisException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public HisException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}

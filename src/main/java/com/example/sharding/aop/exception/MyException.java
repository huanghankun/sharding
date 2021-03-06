package com.example.sharding.aop.exception;

public class MyException extends RuntimeException {
    public MyException (String code,String msg) {
        super(msg);
        this.code = code;

    }

    private String code;
    private String msg;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}

package com.autotestgen.sample;

public class OrderResult {
    private boolean success;
    private String msg;

    private OrderResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public static OrderResult success() {
        return new OrderResult(true, "Success");
    }

    public static OrderResult fail(String msg) {
        return new OrderResult(false, msg);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getMessage() {
        return msg;
    }

    public String getStatus() {
        return success ? "success" : "fail";
    }
}

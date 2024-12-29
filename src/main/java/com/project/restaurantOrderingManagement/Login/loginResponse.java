package com.project.restaurantOrderingManagement.Login;

public class loginResponse {
    private String msg;
    private boolean success;
    private String redirectUrl;

    public loginResponse(String msg,boolean success, String redirectUrl) {
        this.msg = msg;
        this.success = success;
        this.redirectUrl = redirectUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

package com.reactnativesunmieid;

import com.sunmi.eidlibrary.EidConstants;

public enum ReadyState {

    READY(EidConstants.READ_CARD_READY,"准备完毕", "READY"),
    PENDING(EidConstants.READ_CARD_START,"刷卡中", "PENDING"),
    DONE(EidConstants.READ_CARD_SUCCESS,"读卡成功", "DONE"),
    FAILED(EidConstants.READ_CARD_FAILED,"读卡失败", "FAILED"),
    SUCCESS(1, "解析成功", "SUCCESS"),
    PARSE_FAILED(-1, "解析失败", "PARSE_FAILED");
    ReadyState(int code, String msg, String status){
      this.code = code;
      this.msg = msg;
      this.status = status;
    }
    final private int code;
    final private String msg;
    final private String status;
    public int getCode(){
      return this.code;
    }
    public String getMsg(){
      return this.msg;
    }
    public String getStatus() {
      return this.status;
    }
}

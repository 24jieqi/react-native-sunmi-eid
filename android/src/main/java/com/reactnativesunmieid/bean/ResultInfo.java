package com.reactnativesunmieid.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResultInfo implements Serializable {
  @SerializedName("base_info")
  public BaseInfo info;
  /**
   * 身份证指纹信息
   */
  public String dn;
  /**
   * 身份证头像
   */
  public String picture;
  /**
   * 应用网络身份标记，同一个身份有一个编码
   */
  public String appeidcode;
  /**
   * 业务错误信息
   */
  @SerializedName("sub_msg")
  public String msg = "";
  /**
   * 业务错误码
   */
  @SerializedName("sub_code")
  public int code;

  @Override
  public String toString() {
    return "ResultInfo{" +
      "baseInfo='" + (info == null ? "null" : info.toString()) + '\'' +
      ", dn='" + dn + '\'' +
      ", picture='" + picture + '\'' +
      ", appeidcode='" + appeidcode + '\'' +
      ", msg='" + msg + '\'' +
      ", code='" + code + '\'' +
      '}';
  }
}

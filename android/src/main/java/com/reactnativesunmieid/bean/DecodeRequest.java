package com.reactnativesunmieid.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DecodeRequest implements Serializable {
  @SerializedName("request_id")
  public String request_id;
  @SerializedName("encrypt_factor")
  public String encrypt_factor;

}

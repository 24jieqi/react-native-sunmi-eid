package com.reactnativesunmieid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.google.gson.Gson;
import com.reactnativesunmieid.bean.DecodeRequest;
import com.reactnativesunmieid.bean.Result;
import com.reactnativesunmieid.bean.ResultInfo;
import com.reactnativesunmieid.uitls.DesUtils;
import com.reactnativesunmieid.uitls.SignatureUtils;
import com.sunmi.eidlibrary.EidCall;
import com.sunmi.eidlibrary.EidConstants;
import com.sunmi.eidlibrary.EidSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@ReactModule(name = SunmiEidModule.NAME)
public class SunmiEidModule extends ReactContextBaseJavaModule {
    public static OkHttpClient client = new OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .protocols(Collections.singletonList(Protocol.HTTP_1_1))
      .build();
    private final ReactApplicationContext context;
    String appId;
    String appKey;
    Activity activity;
    public static final String NAME = "SunmiEid";
    public SunmiEidModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void init(ReadableMap config, Promise promise) {
      activity = getCurrentActivity();
      this.appId = config.getString("appId");
      this.appKey = config.getString("appKey");
      EidSDK.init(context, appId, (code, s) -> {
        if (code == EidConstants.EID_INIT_SUCCESS) {
          promise.resolve(null);
        } else {
          promise.reject(code + "", s);
        }
      });
    }
    @ReactMethod
    public void stopCheckCard() {
      EidSDK.stopCheckCard(activity);
    }
    @ReactMethod
    public void startCheckCard(Callback statusChangeCallback, Callback errorCallback) {
      EidSDK.startCheckCard(activity, (code, s) -> {
        switch (code) {
          case EidConstants.READ_CARD_READY:
            statusChangeCallback.invoke(ReadyState.READY.getStatus(), ReadyState.READY.getCode(), ReadyState.READY.getMsg());
            break;
          case EidConstants.READ_CARD_START:
            statusChangeCallback.invoke(ReadyState.PENDING.getStatus(), ReadyState.PENDING.getCode(), ReadyState.PENDING.getMsg());
            break;
          case EidConstants.READ_CARD_SUCCESS:
            EidSDK.stopCheckCard(activity);
            statusChangeCallback.invoke(ReadyState.DONE.getStatus(), ReadyState.DONE.getCode(), ReadyState.DONE.getMsg());
            // 云解析
            WritableMap result = requestOrigin(s);
            if (result != null) {
              statusChangeCallback.invoke(ReadyState.SUCCESS.getStatus(), ReadyState.SUCCESS.getCode(), ReadyState.SUCCESS.getMsg(), result);
            } else {
              errorCallback.invoke(ReadyState.PARSE_FAILED.getCode(), ReadyState.PARSE_FAILED.getMsg());
            }
            break;
          case EidConstants.READ_CARD_FAILED:
            statusChangeCallback.invoke(ReadyState.FAILED.getStatus(), ReadyState.FAILED.getCode(), ReadyState.FAILED.getMsg());
            break;
          default:
            errorCallback.invoke(code, s);
        }
      });
    }
    private static String getCharAndNumber() {
      Random random = new Random();
      StringBuilder valSb = new StringBuilder();
      String charStr = "0123456789abcdefghijklmnopqrstuvwxyz";
      int charLength = charStr.length();

      for (int i = 0; i < 8; i++) {
        int index = random.nextInt(charLength);
        valSb.append(charStr.charAt(index));
      }
      return valSb.toString();
    }
    private static String getNumber() {
      Random random = new Random();
      StringBuilder valSb = new StringBuilder();
      String charStr = "0123456789";
      int charLength = charStr.length();

      for (int i = 0; i < 6; i++) {
        int index = random.nextInt(charLength);
        valSb.append(charStr.charAt(index));
      }
    return valSb.toString();
  }
    private WritableMap requestOrigin(String reqId) {

        String json;
        Gson gson = new Gson();
        DecodeRequest reqBean = new DecodeRequest();
        reqBean.request_id = reqId;
        reqBean.encrypt_factor = getCharAndNumber();
        json = gson.toJson(reqBean);
        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        long timeStamp = System.currentTimeMillis()/1000;
        String nonce = getNumber();
        String tempSign = json + this.appId + timeStamp + nonce;
        String sign = SignatureUtils.generateHashWithHmac256(tempSign, this.appKey);
        Request request = new Request.Builder().url(Constant.OPEN_API_HOST + "/v2/eid/eid/idcard/decode")
          .addHeader("Sunmi-Timestamp", timeStamp+"")
          .addHeader("Sunmi-Sign", sign)
          .addHeader("Sunmi-Appid", this.appId)
          .addHeader("Sunmi-Nonce", nonce)
          .post(reqBody)
          .build();
        try{
          Response response = client.newCall(request).execute();
          Result res = new Result();
          JSONObject obj;
          try{
            obj = new JSONObject(Objects.requireNonNull(response.body()).toString());
            res.code = obj.getInt("code");
            if (obj.has("msg")) {
              res.msg = obj.getString("msg");
            }
            if (res.code == 1) {
              JSONObject dataJson = obj.getJSONObject("data");
              Result.Data data = new Result.Data();
              data.info = dataJson.getString("info");
              res.data = data;
            } else {
              return null;
            }
          } catch (JSONException e) {
            e.printStackTrace();
            return null;
          }
          byte[] tempData = Base64.decode(res.data.info.getBytes(), Base64.DEFAULT);
          String tempDataStr = null;
          try {
            tempDataStr = new String(DesUtils.decode(appKey.substring(0, 8), tempData, reqBean.encrypt_factor));
          } catch (Exception e) {
            e.printStackTrace();
            return null;
          }
          ResultInfo info = gson.fromJson(tempDataStr, ResultInfo.class);
          WritableMap result = Arguments.createMap();
          result.putString("dn", info.dn);
          result.putString("name", info.info.name);
          result.putString("sex", info.info.sex);
          result.putString("nation", info.info.nation);
          result.putString("birthDate", info.info.birthDate);
          result.putString("address", info.info.address);
          result.putString("idnum", info.info.idnum);
          result.putString("signingOrganization", info.info.signingOrganization);
          result.putString("beginTime", info.info.beginTime);
          result.putString("endTime", info.info.endTime);
          if (!TextUtils.isEmpty(info.picture)) {
            final Bitmap bit = EidSDK.parseCardPhoto(info.picture);
            result.putString("picture", bit.toString());
          }
          return result;
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        }
      }
}

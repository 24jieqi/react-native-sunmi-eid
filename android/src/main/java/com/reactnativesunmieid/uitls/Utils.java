package com.reactnativesunmieid.uitls;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Utils {
  static String KEY_AREA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  public static String createKey() {
    String key = "";
    Random random = new Random();
    for (int i = 0; i < 8; i++) {
      key += KEY_AREA.charAt(random.nextInt(KEY_AREA.length()));
    }
    return key;
  }

  public static String decode(String key, String data) {
    String value = null;
    try {
      byte[] datas = decode(key, Base64.decode(data, 0));
      value = new String(datas);
    } catch (Exception e) {
      value = "";
    }
    return value;
  }
  private static byte[] decode(String key, byte[] data) throws Exception {
    try {
      DESKeySpec dks = new DESKeySpec(key.getBytes());
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      // key的长度不能够小于8位字节
      Key secretKey = keyFactory.generateSecret(dks);
      Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
      IvParameterSpec iv = new IvParameterSpec(key.getBytes());
      cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
      return cipher.doFinal(data);
    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  public static String md5(String string) {
    byte[] hash;
    try {
      hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Huh, MD5 should be supported?", e);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Huh, UTF-8 should be supported?", e);
    }

    StringBuilder hex = new StringBuilder(hash.length * 2);
    for (byte b : hash) {
      if ((b & 0xFF) < 0x10)
        hex.append("0");
      hex.append(Integer.toHexString(b & 0xFF));
    }
    return hex.toString();
  }
  public static String bitmapToBase64(Bitmap bitmap) {

    String result = null;
    ByteArrayOutputStream baos = null;
    try {
      if (bitmap != null) {
        baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        baos.flush();
        baos.close();

        byte[] bitmapBytes = baos.toByteArray();
        result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (baos != null) {
          baos.flush();
          baos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
}

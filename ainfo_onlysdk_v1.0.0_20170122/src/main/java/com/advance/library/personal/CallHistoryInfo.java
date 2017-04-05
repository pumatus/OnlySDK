package com.advance.library.personal;

/**
 * Created by Hongd on 2017/3/27.
 */

public class CallHistoryInfo {

  private String date;
  private String number;
  private String type;
  private String cachedName;

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCachedName() {
    return cachedName;
  }

  public void setCachedName(String cachedName) {
    this.cachedName = cachedName;
  }

  public CallHistoryInfo() {

  }
}

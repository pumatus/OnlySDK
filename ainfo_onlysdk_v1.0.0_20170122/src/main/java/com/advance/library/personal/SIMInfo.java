package com.advance.library.personal;

/**
 * Created by Hongd on 2017/3/27.
 */

public class SIMInfo {

  private String model;
  private String deviceId;
  private String simNumber;
  private String IMSI;
  private String simCountry;

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getSimNumber() {
    return simNumber;
  }

  public void setSimNumber(String simNumber) {
    this.simNumber = simNumber;
  }

  public String getIMSI() {
    return IMSI;
  }

  public void setIMSI(String IMSI) {
    this.IMSI = IMSI;
  }

  public String getSimCountry() {
    return simCountry;
  }

  public void setSimCountry(String simCountry) {
    this.simCountry = simCountry;
  }

  public SIMInfo() {

  }
}

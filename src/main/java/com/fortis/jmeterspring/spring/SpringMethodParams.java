package com.fortis.jmeterspring.spring;

import com.fortis.jmeterspring.utils.JsonUtils;

public class SpringMethodParams {
  private String parameterType;
  private String parameterValue;

  public SpringMethodParams(String type,String vaule){
    this.parameterType = type;
    this.parameterValue = vaule;
  }

  public String getParameterType() {
    return parameterType;
  }

  public void setParameterType(String parameterType) {
    this.parameterType = parameterType;
  }

  public String getParameterValue() {
    return parameterValue;
  }

  public void setParameterValue(String parameterValue) {
    this.parameterValue = parameterValue;
  }
  public String toString() {
    return JsonUtils.toJson(this);
  }
}

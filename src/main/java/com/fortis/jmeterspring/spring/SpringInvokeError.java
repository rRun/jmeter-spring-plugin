package com.fortis.jmeterspring.spring;

public class SpringInvokeError extends RuntimeException{
  private SpringInvokeErrorType type;
  private String error;
  private Exception exception;

  public SpringInvokeError(SpringInvokeErrorType type,String error,Exception e){
    this.type = type;
    this.error = error;
    this.exception = e;
  }

  public SpringInvokeError(SpringInvokeErrorType type,String error){
    this.type = type;
    this.error = error;
  }

  public SpringInvokeErrorType getType() {
    return type;
  }

  public void setType(SpringInvokeErrorType type) {
    this.type = type;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }
}

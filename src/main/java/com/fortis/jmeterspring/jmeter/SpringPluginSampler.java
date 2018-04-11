package com.fortis.jmeterspring.jmeter;

import com.fortis.jmeterspring.spring.SpringInvokeError;
import com.fortis.jmeterspring.spring.SpringMethodParams;
import com.fortis.jmeterspring.spring.SpringBootStrap;
import com.fortis.jmeterspring.utils.ClassUtils;
import com.fortis.jmeterspring.utils.JsonUtils;
import org.apache.jmeter.samplers.*;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.testelement.property.ObjectProperty;
import org.apache.jmeter.testelement.property.StringProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SpringPluginSampler extends AbstractSampler implements Interruptible,ThreadListener{

  private static final long serialVersionUID = 12333222;
  private static final Logger logger = LoggerFactory.getLogger(SpringPluginSampler.class);

  /**
   * 保存测试数据的关键字
   */
  public static String SPRING_FILES_KEY = "SPRING_FILES";
  public static String SPRING_INTERFACE_KEY = "SPRING_INTERFACE";
  public static String SPRING_METHOD_KEY = "SPRING_METHOD";
  public static String SPRING_METHOD_PARAMS_KEY = "SPRING_METHOD_PARAMS";

  //需要保存的数据
  private List<String> spring_files;
  private String sinterface;
  private String smethod;
  private List<SpringMethodParams> smethodParams;

  public final static ThreadLocal<SpringBootStrap> RESOURCE_1 =
      new ThreadLocal<SpringBootStrap>();

  public SpringPluginSampler(){
    logger.info("创建sampler:"+ Thread.currentThread().getId());

  }

  @Override
  public SampleResult sample(Entry e) {
    logger.info("开始样本测试:"+assembleSampleData()+ Thread.currentThread().getId());

    SampleResult sampleResult = new SampleResult();
    sampleResult.setSampleLabel(getName());
    sampleResult.setSamplerData(assembleSampleData());
    sampleResult.setDataType(SampleResult.TEXT);
    sampleResult.sampleStart();

    boolean isSuccessful =false;
    try {
      SpringBootStrap bootStrap = createBootStrap();

      List<Object> parameterValuesList = new ArrayList<Object>();
      for (int j = 0; j < getSmethodParams().size(); j++) {
        ClassUtils.parseParameter(parameterValuesList, getSmethodParams().get(j));
      }
      Object object = bootStrap.invoke(getSinterface(),getSmethod(),parameterValuesList.toArray());
      sampleResult.setResponseData(JsonUtils.toJson(object),"utf-8");
      sampleResult.setResponseCodeOK();
      sampleResult.setResponseMessageOK();
      isSuccessful = true;
    } catch (SpringInvokeError error){
      logger.error(error.getError());
      sampleResult.setResponseData(error.getError(),"utf-8");
      isSuccessful = false;
      sampleResult.setResponseCode("-1");
    }catch (Exception exc) {
      logger.error(exc.getLocalizedMessage());
      sampleResult.setResponseData(exc.getLocalizedMessage(),"utf-8");
      isSuccessful = false;
      sampleResult.setResponseCode("-999");
    }finally {
      System.out.println("result"+isSuccessful);
      sampleResult.sampleEnd();
      sampleResult.setSuccessful(isSuccessful);
      System.out.println("return result"+sampleResult.toString());
      return sampleResult;
    }

  }

  /**
   * 组装sample请求数据
   * @return sample请求数据
   */
  public String assembleSampleData() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("files: ").append(getSpring_files()).append("\n");
    stringBuilder.append("Interface: ").append(getSinterface()).append("\n");
    stringBuilder.append("Method: ").append(getSmethod()).append("\n");
    stringBuilder.append("Method Parmas: ").append(getSmethodParams().toString());
    String result = stringBuilder.toString();

    logger.info("request :" + result);

    return stringBuilder.toString();
  }

  public List<String> getSpring_files() {
    return (List<String>) this.getProperty(SPRING_FILES_KEY).getObjectValue();
  }
  public void setSpring_files(List<String> spring_files) {
    this.setProperty(new ObjectProperty(SPRING_FILES_KEY, spring_files));
  }
  public String getSinterface() {
    return this.getPropertyAsString(SPRING_INTERFACE_KEY);
  }
  public void setSinterface(String sinterface) {
    this.setProperty(new StringProperty(SPRING_INTERFACE_KEY, sinterface));
  }
  public String getSmethod() {
    return this.getPropertyAsString(SPRING_METHOD_KEY);
  }
  public void setSmethod(String smethod) {
    this.setProperty(new StringProperty(SPRING_METHOD_KEY, smethod));
  }
  public List<SpringMethodParams> getSmethodParams() {
    return (List<SpringMethodParams>) this.getProperty(SPRING_METHOD_PARAMS_KEY).getObjectValue();
  }
  public void setSmethodParams(List<SpringMethodParams> smethodParams) {
    this.setProperty(new ObjectProperty(SPRING_METHOD_PARAMS_KEY, smethodParams));
  }

  private SpringBootStrap createBootStrap(){
    SpringBootStrap bootStrap = RESOURCE_1.get();
    if (bootStrap == null){
      bootStrap = new SpringBootStrap();
      bootStrap.setFiles( getSpring_files().toArray(new String[0]));
      bootStrap.bootStrap();
      bootStrap.start();
      RESOURCE_1.set(bootStrap);
    }
    return bootStrap;
  }
  private void stopBootStrap(){
    SpringBootStrap bootStrap = RESOURCE_1.get();
    if (bootStrap == null){
      bootStrap.stop();
      RESOURCE_1.remove();
    }
  }

  @Override
  public void threadStarted() {
    createBootStrap();
  }

  @Override
  public void threadFinished() {
    stopBootStrap();
  }
  @Override
  public boolean interrupt() {
    try{
      stopBootStrap();
    }catch (Exception e){
      ;
    }
    return RESOURCE_1.get()==null;
  }
}

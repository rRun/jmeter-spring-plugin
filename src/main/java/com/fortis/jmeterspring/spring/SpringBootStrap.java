package com.fortis.jmeterspring.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SpringBootStrap {
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private FileSystemXmlApplicationContext fileSystemXmlApplicationContext;
  private String[] files;

  /**
   * 开始启动spring容器
   * @return
   */
  public boolean bootStrap(){
    boolean createContext = false;
    try {
      if (fileSystemXmlApplicationContext == null){
        fileSystemXmlApplicationContext = new FileSystemXmlApplicationContext();
      }
      fileSystemXmlApplicationContext.setConfigLocations(files);
      fileSystemXmlApplicationContext.refresh();

      createContext = true;
    }catch (BeansException b){
      logger.error("创建springcontext失败",b);
    }
    return createContext;
  }

  public void start(){
    if (fileSystemXmlApplicationContext == null){
      bootStrap();
    }
    fileSystemXmlApplicationContext.start();
  }

  public void stop(){
    if (fileSystemXmlApplicationContext == null)
      return;
    fileSystemXmlApplicationContext.stop();
    fileSystemXmlApplicationContext.close();
  }

  public String[] getFiles() {
    return files;
  }

  public void setFiles(String[] files) {
    this.files = files;
  }


  public Object invoke(String beanName,String methodName, Object... parmas) throws InvocationTargetException, IllegalAccessException,SpringInvokeError {
    logger.info("invoke:"+beanName+methodName+parmas.toString());

    List<Class> paramsClasses = new ArrayList<Class>();
    for (Object param:parmas){
      paramsClasses.add(param.getClass());
    }
    Object demoService = fileSystemXmlApplicationContext.getBean(beanName); // 获取远程服务代理
    Method method = null;
    Method[] methods = demoService.getClass().getMethods();
    for (Method tempMethod:methods){
      Type[] paramTypes = tempMethod.getGenericParameterTypes();
      if (tempMethod.getName().equals(methodName) && paramTypes.length == parmas.length){
        //根据参数确定接口方法是否正确
        boolean isClaas = true;
        for (int i = 0 ;i<paramTypes.length;i++){
          Class type = (Class) paramTypes[i];
          Class type0 = parmas[i].getClass();
          if (type != type0){
            isClaas = false;
            break;
          }
        }
        if (!isClaas)
          throw new SpringInvokeError(SpringInvokeErrorType.NOTMATCHPARMAS,this.assbLog(beanName,methodName,"方法的参数未匹配成功"));

        //确定选定的接口方法
        if (parmas.length == paramTypes.length){
          method = tempMethod;
          break;
        }
      }
    }

    if (method != null){
      Object result = method.invoke(demoService,parmas);
      return result;
    }
    throw new SpringInvokeError(SpringInvokeErrorType.NOTMATCHMETHOD,this.assbLog(beanName,methodName,"未找到接口方法"));
  }

  /**
   * 组装日志
   * @param beanName bean名字
   * @param methodName 方法名
   * @param error 错误原因
   * @return 组装的日志
   */
  private String assbLog(String beanName,String methodName,String error){
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("error: ").append(error).append("\n");
    stringBuilder.append("beanName: ").append(beanName).append("\n");
    stringBuilder.append("methodName: ").append(methodName).append("\n");

    return stringBuilder.toString();
  }


  public static void main(String[] args){
    SpringBootStrap bootStrap = new SpringBootStrap();
    String[] files = new String[1];
    files[0]="file:///Users/hexiayu/Downloads/apache-jmeter-3.1/lib/其他的/spring-dubbo-provider.xml";
    bootStrap.setFiles(files);
    bootStrap.bootStrap();
    bootStrap.start();
    Object[] sss = new Object[1];
    sss[0]=new Long(10);
    try {
      Object object = bootStrap.invoke("drugService1","queryDrugDetailById",sss);
      System.out.println("object:"+object);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }catch (Exception e){
      e.printStackTrace();
    }

  }
}



package com.fortis.jmeterspring.utils;


import com.fortis.jmeterspring.spring.SpringMethodParams;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;


public class ClassUtils {

  private static final String TYPE_NAME_PREFIX = "class ";

  public static String getClassName(Type type) {
    if (type == null) {
      return "";
    }
    String className = type.toString();
    if (className.startsWith(TYPE_NAME_PREFIX)) {
      className = className.substring(TYPE_NAME_PREFIX.length());
    }
    return className;
  }

  @SuppressWarnings("rawtypes")
  public static String[] getMethodParamType(String interfaceName,
                                            String methodName) {
    try {
      // 创建类
      Class<?> class1 = Class.forName(interfaceName);
      // 获取所有的公共的方法
      Method[] methods = class1.getMethods();
      for (Method method : methods) {
        if (method.getName().equals(methodName)) {
          Class[] paramClassList = method.getParameterTypes();
          String[] paramTypeList = new String[paramClassList.length];
          int i = 0;
          for (Class className : paramClassList) {
            paramTypeList[i] = className.getName();
            i++;
          }
          return paramTypeList;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;

  }

  public static void parseParameter(List<Object> parameterValuesList, SpringMethodParams dubboMethodParams)
      throws ClassNotFoundException {
    String className = StringUtils.compoundVariable(dubboMethodParams.getParameterType());
    String value = StringUtils.compoundVariable(dubboMethodParams.getParameterValue());
    if (className.equals("int")) {
      parameterValuesList.add(Integer.parseInt(value));
    } else if (className.equals("double")) {
      parameterValuesList.add(Double.parseDouble(value));
    } else if (className.equals("short")) {
      parameterValuesList.add(Short.parseShort(value));
    } else if (className.equals("float")) {
      parameterValuesList.add(Float.parseFloat(value));
    } else if (className.equals("long")) {
      parameterValuesList.add(Long.parseLong(value));
    } else if (className.equals("byte")) {
      parameterValuesList.add(Byte.parseByte(value));
    } else if (className.equals("boolean")) {
      parameterValuesList.add(Boolean.parseBoolean(value));
    } else if (className.equals("char")) {
      parameterValuesList.add(value.charAt(0));
    } else if (className.equals("java.lang.String")
        || className.equals("String") || className.equals("string")) {
      parameterValuesList.add(String.valueOf(value));
    } else if (className.equals("java.lang.Integer")
        || className.equals("Integer") || className.equals("integer")) {
      parameterValuesList.add(Integer.valueOf(value));
    } else if (className.equals("java.lang.Double")
        || className.equals("Double")) {
      parameterValuesList.add(Double.valueOf(value));
    } else if (className.equals("java.lang.Short")
        || className.equals("Short")) {
      parameterValuesList.add(Short.valueOf(value));
    } else if (className.equals("java.lang.Long")
        || className.equals("Long")) {
      parameterValuesList.add(Long.valueOf(value));
    } else if (className.equals("java.lang.Float")
        || className.equals("Float")) {
      parameterValuesList.add(Float.valueOf(value));
    } else if (className.equals("java.lang.Byte")
        || className.equals("Byte")) {
      parameterValuesList.add(Byte.valueOf(value));
    } else if (className.equals("java.lang.Boolean")
        || className.equals("Boolean")) {
      parameterValuesList.add(Boolean.valueOf(value));
    } else {
      parameterValuesList.add(JsonUtils.formJson(value,
          Class.forName(className)));
    }
  }
}
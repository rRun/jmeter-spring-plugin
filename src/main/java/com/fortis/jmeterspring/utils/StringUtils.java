package com.fortis.jmeterspring.utils;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtils {
  private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

  public static String compoundVariable(String func){
    String result = null;
    if (func.startsWith("${")){
      CompoundVariable function = new CompoundVariable(func);
      try {
        result = function.execute().trim();
      } catch(Exception ex) {
        logger.error("Error calling function {}", func, ex);
      }
    }else
      result = func;
    return result;
  }

}

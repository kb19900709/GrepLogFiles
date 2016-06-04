package com.kb.util;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kb.constant.Constant;

public class PropertiesUtil {
	
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	private static Properties PROP = null;

	static {
		PROP = new Properties();
		try {
			//初始化 Properties 物件
			PROP.load(PropertiesUtil.class
					.getResourceAsStream(Constant.CONFIG_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.debug("***** PropertiesUtil All Properties *****");
		for(Entry<Object,Object> set : PROP.entrySet()){
			logger.debug(set.getKey()+" : "+set.getValue());
		}
		logger.debug("*****************************************");
	}

	public static String getProperty(String key) {
		return PROP.getProperty(key);
	}
	
	public static Properties getPropManager(){
		return PROP;
	}
}

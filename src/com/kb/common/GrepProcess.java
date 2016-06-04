package com.kb.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kb.constant.Constant;
import com.kb.util.PropertiesUtil;

abstract public class GrepProcess {
	
	private static Logger logger = LoggerFactory.getLogger(GrepProcess.class);
	
	@InjectConfig(key = Constant.GREP_LEVEL)
	protected String grepLevel;
	
	@InjectConfig(key = Constant.FILTER_TIME)
	protected String filterTime;
	
	@InjectConfig(key = Constant.OUTPUT_FILE_PATH)
	private String outputFilePath;
	
	@InjectConfig(key = Constant.OUTPUT_FILE_NAME)
	private String outputFileName;

	public void executeGrep() {
		//預先注入 config 變數
		prepareConfig();
		logger.info("prepare config params finish");
		
		List<String> grepResult = execute();
		logger.info("execute grep finish");
		
		for(String text : grepResult){
			text = processText(text);
			outputFile(text);
		}
		logger.info("out put file finish");
	}
	                                      
	private String processText(String text){		
		//取得需錯誤處理的字串
		Map<String,String> checkedMap = new HashMap<String,String>();
		Properties prop = PropertiesUtil.getPropManager();
		String key,value;
		for(Entry<Object,Object> set : prop.entrySet()){
			key = set.getKey().toString();
			if(key.indexOf(Constant.ENV_ERROR) != -1){
				value = set.getValue().toString();
				checkedMap.put(value, key);
			}
		}

		//字串處理
		Map<String,Integer> recordMap = new HashMap<String,Integer>();
		StringBuffer sbResult = new StringBuffer();
		StringBuffer sbBody = new StringBuffer();		
		String[] textLineArray = text.split(Constant.ENV_WARP);
		for(String line : textLineArray){			
			for(String errorKey : checkedMap.keySet()){
				if(line.indexOf(errorKey) != -1){
					sbBody.append(line + Constant.ENV_WARP);
					if(recordMap.get(errorKey) != null){
						recordMap.put(errorKey, recordMap.get(errorKey) + 1);
					}else{
						recordMap.put(errorKey, 1);
					}
				}
			}
		}
		
		for(Entry<String,Integer> set : recordMap.entrySet()){
			sbResult.append(set.getKey()+" >>> count:"+set.getValue());
			sbResult.append(Constant.ENV_WARP);
		}
		
		sbResult.append(Constant.ENV_WARP+Constant.ENV_WARP);
		sbResult.append(sbBody.toString());
		
		return sbResult.toString();
	}
	
	//輸出 log
	private void outputFile(String text){
		try {
			File newFile = new File(outputFilePath + File.separator + outputFileName);
			newFile.createNewFile();
			
			FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//準備環境資料
	private synchronized void prepareConfig() {
		Class<?> clazz;
		
		//注入子類別
		clazz = this.getClass();
		invokeFieldValue(clazz);
		
		//注入父類別
		clazz = clazz.getSuperclass();
		invokeFieldValue(clazz);
	}
	
	//注入 config by @InjectConfig
	private void invokeFieldValue(Class<?> clazz){
		Field[] declaredFields = clazz.getDeclaredFields();
		InjectConfig injectAnno;
		String key;
		for (Field field : declaredFields) {
			injectAnno = field.getAnnotation(InjectConfig.class);
			if (injectAnno != null) {
				key = injectAnno.key();
				if (StringUtils.isNotEmpty(key)
						&& field.getType().equals(String.class)) {
					field.setAccessible(true);
					try {
						field.set(this, PropertiesUtil.getProperty(key));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	abstract protected List<String> execute();
}

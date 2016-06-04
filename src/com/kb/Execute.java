package com.kb;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kb.common.GrepProcess;
import com.kb.constant.Constant;
import com.kb.service.LinuxGrepService;
import com.kb.util.PropertiesUtil;

/**
 * 
 * @author KB
 */
public class Execute {

	private static Logger logger = LoggerFactory.getLogger(Execute.class);

	public static void main(String[] args) {
		logger.info("Grep files process start at "
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));

		String executeType = PropertiesUtil.getProperty(Constant.ENV_TYPE);
		
		if (StringUtils.isEmpty(executeType)) {
			logger.error("missing executeType");
			return;
		}

		logger.info("Execute grep : " + executeType);

		//依據環境準備過濾服務 (linux/local)
		GrepProcess service;
		if (executeType.equals(Constant.ENV_LOCAL)) {

		} else if (executeType.equals(Constant.ENV_LINUX)) {
			
			service = new LinuxGrepService();
			service.executeGrep();
			
		} else {
			logger.error("executeType can not match");
			return;
		}

		logger.info("Grep files process end at "
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
	}

}

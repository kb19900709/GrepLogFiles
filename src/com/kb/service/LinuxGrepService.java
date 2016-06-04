package com.kb.service;

import static org.grep4j.core.Grep4j.constantExpression;
import static org.grep4j.core.Grep4j.grep;
import static org.grep4j.core.Grep4j.regularExpression;
import static org.grep4j.core.fluent.Dictionary.on;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.grep4j.core.model.Profile;
import org.grep4j.core.model.ProfileBuilder;
import org.grep4j.core.result.GrepResult;
import org.grep4j.core.result.GrepResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kb.common.GrepProcess;
import com.kb.common.InjectConfig;
import com.kb.constant.Constant;

public class LinuxGrepService extends GrepProcess {

	private static Logger logger = LoggerFactory.getLogger(LinuxGrepService.class);

	@InjectConfig(key = Constant.LINUX_IP)
	private String ip;

	@InjectConfig(key = Constant.LINUX_USER)
	private String user;

	@InjectConfig(key = Constant.LINUX_PASSWORD)
	private String password;

	@InjectConfig(key = Constant.LINUX_FILE_PATH_AND_NAME)
	private String filePathAndName;

	@Override
	protected List<String> execute() {

		logger.info(
				Constant.ENV_WARP 
				+ "*** execute params ***" + Constant.ENV_WARP 
				+ "ip >>> " + ip + Constant.ENV_WARP
				+ "filePathAndName >>> " + filePathAndName + Constant.ENV_WARP
				+ "grepLevel >>> " + grepLevel + Constant.ENV_WARP
				+ "filterTime >>> "+ filterTime + Constant.ENV_WARP
				+ "**********************");

		//取得遠端連線資訊 on linux
		Profile profile = ProfileBuilder.newBuilder()
				.name(filePathAndName)
				.filePath(filePathAndName)
				.onRemotehost(ip)
				.credentials(user, password)
				.build();

		//先過濾 log 等級
		GrepResults grepResultList = grep(constantExpression(grepLevel),on(profile));
		
		//如果過濾時間不是空值則擺時間過濾
		if(StringUtils.isNotEmpty(filterTime)){
			grepResultList = grepResultList.filterBy(regularExpression(filterTime));
		}
		
		//準備回傳資料
		List<String> resultList = null;
		if(grepResultList.size()>0){
			resultList = new ArrayList<String>();
			for(GrepResult grepResult : grepResultList){
				resultList.add(grepResult.getText());
			}
		}
		
		return resultList;
	}
}

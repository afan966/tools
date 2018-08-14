package com.afan.tool.log;

import java.io.File;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * 手动加载logback.xml
 * @author cf
 *
 */
public class LogBackConfigLoader {
	
	public static void init(){
		try {
			load(search("logback.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void init(String file){
		try {
			load(new File(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void load(File externalConfigFile) throws IOException, JoranException {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		if (externalConfigFile==null || !externalConfigFile.exists()) {
			throw new IOException("Logback External Config File Parameter does not reference a file that exists");
		} else {
			if (!externalConfigFile.isFile()) {
				throw new IOException("Logback External Config File Parameter exists, but does not reference a file");
			} else {
				if (!externalConfigFile.canRead()) {
					throw new IOException("Logback External Config File exists and is a file, but cannot be read.");
				} else {
					JoranConfigurator configurator = new JoranConfigurator();
					configurator.setContext(lc);
					lc.reset();
					configurator.doConfigure(externalConfigFile.getPath());
					StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @param fileName
	 * @param searchReturn找到就返回
	 * @return
	 */
	public static File search(String fileName){
		try{
			String rootPath = LogBackConfigLoader.class.getProtectionDomain().getCodeSource().getLocation().getFile();
			rootPath = java.net.URLDecoder.decode(rootPath, "UTF-8");
			File rootFile = new File(rootPath);
			if(rootFile.isFile()){
				rootPath = rootFile.getParent();
				//引用的方式找到的是lib下的jar包
				rootFile =  new File(rootPath);
				if("lib".equals(rootFile.getName())){
					rootPath = rootFile.getParent();
				}
			}
			return findFile(rootPath, fileName);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static File findFile(String currentDir, String fileName) {
		File dir = new File(currentDir);
		if (!dir.exists() || !dir.isDirectory()) {
			return null;
		}
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				findFile(file.getAbsolutePath(), fileName);
			} else {
				if (file.getAbsolutePath().endsWith(fileName)) {
					return file;
				}
			}
		}
		return null;
	}
}

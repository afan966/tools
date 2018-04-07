package com.afan.tool.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 文件格式转换
 * @author cf
 *
 */
public class ConvertCharset {
	
	public static void main(String[] args) {
		ConvertCharset tool = new ConvertCharset();
		String inDir = "C:\\Workspaces\\Projects\\afan-rpc\\src";
		String outDir = "E:\\afan-rpc-utf8\\src";
		String fromCharset = "GBK";
		String toCharset = "UTF-8";
		tool.convert(inDir, outDir, fromCharset, toCharset);
	}

	/**
	 * 指定文件目录字符集转换
	 * @param inDir输入目录
	 * @param outDir输出目录
	 * @param fromCharset输入目录字符集
	 * @param toCharset输出目录字符集
	 */
	public void convert(String inDir, String outDir, String fromCharset, String toCharset) {
		File file = new File(inDir);
		readFiles(file, fromCharset, toCharset, inDir, outDir);
	}
	
	public void readFiles(File file, String fromCharset, String toCharset, String inDir, String outDir){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for (File f : files) {
				readFiles(f, fromCharset, toCharset, inDir, outDir);
			}
		}else if(file.isFile()){
			String content = readFile(file, fromCharset);
			String newFile = file.getPath().replace(inDir, outDir);
			writeFile(new File(newFile), content, toCharset);
		}
	}

	public String readFile(File file, String charset) {
		String fileContent = "";
		try {
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), charset);
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					fileContent += line+"\n";
				}
				read.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容操作出错");
			e.printStackTrace();
		}
		return fileContent;
	}

	public void writeFile(File file, String content, String charset) {
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), charset);
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			System.out.println("写文件内容操作出错");
			e.printStackTrace();
		}
	}

}

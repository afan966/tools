package com.afan.tool.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 文件读取，不支持多线程
 * @author cf
 *
 */
public class FileUtil {
	
	private File file = null;
	private String charset = "UTF-8";
	private FileInputStream fileInputStream = null;
	private InputStreamReader inputStreamReader = null;
	private BufferedReader readLine = null;
	
	private String temp = null; 
	private int lineNumber = 0;
	private boolean next = false;
	
	public FileUtil() {
	}
	
	public FileUtil(String path) {
		this.file = new File(path);
		init();
	}
	
	public FileUtil(File file) {
		this.file = file;
		init();
	}
	
	public FileUtil(String path, String charset) {
		this.file = new File(path);
		this.charset = charset;
		init();
	}
	
	public FileUtil(File file, String charset) {
		this.file = file;
		this.charset = charset;
		init();
	}
	
	private void init() {
		check();
		try{
			this.fileInputStream = new FileInputStream(this.file);
			this.inputStreamReader = new InputStreamReader(this.fileInputStream, this.charset);
			this.readLine = new BufferedReader(this.inputStreamReader);  
			this.next = true;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public File getFile() {
		return this.file;
	}
	
	public String readAllFile() throws IOException{
		StringBuffer content = new StringBuffer();
		while(this.next){
			String temp = readLine();
			if(temp!=null){
				content.append(temp);
			}
		}
		return content.toString();
	}
	
	/**
	 * @return
	 * @throws IOException 
	 */
	public String readLine() throws IOException{
		this.temp = readLine.readLine();
		this.next = (this.temp!=null);
		this.lineNumber++;
		return temp;
	}
	
	public boolean hasNext(){
		return this.next;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}

	public void close() throws IOException{
		if(fileInputStream == null)
			fileInputStream.close();
		if(inputStreamReader == null)
			inputStreamReader.close();
		if(readLine == null)
			readLine.close();
	}
	
	public void appendFile(String content){
		check();
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), charset))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void appendLine(String content){
		check();
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), charset))) {
            writer.write(content);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void writeFile(String content){
		check();
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void check(){
		if(!this.file.exists()){
			try {
				if(!this.file.getParentFile().exists()){
					this.file.getParentFile().mkdirs();
				}
				this.file.createNewFile();
			} catch (IOException e) {
			}
		}
	}
	
	public byte[] readByte(){
        try {
            FileInputStream in =new FileInputStream(this.file);
            byte[] data=new byte[in.available()];
            in.read(data);
            in.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeByte(byte[] data){
        try {
            FileOutputStream outputStream  =new FileOutputStream(this.file);
            outputStream.write(data);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

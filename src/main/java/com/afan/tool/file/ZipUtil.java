package com.afan.tool.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 压缩文件夹
 * @author afan
 *
 */
public class ZipUtil {
	
	/**
	 * 压缩指定的目录，指定字符
	 * @param srcDir源文件目录
	 * @param desFile输出目录
	 * @param charset
	 */
	public static void createZip(String srcDir, String desFile, String charset) {
		ZipOutputStream zout = null;
		try {
			FileOutputStream out = new FileOutputStream(new File(desFile));
			zout = new ZipOutputStream(out);
			zout.setEncoding(charset);
			File sourceFile = new File(srcDir);
			if(!sourceFile.exists()){
				sourceFile.mkdirs();
			}
			compress(sourceFile, zout, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void compress(File sourceFile, ZipOutputStream zout, String name) throws Exception {
		byte[] buf = new byte[512];
		if (sourceFile.isFile()) {
			ZipEntry zipEntry = new ZipEntry(name);
			zipEntry.setUnixMode(644);
			zout.putNextEntry(zipEntry);
			int len;
			FileInputStream in = new FileInputStream(sourceFile);
			while ((len = in.read(buf)) != -1) {
				zout.write(buf, 0, len);
			}
			zout.closeEntry();
			in.close();
		} else {
			File[] listFiles = sourceFile.listFiles();
			if (listFiles == null || listFiles.length == 0 && name!=null) {
				// 空文件夹
				ZipEntry zipEntry = new ZipEntry(name + File.separator);
				zipEntry.setUnixMode(755);
				zout.putNextEntry(zipEntry);
				zout.closeEntry();
			} else {
				for (File file : listFiles) {
					if(name == null){
						compress(file, zout, file.getName());
					}else{
						compress(file, zout, name + File.separator + file.getName());
					}
				}
			}
		}
	}
}

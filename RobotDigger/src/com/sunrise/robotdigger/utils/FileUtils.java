package com.sunrise.robotdigger.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FileUtils {
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath);
			File myFilePath = new java.io.File(folderPath);
			myFilePath.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}

	 //在sd卡的路径下创建文件
	 public File createSDFile(String filename) throws IOException{
	  File file=new File(filename);
	  file.createNewFile();
	  return file;
	 }
	 
	 //在sd卡的路径下创建目录
	 public File createSDDir(String dirname) {
	  File dir=new File(dirname);
	  dir.mkdir();
	  return dir;
	 }
	 
	 //判断文件是否已经存在
	 public boolean isFileExist(String filename){
	  File file=new File(filename);
	  return file.exists();
	 }
	 
	 //将一个InputStream里的数据写进SD卡
	 public File inputSD(String path,String fileName,InputStream inputstream){
	  File file=null;
	  OutputStream outputstream=null;
	  try {
	   createSDDir(path);
	   file=createSDFile(path+fileName);
	   outputstream=new FileOutputStream(file);
	   byte[] buffer=new byte[4*1024];
	   while(inputstream.read(buffer)!=-1){
	    outputstream.write(buffer);
	   }
	   outputstream.flush();
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  } finally{
	   
	   try {
	    outputstream.close();
	   } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	   }
	   
	  }
	  return file;
	 }
}

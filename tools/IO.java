package tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

public class IO {
	
	public static String read(String fpath){
		StringBuffer fileContent = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fpath),"UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				fileContent.append(line.trim());
				fileContent.append("\n");
				}
		} catch (Exception e) {
			e.printStackTrace();
			}
		finally{
			if(br!=null)
				try {
						br.close();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		return fileContent.toString();
	}
	
	public static void write(String fpath,String fileContent) {
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(fpath), "UTF-8");
			osw.write(fileContent);
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
			}
		finally{
			if(osw!=null)
				try {
					osw.close();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
		}
	}
 
	public static void append(String fpath,String fileContent) {
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(fpath,true), "UTF-8");
			osw.write(fileContent);
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
			}
		finally{
			if(osw!=null)
				try {
					osw.close();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
		}
	}
	
	public static int [] getIndexList(String fpath){
		String [] indexStrs = IO.read(fpath).split("\n");
		int [] indexList = new int [indexStrs.length];
		for (int i=0;i<indexStrs.length;i++){
			indexList[i]=Integer.parseInt(indexStrs[i])+1;
		}
		return indexList;
	}
	
	
//	public static void makeDir(String dirPath) {
//		File file = new File(dirPath);
//		if  (!file .exists() && !file.isDirectory())
//			file.mkdir();
//    }
//	
//	public static void clearDir(String dirPath){
//		File dir = new File(dirPath);
//		if (dir.isDirectory()) {
//            String[] children = dir.list();
//            for (int i=0; i<children.length; i++) {
//                File child = new File(dir, children[i]);
//                child.delete();
//            }
//        }
//	}
	
	public static void main(String [] args){
//		IO.append("data\\1.txt","123");
//		IO.read("1.txt");
//		String a="123456,";
//		System.out.println(a.subSequence(0, a.length()-1));
//		Date d= new Date();
//		System.out.println(d.getTime());
		//test
//		Double[] d= new Double[2];
//		System.out.println(d[0]+"\t"+d[1]); 
//		String path = IO.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//	    System.out.println(path);
//	    System.out.println(path.substring(path.lastIndexOf("/")+1,path.lastIndexOf(".")));

		ArrayList<Integer> a=new ArrayList<Integer>();
		a.add(3);a.add(1);
		Collections.sort(a);
		System.out.println(a.get(0)+"\t"+a.get(1));
	}
}

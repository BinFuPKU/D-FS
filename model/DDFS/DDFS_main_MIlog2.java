package model.DDFS;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ml.MLUtil;
import tools.DecimalFormatUtil;
import tools.Discretize;
import tools.IO;
import tools.IOUtil;
import tools.MeasureUtil;
import tools.PreProcessUtil;
import tools.StatisticsUtil;
import weka.core.Instances;

public class DDFS_main_MIlog2 {

	private static double alpha;
	
	public static double [] deal(String trainPath, String testPath, String dataName, int fold){
		Instances train = IOUtil.instancesFromARFF(trainPath);
		Instances test = IOUtil.instancesFromARFF(testPath);
		{
			train.setClassIndex(train.numAttributes()-1);
			test.setClassIndex(test.numAttributes()-1);
		}
		
		String [] labels=new String[train.numInstances()];
		String [] targets=new String[train.numInstances()];
		{
			for (int k=0;k<labels.length;k++){
				labels[k]=(String)train.instance(k).stringValue(train.numAttributes()-1);
				targets[k]="";
			}
		}
		
		Map<Integer,ArrayList<Double>> cutpoints=PreProcessUtil.getCutPoints(train);
		Map<Integer,ArrayList<Double>> choose= new HashMap<Integer,ArrayList<Double>>();
		
		double ceilMI = 0;
		{
			String [] targets_temp=Discretize.dis_label(train, targets, cutpoints);
			ceilMI = MeasureUtil.muInfor_log(targets_temp, labels);
			System.out.println(ceilMI);
		}

		// all
		// #1#time #1#feature  #1#intervals  #6#ACC  #6#AUC  #6#FMeasure #6#Time 
		double [] rss =new double[3+6*4];
		
		// discretization
		{
			Date t0 = new Date();
			double mi_pre=0;
			while (true){
				double mi_max=0;
				int col_max=-1;
				double cp_max = Double.NaN;
				String [] targets_max=null;
				
				for(int a: cutpoints.keySet()){
					ArrayList<Double> vals = cutpoints.get(a);
					// col: vals
					for(double val:vals){
						String [] targets_temp=Discretize.dis_label(train, targets, a, val);
						
						double mi_temp=MeasureUtil.muInfor_log(targets_temp, labels);
						
						if (mi_max < mi_temp){
							mi_max = mi_temp;
							col_max = a;
							cp_max = val;
							targets_max = targets_temp;
						}// if
					}//for val
				}// for a
				
				// check whether exit
				if ((mi_pre!=0 && (mi_max - mi_pre)/mi_pre<=alpha) || (col_max==-1 || Double.isNaN(cp_max))){
					System.out.println(mi_pre);
					break;
				}
				
				// add and remove
				{
					// add
					{
						ArrayList<Double> tempArray = choose.getOrDefault(col_max, new ArrayList<Double>());
						
						tempArray.add(cp_max);
						choose.put(col_max, tempArray);
					}
					// remove
					{
						ArrayList<Double> tempArray = cutpoints.get(col_max);
						tempArray.remove(cp_max);
						if (tempArray.size()==0)
							cutpoints.remove(col_max);
						else
							cutpoints.put(col_max, tempArray);
					}
				}
				
				// 
				mi_pre = mi_max;
				targets = targets_max;
				System.gc();
			}//while (true)
			Date t1 = new Date();
			
			rss[0] = DecimalFormatUtil.df((t1.getTime()-t0.getTime())/1000.0, 4);
			System.gc();
		}
		
		//1.statistic
		ArrayList<Integer> left = null;
		{
			left = new ArrayList<Integer> ();
			int cutpoints_num = 0;
			for(int a: choose.keySet()){
				left.add(a);
				cutpoints_num+=choose.get(a).size();
			}
			rss[1] = (double)left.size();
			rss[2] = (double)cutpoints_num;
		}
		
		// 2. ml
		{
			// Discretize
			Instances train_new=Discretize.dis_data(train, choose);
			Instances test_new=Discretize.dis_data(test, choose);
			// filter
			train_new=PreProcessUtil.filterLeftClass(train_new, left);
			test_new=PreProcessUtil.filterLeftClass(test_new, left);
			
			// 6*4
			double [][] rs = MLUtil.deal(train_new, test_new);
			for(int k2=0;k2<4;k2++)
				for(int k1=0;k1<6;k1++)
					rss[3+(k2*6)+k1] = rs[k1][k2];
		}
		
		return  rss;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		final String IN_DIR="D:/Study/DDFS/数据/arff/orig-uci-14";
		final int FOLD = 10;
		alpha = 0;
		final String head="#dataset\t#Discretize_Time\t#Feature\t#Cut-points"
				+ "\t#IBK-ACC\t#NB-ACC\t#J48-ACC\t#LogReg-ACC\t#SMO-ACC\t#MLP-ACC"
				+ "\t#IBK-AUC\t#NB-AUC\t#J48-AUC\t#LogReg-AUC\t#SMO-AUC\t#MLP-AUC"
				+ "\t#IBK-FMeasure\t#NB-FMeasure\t#J48-FMeasure\t#LogReg-FMeasure\t#SMO-FMeasure\t#MLP-FMeasure"
				+ "\t#IBK-Time\t#NB-Time\t#J48-Time\t#LogReg-Time\t#SMO-Time\t#MLP-Time";

		final String pre_path = IN_DIR+"/ddfs-pre(log-"+alpha+").txt";
		final String sstd_path = IN_DIR+"/ddfs-sstd(log-"+alpha+").txt";
		
		IO.append(pre_path, head+"\r\n");
		IO.append(sstd_path, head+"\r\n");
		
		for (File subdir:new File(IN_DIR).listFiles()){
			if(subdir.isFile())
				continue;
			System.out.println("\n"+subdir.getName());
			
			if(!subdir.getName().equalsIgnoreCase("magic04")){
				continue;
			}
			
			double [][] rss_stat = new double[FOLD][3+6*4];
			for (int i=1;i<=FOLD;i++){
				
				System.out.println("\n\t"+i);
				String prefix=subdir.getName().split("fold")[0];
				
				String trainpath=subdir.getAbsolutePath()+"/"+prefix+"-"+FOLD+"-"+i+"tra.arff";
				String testpath=subdir.getAbsolutePath()+"/"+prefix+"-"+FOLD+"-"+i+"tst.arff";
				
				rss_stat[i-1] = deal(trainpath, testpath, subdir.getName(), i);
				
			}//for

			// write
			{
				double [] pre = new double [3+6*4];  //Pre
				double [] sstd = new double [3+6*4]; //StandardDiviation
				
				for(int col=0;col<(3+6*4);col++){
					double [] temp = new double [10];
					for(int row=0;row<FOLD;row++)
						temp[row] = rss_stat[row][col];
					pre[col] = DecimalFormatUtil.df(StatisticsUtil.getAverage(temp), 4);
					sstd[col] = DecimalFormatUtil.df(StatisticsUtil.getStandardDiviation(temp), 5);
				}
				
				String preTxt=subdir.getName()+":\t", sstdTxt=subdir.getName()+":\t";
				for(int j=0;j<(3+6*4);j++){
					preTxt += pre[j]+"\t";
					sstdTxt += sstd[j]+"\t";
				}
				{
					preTxt = preTxt.trim()+"\r\n";
					sstdTxt = sstdTxt.trim()+"\r\n";
				}
				IO.append(pre_path, preTxt);
				IO.append(sstd_path, sstdTxt);
				
			}
			
		}//for file
	}
}

package ml;

import weka.core.Instances;

public class MLUtil {
	
	//dis deal  [7][3]
	public static double [][] deal(Instances train,Instances test){
		// ibk  nb j48 logistic rf smo mlp
		double [][] rs = new double [6][4];
		System.out.print("\t\tIBK");
		rs[0]=IBK_.run(train,test);
		System.out.print("\tNB");
		rs[1]=NB_.run(train,test);
		System.out.print("\tJ48");
		rs[2]=J48_.run(train,test);
		System.out.print("\tLogReg");
		rs[3]=LogReg_.run(train,test);
		System.out.print("\tSMO");
		rs[4]=SMO_.run(train,test);
		System.out.print("\tMLP");
		rs[5]=MLP_.run(train,test);
		
		System.gc();
		
		return rs;
	}
}

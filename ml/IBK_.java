package ml;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class IBK_ {
	private static int knn=3;
	private static IBk slg = new IBk(knn);
	// IBK_
	public static double [] run(Instances train, Instances test) {
		Date start = new Date();
		Evaluation eval = null;
		try {
			slg.buildClassifier(train);
			eval = new Evaluation(train);
			eval.evaluateModel(slg, test);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date end = new Date();
		
		double [] rs=new double[4];
		rs[0]=1-eval.errorRate();
		rs[1]=eval.weightedAreaUnderROC();
		rs[2]=eval.weightedFMeasure();
		rs[3]=(end.getTime()-start.getTime())/1000;
		System.gc();
		return rs;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String indirpath="I:\\TempFiles\\10_";
		
		ArffLoader loader = new ArffLoader();   
		
		for (File subdir:new File(indirpath).listFiles()){
			for (int i=1;i<11;i++){
				String trainpath=subdir.getAbsolutePath()+"\\"+i+"_train.arff";
				String testpath=subdir.getAbsolutePath()+"\\"+i+"_test.arff";
				try {
					loader.setFile(new File(trainpath));

			        Instances train = loader.getDataSet();
			        train.setClassIndex(train.numAttributes()-1);
			        
			        loader.setFile(new File(testpath));
			        Instances test = loader.getDataSet();
			        test.setClassIndex(test.numAttributes()-1);
			        
			        double [] rs=IBK_.run(train,test);
			        
			        System.out.println(i+":\t"+rs[0]+"\t"+rs[1]+"\t"+rs[2]);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}

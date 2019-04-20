package ml;

import java.util.Date;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

public class LogReg_ {
	private static Logistic lg = new Logistic();

	// J48_
	public static double [] run(Instances train, Instances test) {
		Date start = new Date();
		Evaluation eval = null;
		try {
			lg.buildClassifier(train);
			eval = new Evaluation(train);
			eval.evaluateModel(lg, test);
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
}

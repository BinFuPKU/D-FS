package ml;

import java.util.Date;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class J48_ {
	private static J48 j48 = new J48();

	// J48_
	public static double [] run(Instances train, Instances test) {
		Date start = new Date();
		Evaluation eval = null;
		try {
			j48.buildClassifier(train);
			eval = new Evaluation(train);
			eval.evaluateModel(j48, test);
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

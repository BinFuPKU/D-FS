package ml;

import java.util.Date;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

public class NB_ {
	private static NaiveBayes nb = new NaiveBayes();
	
	// NaiveBayes
		public static double [] run(Instances train, Instances test) {
			Date start = new Date();
			Evaluation eval = null;
			try {
				nb.buildClassifier(train);
				eval = new Evaluation(train);
				eval.evaluateModel(nb, test);
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

package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Standardize;

public class PreProcessUtil {

	/** Filter the predictive attributes but left the class attribute.*/
	public static Instances filterLeftClass(Instances its, ArrayList<Integer> left){//all include the classindex(col)
		Remove rm = new Remove();
		int all=its.numAttributes();
		int removeLen=all-(left.size()+1);
		if (removeLen==0){
			return its;
		}
		int [] removelist= new int [removeLen];
		
		int [] all_= new int[all-1];
		for (int i=0;i<all-1;i++)//all-1
			all_[i]=i;
		for(int i:left){//left
			all_[i]=-1;
		}
		int j=0;
		for (int i=0;i<all-1;i++){//(all-1)-left=remove
			if (all_[i]>0)
				removelist[j++]=all_[i];
		}
		
		rm.setAttributeIndicesArray(removelist);
		Instances newIts=null;
		try {
			rm.setInputFormat(its);
			newIts = Filter.useFilter(its, rm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newIts;
	}
	/** Filter the predictive attributes and remove the class attribute.*/
	public static Instances filterNotLeftClass(Instances its, ArrayList<Integer> left){//all include the classindex(col)
		Remove rm = new Remove();
		int all=its.numAttributes();
		int removeLen=all-left.size();
		if (removeLen==0){
			return its;
		}
		int [] removelist= new int [removeLen];
		
		int [] all_= new int[all];
		for (int i=0;i<all;i++)//all-1
			all_[i]=i;
		for(int i:left){//left
			all_[i]=-1;
		}
		int j=0;
		for (int i=0;i<all;i++){//(all-1)-left=remove
			if (all_[i]>0)
				removelist[j++]=all_[i];
		}
		
		rm.setAttributeIndicesArray(removelist);
		Instances newIts=null;
		try {
			rm.setInputFormat(its);
			newIts = Filter.useFilter(its, rm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//			System.out.println("\t\tna:"+newIts.numAttributes());
		return newIts;
	}
	/** Nominalize the numeric predictive attributes.*/
	public static Instances Numeric2Nominal(Instances its, ArrayList<Integer> cols){
		
		int[] cols_=new int[cols.size()];
		for (int i=0;i<cols.size();i++)
			cols_[i]=cols.get(i);
		
		NumericToNominal num2nom = new NumericToNominal();
		num2nom.setAttributeIndicesArray(cols_);
		
		Instances newIts=null;
		try {
			num2nom.setInputFormat(its);
			newIts = Filter.useFilter(its, num2nom);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newIts;
	}
	
	/** Normalize the predictive numeric attributes into [0,1]. */
	public static Instances [] normalize_(Instances train, Instances test){
		Normalize n=new Normalize();
		try {
			n.setInputFormat(train);
			train=n.useFilter(train, n);
			test=n.useFilter(test, n);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instances [] rs=new Instances[2];
		rs[0]=train;
		rs[1]=test;
		return rs;
	}
	
	/** Standardize the predictive numeric attributes into [0,1]. */
	public static Instances [] standardize_(Instances train, Instances test){
		Standardize s=new Standardize();
		try {
			s.setInputFormat(train);
			train=s.useFilter(train,s);
			test=s.useFilter(test,s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instances [] rs=new Instances[2];
		rs[0]=train;
		rs[1]=test;
		return rs;
	}
	
	/** Get all the Cut Points on the predictive numeric attributes. */
	public static Map<Integer, ArrayList<Double>> getCutPoints(Instances data){
		Map<Integer,ArrayList<Double>> all_CutPoints = new HashMap<Integer,ArrayList<Double>>();
		for (int i=0;i<data.numAttributes()-1;i++){
			if(!data.attribute(i).isNumeric())
				continue;
			ArrayList<Double> CutPoints = new ArrayList<Double>();
			
			TreeSet ts=new TreeSet();
			{
				for (int j=0;j<data.numInstances();j++)
					ts.add(data.instance(j).value(i));
				double last=(double)ts.pollFirst();
				double temp;
				while (ts.size()>0){
					temp=(double)ts.pollFirst();
					if(last==temp)
						continue;
					CutPoints.add((last+temp)/2);
					last=temp;
				}
			}
			if (CutPoints.size()>0)
				all_CutPoints.put(i, CutPoints);
		}
		return all_CutPoints;
	}
}

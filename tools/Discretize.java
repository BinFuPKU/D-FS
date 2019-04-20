package tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import weka.core.Instance;
import weka.core.Instances;

public class Discretize {

	//������ɢ���ļ�������ɢ����ǩ
	public static HashMap<Integer,ArrayList<Double>> loadDis(String disPath){
		//
		HashMap<Integer,ArrayList<Double>> hmap_dis = new HashMap<Integer,ArrayList<Double>> ();
		
		for (String line:IO.read(disPath).trim().split("\n")){
			ArrayList<Double> vals = new ArrayList<Double>();
			int col=Integer.parseInt(line.trim().split(":\t")[0]);
			for (String val:line.trim().split(":\t")[1].split(" "))
				vals.add(Double.parseDouble(val));
			hmap_dis.put(col, vals);
		}
		
		return hmap_dis;
	}
	
	//��ʼ����������ɢ��������ɢ����ǩ
	public static String[] dis_label(Instances data,String[] targets,Map<Integer,ArrayList<Double>> hmap_dis) {
		String [] targets_new = new String[targets.length];
		for(int i=0;i<targets.length;i++)
			targets_new[i]=targets[i];
		
		Iterator iter = hmap_dis.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer key = (Integer) entry.getKey();
			ArrayList<Double> vals = (ArrayList<Double>) entry.getValue();
			
			for (double val:vals)
				targets_new=dis_label(data,targets_new,key,val);
		}
		
		return targets_new;
	}
	
	//������ɢ����ɢ��������ɢ����ǩ
	// < 0     >= 1
	public static String[] dis_label(Instances data,String[] targets,int col, double val) {
		String [] targets_new = new String[targets.length];
		for (int k=0;k<targets_new.length;k++)
			targets_new[k]=targets[k];
		for (int j=0;j<data.size();j++){
			if (data.instance(j).value(col)<val)
				targets_new[j]+="0";
			else
				targets_new[j]+="1";
		}
		String [] targets_new_ = new String[targets.length];
		HashMap<String,Integer> hmap = new HashMap<String,Integer>();
		int count=0;
		for(int j=0;j<data.size();j++){
			if (!hmap.containsKey(targets_new[j]))
				hmap.put(targets_new[j], count++);
			targets_new_[j] = hmap.get(targets_new[j])+"";
		}
		return targets_new_;
	}
	
	
	
	//���������ɢ������ɢ������
	public static Instances dis_data(Instances data, Map<Integer,ArrayList<Double>> hmap_dis) {
		
		Iterator iter = hmap_dis.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer key = (Integer) entry.getKey();
			ArrayList<Double> vals = (ArrayList<Double>) entry.getValue();
			data=dis_data(data,key,vals);
		}
		
		return data;
	}
	
	//��󣺵���������ɢ������ɢ������ 
	// < 0     >= 1
	private static Instances dis_data(Instances data,int i, ArrayList<Double> vals) {
		//С����
		Collections.sort(vals);
		
		for (int j=0;j<data.size();j++){
			Instance instance_j=data.get(j);
			int code=0;
			for (double val:vals){
				if (instance_j.value(i)<val)
					break;
				code+=1;
			}
			
			instance_j.setValue(i, code);
			data.set(j, instance_j);
		}
		return data;
	}
	
	// ��ͳһ���ˣ�Ȼ��ת��weka.filters.unsupervised.attribute.NumericToNominal
	public static void writeDis(HashMap<Integer,ArrayList<Double>> hmap_dis,String disPath){
		String txt="";
		ArrayList<Integer> cols = new ArrayList<Integer>();
		Iterator iter = hmap_dis.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer key = (Integer) entry.getKey();
			txt+=key+":\t";
			ArrayList<Double> vals = (ArrayList<Double>) entry.getValue();
			for(double val:vals)
				txt+=val+" ";
			txt+="\n";
		}
		
		IO.write(disPath, txt);
	}
	
	// ��ͳһ���ˣ�Ȼ��ת��weka.filters.unsupervised.attribute.NumericToNominal
	public static void writeData(Instances data,HashMap<Integer,ArrayList<Double>> hmap_dis,String dataPath){
		data=dis_data(data,hmap_dis);
		
		ArrayList<Integer> cols = new ArrayList<Integer>();
		for (Object o:hmap_dis.keySet().toArray())
			cols.add((int) o);

		data=PreProcessUtil.Numeric2Nominal(data, cols);
		data=PreProcessUtil.filterLeftClass(data, cols);
		
		IOUtil.instances2ARFF(dataPath, data);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}

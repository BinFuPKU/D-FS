package tools;

import java.util.HashMap;
import java.util.Iterator;

public class MeasureUtil2 {
	
	//Entropy
	private static double entropy(String [] labels){
		HashMap<String, Integer> hm_label=new HashMap<String, Integer>();
		double size_=labels.length;
		for(int i=0;i<size_;i++){
			hm_label.put(labels[i], hm_label.getOrDefault(labels[i], 0)+1);
		}
		double entropy=0;
		Iterator iter = hm_label.entrySet().iterator();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			String key = (String) entry.getKey();
			int val = (Integer) entry.getValue();
			entropy+=(val/size_)*Math.log(val/size_);
		}
		return entropy;
	}
	// logistic mutual information
	public static double muInfor_log(String [] targets, String [] labels){
		if (targets.length!=labels.length){
			System.out.println("len(targets)!=len(labels)");
			System.exit(1);
		}
		HashMap<String, Integer> hm_target_label=new HashMap<String, Integer>();
		HashMap<String, Integer> hm_target=new HashMap<String, Integer>();
		HashMap<String, Integer> hm_label=new HashMap<String, Integer>();
		double size_=(double)targets.length;
		for(int i=0;i<size_;i++){
			String key=targets[i]+"_"+labels[i];
			hm_target_label.put(key, hm_target_label.getOrDefault(key,0)+1);
			hm_target.put(targets[i], hm_target.getOrDefault(targets[i], 0)+1);
			hm_label.put(labels[i], hm_label.getOrDefault(labels[i], 0)+1);
		}
		double mi=0;
		
		Iterator iter = hm_target_label.entrySet().iterator();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			String key = (String) entry.getKey();
			int val = (Integer) entry.getValue();
			String [] snames=key.split("_");
//			System.out.println("\t\t"+hm_target.get(snames[0])/size_);
			mi+=(val/size_)*Math.log((val/size_)/((hm_target.get(snames[0])/size_)*(hm_label.get(snames[1])/size_)));
		}
//		System.out.println(mi);
		return mi;
	
	}
	
	// guassian mutual information
	public static double muInfor_gaussian(String [] targets, String [] labels){
		if (targets.length!=labels.length){
			System.out.println("len(targets)!=len(labels)");
			System.exit(1);
		}
		
		HashMap<String, Integer> hm_target_label=new HashMap<String, Integer>();
		HashMap<String, Integer> hm_target=new HashMap<String, Integer>();
		HashMap<String, Integer> hm_label=new HashMap<String, Integer>();
		double size_=(double)targets.length;
		for(int i=0;i<size_;i++){
			String key=targets[i]+"_"+labels[i];
			hm_target_label.put(key, hm_target_label.getOrDefault(key,0)+1);
			hm_target.put(targets[i], hm_target.getOrDefault(targets[i], 0)+1);
			hm_label.put(labels[i], hm_label.getOrDefault(labels[i], 0)+1);
		}
		double mi=0;
		
		Iterator iter = hm_target_label.entrySet().iterator();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			String target_label = (String) entry.getKey();
			int count = (Integer) entry.getValue();
			String [] snames=target_label.split("_");
			
			double p_x_y = count/size_;
			double p_x = hm_target.get(snames[0])/size_;
			double p_x__y = count/(double)hm_label.get(snames[1]);
			
			
			mi+=p_x_y * ( Math.pow(Math.E, - Math.pow(p_x, 2))
					- Math.pow(Math.E, - Math.pow(p_x__y, 2)));
			
//			System.out.println("\t\t"+hm_target.get(snames[0])/size_);
			
		}
//		System.out.println(mi);
		return mi;
	
	}
	// caim
	public static double caim(String [] targets, String [] labels){
		double caim =0;
		// col - row
		HashMap<String, HashMap<String, Integer>> t_l_table = new HashMap<String, HashMap<String, Integer>>();
		{
			for(int i=0;i<targets.length;i++){
				HashMap<String, Integer> temp = t_l_table.getOrDefault(targets[i], 
						new HashMap<String, Integer>());
				temp.put(labels[i], temp.getOrDefault(labels[i], 0)+1);
				t_l_table.put(targets[i], temp);
			}
		}
		{
			for(String target: t_l_table.keySet()){
				int sum=0,max=0;
				HashMap<String, Integer> temp = t_l_table.getOrDefault(target, 
						new HashMap<String, Integer>());
				for(String label: temp.keySet()){
					int val = temp.get(label);
					if (val>max)
						max=val;
					sum += val;
				}
				if(max>sum){
					System.out.println("something wrong!");
					System.exit(1);
				}
				if(sum>0)
					caim += (max * max)/(double)sum;
			}
		}
		if(t_l_table.size()>0)
			caim/=t_l_table.size();
		return caim;
	
	}
}

package tools;

import java.text.DecimalFormat;

public class DecimalFormatUtil {

	public static DecimalFormat df  = new DecimalFormat("######0.000000");
	public static DecimalFormat df4  = new DecimalFormat("######0.0000");
	public static DecimalFormat df5  = new DecimalFormat("######0.00000");
	
	
	public static double df(double value, int numdf){
		if(numdf==4)
			return Double.parseDouble(df4.format(value));
		if(numdf==5)
			return Double.parseDouble(df5.format(value));
		return Double.parseDouble(df.format(value));
	}
}

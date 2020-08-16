package src.processor;

import java.util.ArrayList;
import java.util.Arrays;

public class Comparator {
	public static final ArrayList<String> comparisonOperators = new ArrayList<String>(Arrays.asList("=", ">", ">=","<", "<=", "!="));
	public static boolean evaluateOperator(String[] colValues, int rowID, String[] comparators, String[] columnNames){
		if(comparators.length == 0){
			return true;
		}
		boolean comp = false;
		int pos = 1;
		int i=0;
		while(i < columnNames.length){
			if(columnNames[i].equals(comparators[0])){
				pos = i + 1;
				break;
			}
			i++;
		}
		if(pos != 1){
			comp = comparators[2].equals(colValues[pos - 1]);
		} else {
			int val = Integer.parseInt(comparators[2]);
			String op = comparators[1];
			if(op.equals(comparisonOperators.get(0))) {
				comp = rowID == val; 
			} else if(op.equals(comparisonOperators.get(1))) {
				comp = rowID > val;
			} else if(op.equals(comparisonOperators.get(2))) {
				comp = rowID >= val;
			} else if(op.equals(comparisonOperators.get(3))) {
				comp = rowID < val;
			} else if(op.equals(comparisonOperators.get(4))) {
				comp = rowID <= val;
			} else if(op.equals(comparisonOperators.get(5))) {
				comp = rowID != val;
			}
			
		}
		return comp;
	}
}

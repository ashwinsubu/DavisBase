package src.processor;

import java.util.HashMap;

public abstract class StateProcessorInterface {
	public HashMap<Integer, String[]> rowIDColsMap;
	public String[] columnName; 
	public int[] payLoad; 
	protected static int row = 0;
	protected final int space = 4;
	
	public StateProcessorInterface(){
		row = 0;
		rowIDColsMap = new HashMap<Integer, String[]>();
	}
	public void addRowID(int rowid, String[] val){}
	
	public void toString(String[] col){}
	
}

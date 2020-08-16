package src.db;

import src.processor.StateProcessorInterface;
import src.util.UserUtil;

public class DBStateHolder extends StateProcessorInterface{
	public DBStateHolder(){
		super();
	}
	
	@Override
	public void addRowID(int rowid, String[] val){
		rowIDColsMap.put(rowid, val);
		row++;
	}
	
	@Override
	public void toString(String[] colums){
		if(row == 0){
			System.out.println("---No Data---");
			return;
		}
		
		for(int i = 0; i < payLoad.length;payLoad[i] = columnName[i].length(), i++);
			
		for(String[] vals : rowIDColsMap.values()) {
			int j=0;
			while(j < vals.length) {
				payLoad[j] = payLoad[j] < vals[j].length() ? vals[j].length() : payLoad[j];
				j+=1;
			}
			
		}
		
		if(!"*".equals(colums[0])){
			int[] control = new int[colums.length];
			for(int j = 0; j < colums.length; j++)
				for(int i = 0; i < columnName.length; i++)
					if(colums[j].equals(columnName[i]))
						control[j] = i;

			System.out.println();
			
			for(int j = 0; j < control.length; j++) {
				UserUtil userUtil = new UserUtil(control[j],  columnName[control[j]]);
				System.out.print(userUtil.formatData()+"|");
			}
			
			System.out.println();
			for(int j = 0; j < control.length;System.out.print(DavisBase.line("-", payLoad[control[j]]+space)), j++);
			System.out.println();
			
			for(String[] i : rowIDColsMap.values()){
				for(int j = 0; j < control.length; j++) {
					UserUtil userUtil = new UserUtil(payLoad[control[j]], i[control[j]]);
					System.out.print(userUtil.formatData()+"|");
				}
				System.out.println();
			}
			System.out.println();
			
		} else {
			System.out.println();
			for(int i=0;i<payLoad.length;System.out.print(DavisBase.line("-", payLoad[i]+space-1)), i++); System.out.println();
			int q = 0;
			while(q < columnName.length) {
				UserUtil userUtil = new UserUtil(payLoad[q],  columnName[q]);
				System.out.print(userUtil.formatData()+"|");
				q++;
			}
			System.out.println();
			
			for(int i=0; i<payLoad.length;System.out.print(DavisBase.line("-", payLoad[i]+space-1)), i++);System.out.println();
			
			for(String[] columnVals : rowIDColsMap.values()){
				int j=0;
				if(j < columnVals.length)
					do{
						UserUtil userUtil = new UserUtil(payLoad[j],  columnVals[j]);
						System.out.print(userUtil.formatData()+"|");
						j++;
					} while(j < columnVals.length); 
				System.out.println();
			}
		}
	}
}
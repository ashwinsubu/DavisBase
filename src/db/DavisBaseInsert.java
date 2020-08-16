package src.db;

import java.io.IOException;
import java.io.RandomAccessFile;

import src.util.BTreeUtil;
import src.util.DatabaseUtil;
import src.util.InsertUtil;
import src.util.PageUtil;
import src.util.ValidationUtil;

public class DavisBaseInsert {
	 public static void parseInsertString(String statement) {
			System.out.println("INSERT METHOD");
			System.out.println("Parsing the string:\"" + statement + "\"");
			String table = statement.split(" ")[2];
			String cValues = statement.split("values")[1].trim();
			String[] insertedValues = cValues.substring(1, cValues.length()-1).split(",");
			int i = 0;
			while(i < insertedValues.length) {
				insertedValues[i] = insertedValues[i].trim();
				i++;
			}
			if(!DavisBase.checkDuplicateTable(table)){
				System.out.println("@@@@Error:::: Table "+ table +" does not exist.");
			} else {
				try {
					DavisBaseInsert.davisBaseInsert(table, insertedValues);
				} catch (Exception e) {
					System.err.println("@@@@@Error while insert table::::");
				}
			}
		}
	 
	 public static void davisBaseInsert(String table, String[] values) throws IOException{
			RandomAccessFile file = new RandomAccessFile("data/"+table+".tbl", "rw");
			davisBaseInsert(file, table, values);
			file.close();
	}

	public static void davisBaseInsert(RandomAccessFile file, String table, String[] values) throws IOException{
		try {
			String[] davisBaseDataTypes = DatabaseUtil.davisBaseDataTypes(table), nullVals = DatabaseUtil.checkNull(table);
			int m = 0;
			while(m < nullVals.length) {
				if("null".equalsIgnoreCase(values[m]) && "NO".equalsIgnoreCase(nullVals[m])){
					System.out.println("@@@Cant enter null in given column as per schema");
					System.out.println();
					return;
				}
				m++;
			}
		
			int k = Integer.parseInt(values[0]);
			int pg = PageUtil.searchKeyPage(file, k);
			if(pg != 0 && ValidationUtil.isBlockExist(file, pg, k)){
				System.out.println("@@@Error:: Duplicate key found");
				return;
			}
			pg = pg == 0 ? 1 : pg;
		
			byte[] typeCodes = new byte[davisBaseDataTypes.length-1];
			String[] dataType = DatabaseUtil.davisBaseDataTypes(table);
			int lenth = dataType.length;
			try {
				int i = 1;
				while( i < dataType.length){
					typeCodes[i - 1]= DavisBaseStarter.findTypeCode(values[i], dataType[i]);
					lenth = lenth + DavisBaseStarter.payloadLength(typeCodes[i - 1]);
					i++;
				}
			} catch (Exception e) {
				System.out.println("@@@Exception at Insert getStc/fieldLength::::" + e);
				e.printStackTrace();
			}
			int cellSize = lenth + 6;
			int offsetval = ValidationUtil.leafPageAvailability(file, pg, cellSize);
		
			if(offsetval != -1){
				InsertUtil.addLeaf(file, pg, offsetval, (short)lenth, k, typeCodes, values);
				return;
			}
			BTreeUtil.partitionLeaf(file, pg);
			davisBaseInsert(file, table, values);
		} catch(Exception e) {
			System.out.println("@@@Error while inserting:::" + e);
			e.printStackTrace();
		}
	}
	    
}

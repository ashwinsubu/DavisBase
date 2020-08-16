package src.db;

import java.io.RandomAccessFile;

import src.processor.DataProcessor;
import src.util.DatabaseUtil;

public class DavisBaseSelect {
	public static void selectCommand(String table, String[] columns, String[] comparators){
		try{
			RandomAccessFile file = new RandomAccessFile("data/"+table+".tbl", "rw");
			String[] columnName = DatabaseUtil.davisBaseColumns(table), type = DatabaseUtil.davisBaseDataTypes(table);
			DBStateHolder dbstate = new DBStateHolder();
			DataProcessor.process(file, comparators, columnName, type, dbstate);
			file.close();
			dbstate.toString(columns);
		} catch(Exception e){
			System.out.println("@@@@Error during selectCommand:::"+e);
		}
	}
}

package src.util;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import src.db.DBStateHolder;
import src.db.DavisBaseStarter;
import src.processor.DataProcessor;

public class DatabaseUtil {

	public static String[] davisBaseColumns(String table){
		String[] cols = new String[0];
		try{
			RandomAccessFile file = new RandomAccessFile(FileUtil.davisbase_columns_path, "rw");
			DBStateHolder dbstate = new DBStateHolder();
			processDataGen(table, file, dbstate);
			ArrayList<String> list = new ArrayList<String>();
			for(String[] colVal : dbstate.rowIDColsMap.values()){
				list.add(colVal[2]);
			}
			cols = list.toArray(new String[list.size()]);
			file.close();
			return cols;
		}catch(Exception e){
			System.out.println(e);
		}
		return cols;
	}

	private static String[] tableComparisonGen(String table) {
		String[] gen = {"table_name","=",table};
		return gen;
	}
	
	public static String[] davisBaseDataTypes(String table){
		String[] dataType = new String[0];
		try{
			RandomAccessFile file = new RandomAccessFile(FileUtil.davisbase_columns_path, "rw");
			DBStateHolder dbstate = new DBStateHolder();
			processDataGen(table, file, dbstate);
			HashMap<Integer, String[]> content = dbstate.rowIDColsMap;
			ArrayList<String> array = new ArrayList<String>();
			for(String[] x : content.values()){
				array.add(x[3]);
			}
			int size=array.size();
			dataType = array.toArray(new String[size]);
			file.close();
			return dataType;
		}catch(Exception e){
			System.out.println(e);
		}
		return dataType;
	}

	private static void processDataGen(String table, RandomAccessFile file, DBStateHolder dbstate) {
		String[] gen = tableComparisonGen(table);
		DataProcessor.process(file, gen, DavisBaseStarter.davisbase_column_name, dbstate);
	}
	
	
	public static String[] checkNull(String table){
		String[] nullVals = new String[0];
		try{
			RandomAccessFile file = new RandomAccessFile(FileUtil.davisbase_columns_path, "rw");
			DBStateHolder dbstate = new DBStateHolder();
			processDataGen(table, file, dbstate);
			ArrayList<String> list = new ArrayList<String>();
			addDataToList(dbstate.rowIDColsMap, list);
			nullVals = list.toArray(new String[list.size()]);
			file.close();
			return nullVals;
		} catch(Exception e) {
			System.out.println(e);
		}
		return nullVals;
	}

	private static void addDataToList(HashMap<Integer, String[]> data, ArrayList<String> list) {
		for(String[] colVals : data.values()){
			list.add(colVals[5]);
		}
	}
	
	
	
}

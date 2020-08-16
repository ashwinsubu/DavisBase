package src.db;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

import src.util.*;
/**
 * @author ashwin
 *
 */
public class DavisBaseCreate {
	static FileUtil fileUtil = new FileUtil();
	
public static void parseCreateTable(String createTableString) {
		
		System.out.println("STUB: Calling your method to create a table");
		System.out.println("Parsing the string:\"" + createTableString + "\"");
		ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableString.split(" ")));
		
		if (!"table".equalsIgnoreCase(createTableTokens.get(1))){
			System.err.println("@@ERROR: Check DavisBaseSQL Syntax at line 1");
		} else {
			String tableName = createTableTokens.get(2);
			String columns = createTableString.split(tableName)[1].trim();
			String[] createdColumns = columns.substring(1, columns.length()-1).split(",");
			for(int i = 0; i < createdColumns.length; createdColumns[i] = createdColumns[i].trim(),i++);
			if(!DavisBase.checkDuplicateTable(tableName)){
				createTable(tableName, createdColumns);
			} else {
				System.err.println("ERROR:: The table "+tableName+" already exists. Drop table to add again");						
			}
		}
	}

public static void createTable(String tableName, String[] col){
	try{	
		RandomAccessFile file = new RandomAccessFile("data/"+tableName+".tbl", "rw");
		FileUtil fileUtil = new FileUtil();
		fileUtil.setFileProps(file, true, -1);
		file.close();
		
		file = new RandomAccessFile(FileUtil.davisbase_tables_path, "rw");
		
		int noOfpgs = PageUtil.getNoOfPages(file), pg=-1;
		
		for(int i = 1; i <= noOfpgs; pg = (0 == PageUtil.lastPage(file, i)) ?i:pg, i++);
		
		int[] keys = ValidationUtil.getBlocks(file, pg == -1 ? 1:pg);
		int l = keys[0];
		for(int i = 0; i < keys.length;l = keys[i] > l ? keys[i]: l, i++);
		
		DavisBaseInsert.davisBaseInsert("davisbase_tables", new String[]{l+1+"", tableName});
		
		file.close();
		file = new RandomAccessFile(FileUtil.davisbase_columns_path, "rw");
		
		noOfpgs = PageUtil.getNoOfPages(file);
		pg=1;
		for(int p = 1; p <= noOfpgs; p++){
			int rm = PageUtil.lastPage(file, p);
			pg = rm == 0 ? p : pg;
		}
		
		keys = ValidationUtil.getBlocks(file, pg);
		l = keys[0];
		for(int e = 0; e < keys.length; e++) {
			if(keys[e]>l){l = keys[e];}
		}
		file.close();

		for(int i = 0; i < col.length; i++){
			String[] token = col[i].split(" ");
			String col_name = token[0], dataType = token[1].toUpperCase(),ordinal_pos = Integer.toString(i+1), is_nullable;
			is_nullable = token.length > 2 ? "NO" : "YES";
			String[] value = {Integer.toString(++l), tableName, col_name, dataType, ordinal_pos, is_nullable};
			DavisBaseInsert.davisBaseInsert("davisbase_columns", value);
		}

	} catch(Exception e) {
		System.out.println(e);
	}
}

}

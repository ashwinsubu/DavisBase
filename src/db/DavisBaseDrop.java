package src.db;
import java.io.RandomAccessFile;

import src.processor.DataProcessor;
import src.util.BTreeUtil;
import src.util.FileUtil;
import src.util.PageUtil;

import java.io.File;
import java.io.IOException;
public class DavisBaseDrop {
	public static void dropTable(String dropTableString) {
		System.out.println("DROP METHOD");
		System.out.println("Parsing the string:\"" + dropTableString + "\"");
		
		String[] tokens=dropTableString.split(" ");
		String tableName = tokens[2];
		if(!DavisBase.checkDuplicateTable(tableName)){
			System.out.println("Table "+tableName+" does not exist.");
			return;
		}
		dropDavisBaseTable(tableName);
	}
	
	private static void dropDavisBaseTable(String table){
		try{
			
			RandomAccessFile file = new RandomAccessFile(FileUtil.davisbase_tables_path, "rw");
			int noOfPages = PageUtil.getNoOfPages(file);
			int pg = 1;
			while(pg <= noOfPages){
				file.seek(PageUtil.getSeekPageOffSetForID(pg, 0));
				byte fileType = file.readByte();
				if(fileType == FileUtil.leafPage) {
					short[] blockaddr = FileUtil.blockArray(file, pg);
					int k = 0;
					k = dbDropProcessOffset(table, file, pg, blockaddr, k);
					BTreeUtil.setBlockIndex(file, pg, (byte)k);
				}
				pg++;
			}

			file = new RandomAccessFile(FileUtil.davisbase_columns_path, "rw");
			noOfPages = PageUtil.getNoOfPages(file);
			int page = 1;
			while(page <= noOfPages){
				file.seek(PageUtil.getSeekPageOffSetForID(page, 0));
				byte fileType = file.readByte();
				if(fileType == FileUtil.leafPage) {
					short[] blockaddr = FileUtil.blockArray(file, page);
					int k = 0;
					k = dbDropProcessOffset(table, file, page, blockaddr, k);
					BTreeUtil.setBlockIndex(file, page, (byte)k);
				}
				page++;
			}

			File dropFile = new File(FileUtil.dataFolderPath, table + ".tbl"); 
			dropFile.delete();
		} catch(Exception e){
			System.out.println("@@Error: drop table:::" + e);
			e.printStackTrace();
		}

	}

	private static int dbDropProcessOffset(String table, RandomAccessFile file, int page, short[] cellsAddr, int k) throws IOException {
		int i = 0;
		while(i < cellsAddr.length) {
			String[] vals = DataProcessor.getProcessedValues(file, BTreeUtil.blockLocator(file, page, i));
			if(!vals[1].equals(table)) {
				BTreeUtil.blockOffSetter(file, page, k, cellsAddr[i]);
				k++;
			}
			i++;
		}
		return k;
	}

}

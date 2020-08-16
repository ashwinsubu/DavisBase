package src.db;

import src.util.DirectoryUtil;
import src.util.FileUtil;
import java.io.RandomAccessFile;
import java.io.File;



public class DavisBaseStarter implements StarterConstsInterface{
	private static int s1=3*(int)Math.pow(2, 3);
	private static int s2=s1+1;
	private static int offsetTable=(int) (FileUtil.pageSize-s1);
	private static int offsetCol=offsetTable-s2;
	private static FileUtil fileUtil = new FileUtil();
	
	public static void initDataTypeMap() {
		dataTypeMap.put("tinyint", (byte) 0x04);
		dataTypeMap.put("smallint",(byte) 0x05);
		dataTypeMap.put("int", (byte) 0x06);
		dataTypeMap.put("bigint",(byte) 0x07);
		dataTypeMap.put("real", (byte)0x08);
		dataTypeMap.put("double", (byte)0x09);
		dataTypeMap.put("datetime", (byte)0x0A);
		dataTypeMap.put("date", (byte)0x0B);
		dataTypeMap.put("text", (byte)0x0C);
	}
	
	public static void initNullDataTypeMap() {
		nullDataTypeMap.put("tinyint", (byte)0x00);
		nullDataTypeMap.put("smallint",(byte) 0x01);
		nullDataTypeMap.put("int", (byte)0x02);
		nullDataTypeMap.put("bigint",(byte) 0x03);
		nullDataTypeMap.put("real",(byte) 0x02);
		nullDataTypeMap.put("double", (byte)0x03);
		nullDataTypeMap.put("datetime",(byte) 0x03);
		nullDataTypeMap.put("date", (byte)0x03);
		nullDataTypeMap.put("text",(byte) 0x03);
	}
	
public static void start() {
		initDataTypeMap();
		initNullDataTypeMap();
		try {
			File directory = new File(FileUtil.dataFolderPath);
			boolean exists = directory.exists();
			if(exists) {
				DirectoryUtil dirUtil = new DirectoryUtil();
				dirUtil.deleteDirectory(directory);
			}
			int writeInt = 1, writeByte = 28, writeShort = 20;
			directory.mkdir();
			RandomAccessFile dbtablefile = new RandomAccessFile(FileUtil.davisbase_tables_path, "rw");
			fileUtil.allocateInitialFileStruct(dbtablefile, offsetCol, offsetCol);
			fileUtil.writeIntoDavisBaseFile(dbtablefile, offsetTable,"davisbase_tables",writeInt, writeByte, writeShort);
			fileUtil.writeIntoDavisBaseFile(dbtablefile, offsetCol,"davisbase_columns",writeInt+1, writeByte+1, writeShort+1);
			dbtablefile.close();
		}
		catch (Exception e) {
			System.err.println(e);
		}
		
		try {
			RandomAccessFile dbColFile = new RandomAccessFile(FileUtil.davisbase_columns_path, "rw");
			fileUtil.setFileProps(dbColFile, true,-1);
			
			dbColFile.writeByte(0x08);
			
			dbColFile.writeShort(FileUtil.preProcessedOffset[7]); 
			for(int i=0;i<2;dbColFile.writeInt(0),i++);
			for(int i=0;i<FileUtil.preProcessedOffset.length; dbColFile.writeShort(FileUtil.preProcessedOffset[i++]));

			int index=0, wb1=40, wb2=1, wb3=5, wb4=28, wb5=17, wb6=15, wb7=4, wsize=14, wsize1=1;
			fileUtil.preProcessDavisBaseData(dbColFile, FileUtil.preProcessedOffset[index++],wb1-7,wb2++,wb3,wb4,wb5,wb6++,wb7,wsize,"davisbase_tables","rowid","INT",wsize1++);
			
			fileUtil.preProcessDavisBaseData(dbColFile, FileUtil.preProcessedOffset[index++],wb1-1,wb2++,wb3,wb4++,wb5+5,wb6--,wb7,wsize,"davisbase_tables",davisbase_column_name[1],"TEXT",wsize1--);
			
			fileUtil.preProcessDavisBaseData(dbColFile, FileUtil.preProcessedOffset[index++],wb1-6,wb2++,wb3,wb4,wb5,wb6++,wb7,wsize,"davisbase_columns",davisbase_column_name[0],"INT",wsize1++);
			
			fileUtil.preProcessDavisBaseData(dbColFile, FileUtil.preProcessedOffset[index++],wb1,wb2++,wb3,wb4,wb5+5,wb6,wb7,wsize,"davisbase_columns",davisbase_column_name[1],"TEXT",wsize1++);
			
			fileUtil.preProcessDavisBaseData(dbColFile, FileUtil.preProcessedOffset[index++],wb1+1,wb2++,wb3,wb4,wb5+6,wb6,wb7,wsize,"davisbase_columns",davisbase_column_name[2],"TEXT",wsize1++);
			
			fileUtil.preProcessDavisBaseData(dbColFile, FileUtil.preProcessedOffset[index++],wb1-1,wb2++,wb3,wb4,wb5+4,wb6,wb7,wsize,"davisbase_columns",davisbase_column_name[3],"TEXT",wsize1++);
			
			fileUtil.preProcessDavisBaseData(dbColFile, FileUtil.preProcessedOffset[index++],wb1-1,wb2++,wb3,wb4,wb5+11,wb6+3,wb7,wsize,"davisbase_columns",davisbase_column_name[4],"TINYINT",wsize1++);
			
			fileUtil.preProcessDavisBaseData(dbColFile, FileUtil.preProcessedOffset[index],wb1+1,wb2,wb3,wb4,wb5+6,wb6,wb7,wsize,"davisbase_columns",davisbase_column_name[5],"TEXT",wsize1);
			
			dbColFile.close();
		}
		catch (Exception ex) {
			System.err.println(ex);
		}
}

public static byte findTypeCode(String value, String dataType){
	if(!"null".equalsIgnoreCase(value)){
		int i=0;
		while(i<data_type_array.length) {
			if(dataType.equalsIgnoreCase(data_type_array[i])) {
				if("TEXT".equalsIgnoreCase(dataType)) {
					return (byte) (data_type_vals[i] + value.length());
				}
				return data_type_vals[i];
			}
			i++;
		}
		
	} else {
		int i=0;
		while(i<data_type_array.length) {
			if(dataType.equalsIgnoreCase(data_type_array[i])) {
				return null_data_type_vals[i];
			}
			i++;
		}
	}
	return null_data_type_vals[0];
}

public static short payloadLength(byte type){
	int i=0;
	while( i < payloadLen.length) {
		if(type == payloadLen[i]) {
			return payLoadVals[i];
		}
		i++;
	}
	return  (short)(type - data_type_vals[data_type_vals.length-1]);
}

}

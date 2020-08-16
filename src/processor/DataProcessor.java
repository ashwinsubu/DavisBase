package src.processor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import src.db.DBStateHolder;
import src.util.BTreeUtil;
import src.util.FileUtil;
import src.util.InsertUtil;
import src.util.PageUtil;

public class DataProcessor {
	
	public static void process(RandomAccessFile file, String[] comparator, String[] columnName, DBStateHolder dbstate){
		try{
			int pages = PageUtil.getNoOfPages(file),p = 1;
			while( p <= pages){
				int seekoff = (int) PageUtil.getSeekPageOffSetForID(p, 0);
				file.seek(seekoff);
				if(FileUtil.leafPage ==  file.readByte()) {
					int i=0;
					int cllno = BTreeUtil.blockIndex(file, p);
					if(i < cllno) {
						do {
							long clloc = BTreeUtil.blockLocator(file, p, i);
							String[] values = getProcessedValues(file, clloc);
							if(Comparator.evaluateOperator(values, Integer.parseInt(values[0]), comparator, columnName)) {
								dbstate.addRowID(Integer.parseInt(values[0]), values);
							}
							i++;
						} while (i < cllno);
					}
				}
				p++;
			}
			dbstate.columnName = columnName;
			dbstate.payLoad = new int[columnName.length];

		} catch(Exception e){
			System.out.println("@@@@ DataProcessor error::: " + e);
			e.printStackTrace();
		}
	}
	
public static String[] getProcessedValues(RandomAccessFile file, long block){
		String[] getValues = null;
		try{
			long seekpos = 2 + block;
			file.seek(seekpos);
			int k = file.readInt();
			
			int colums = file.readByte();
			byte[] typeCodes = new byte[colums];
			file.read(typeCodes);
			int cLen = colums+1;
			getValues = new String[cLen];
			
			getValues[0] = k+"";
			int i=1;
			while(i <= colums){
				byte typeCode = typeCodes[i-1];
				findTypeCode(typeCode, file, i, getValues);
				i++;
			}

		} catch(Exception e) {
			System.out.println("@@Error in Dataprocessor::: " + e);
			e.printStackTrace();
		}

		return getValues;
	}

public static void process(RandomAccessFile file, String[] cmp, String[] columnName, String[] type, DBStateHolder dbstate){
	try{
		int p = 1;
		while(p <= PageUtil.getNoOfPages(file)){
			file.seek(PageUtil.getSeekPageOffSetForID(p, 0));
			byte pType = file.readByte();
			getColumnNameAndFormat(p, file, pType, type, cmp, columnName, dbstate);
			p++;
		}

		dbstate.columnName = columnName;
		dbstate.payLoad = new int[columnName.length];

	} catch(Exception e){
		System.out.println("@@@Error in DataProecessor::: " + e);
		e.printStackTrace();
	}

}

private static void findTypeCode(byte typeCode, RandomAccessFile file, int i, String[] values) throws IOException {
	int typeNo=0;
	String nl = "null";
	if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		file.readByte();
        values[i] = nl;
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		file.readShort();
        values[i] = nl;
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		 file.readInt();
         values[i] = nl;
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		file.readLong();
        values[i] = nl;
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		values[i] = Integer.toString(file.readByte());
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		 values[i] = Integer.toString(file.readShort());
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		values[i] = Integer.toString(file.readInt());
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		values[i] = Long.toString(file.readLong());
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		values[i] = String.valueOf(file.readFloat());
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		values[i] = String.valueOf(file.readDouble());
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(InsertUtil.datePattern);
		values[i] = dateFormat.format(new Date(file.readLong()));
	} else if(typeCode == FileUtil.listOfTypeCodes[typeNo++]) {
		SimpleDateFormat df = new SimpleDateFormat(InsertUtil.datePattern);
		values[i] = df.format(new Date(file.readLong())).substring(0,typeNo-2);
	} else {
		byte[] bytes = new byte[(int)(typeCode - FileUtil.listOfTypeCodes[typeNo])];
		file.read(bytes);
		values[i] = new String(bytes);
	}
}

public static void getColumnNameAndFormat(int p, RandomAccessFile file, byte pType, String[] type, String[] comparator, String[] columnName, DBStateHolder dbstate ) throws IOException {
	if(FileUtil.leafPage == pType){
		 int i=0;
		 while(i < BTreeUtil.blockIndex(file, p)){
			String[] vals = DataProcessor.getProcessedValues(file, BTreeUtil.blockLocator(file, p, i));
			int j=0;
			int typeLen = type.length;
			dataHandler(type, vals, j, typeLen);
			if(Comparator.evaluateOperator(vals, Integer.parseInt(vals[0]) , comparator, columnName)) {
				dbstate.addRowID(Integer.parseInt(vals[0]), vals);
			}
			i++;
		 }
	  }
}

private static void dataHandler(String[] type, String[] vals, int j, int typeLen) {
	while(j < typeLen) {
		if(type[j].startsWith("DATE")) {
			vals[j]="'"+vals[j]+"'";
		}
		j++;
	}
	j=0;
	while(j < typeLen) {
		if(type[j].startsWith("DATE")) {
			int valLen = vals[j].length();
			vals[j] = vals[j].substring(1, valLen - 1);
		}
		j++;
	}
}

	
}

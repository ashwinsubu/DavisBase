package src.util;

import java.io.IOException;
import java.io.RandomAccessFile;


public class FileUtil {
	public static int pageSize = 512;
	public static final int []preProcessedOffset = {469,422,378,330,281,234,177,128};
	public static final int []listOfTypeCodes = {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C};
	public static int leafPage = 0x0D;
	public static int interiorPage = 0x05;
	public static final String dataFolderPath = "data";
	public static final String davisbase_columns_path = "data/davisbase_columns.tbl";
	public static final String davisbase_tables_path = "data/davisbase_tables.tbl";
	public FileUtil(){}
	
	public void setFileProps(RandomAccessFile file, boolean isLeafPage, int writeByte) throws IOException {
		setFileProps(file, isLeafPage, writeByte, 0, pageSize);
	}
	
	public void setFileProps(RandomAccessFile file, boolean isLeafPage, int writeByte, int seek, int length) throws IOException {
		file.setLength(length);
		file.seek(seek);
		file.write(isLeafPage? leafPage : writeByte); 
	}
	
	 public void preProcessDavisBaseData(RandomAccessFile file, int preProcessedOffset, int ws, int wi, int wb1, int wb2, int wb3,int wb4,int wb5, int wb6,String tableName,String colName,String dataType,int wb7) throws IOException {
		file.seek(preProcessedOffset);
		file.writeShort(ws);
		file.writeInt(wi);
		file.writeByte(wb1);
		file.writeByte(wb2);
		file.writeByte(wb3);
		file.writeByte(wb4);
		file.writeByte(wb5);
		file.writeByte(wb6);
		file.writeBytes(tableName); 
		file.writeBytes(colName); 
		file.writeBytes(dataType); 
		file.writeByte(wb7);
		file.writeBytes("NO"); 
	}
	 
	 public void allocateInitialFileStruct(RandomAccessFile file, int offSetCol, int offSetTable) throws IOException {
		setFileProps(file, true, -1);
		file.writeByte(0x02); 
		
		file.writeShort(offSetCol); 
		for(int i=0;i<2;file.writeInt(0),i++);
		file.writeShort(offSetTable);
		file.writeShort(offSetCol);
	}
	 
	 public void writeIntoDavisBaseFile(RandomAccessFile file, int offset, String name,int writeInt,int writeByte,int writeShort) throws IOException {
		file.seek(offset);
		file.writeShort(writeShort);
		file.writeInt(writeInt); 
		file.writeByte(1);
		file.writeByte(writeByte);
		file.writeBytes("davisbase_tables");
	}
	 
	 public static void moveToFilePosition(RandomAccessFile file, long seekpos, int rootpage, int page){
			try{
				if(seekpos == 0){
					file.seek(PageUtil.getSeekPageOffSetForID(rootpage, 1));
				} else {
					file.seek(seekpos);
				}
				file.writeInt(page);
			}catch(Exception e){
				System.out.println(e);
			}
	}
	 
	 public static long currentFilePosition(RandomAccessFile file, int page, int parent){
			long val = 0;
			try{
				int blocks = (int)(BTreeUtil.blockIndex(file, parent));
				for(int i=0; i < blocks; i++){
					long seekpos = BTreeUtil.blockLocator(file, parent, i);
					file.seek(seekpos);
					int endpg = file.readInt();
					if(endpg == page){
						val = seekpos;
					}
				}
			}catch(Exception e){
				System.out.println(e);
			}

			return val;
	}
	 public static short[] blockArray(RandomAccessFile file, int page) throws IOException{
			int n = (int)(BTreeUtil.blockIndex(file, page));
			short[] blocks = new short[n];
			try{
				file.seek((page-1)*pageSize+12);
				for(int i = 0; i < n;blocks[i] = file.readShort(), i++);
			}catch(Exception e){
				System.out.println(e);
			}

			return blocks;
	}
}

package src.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertUtil {
	public static final String datePattern = "yyyy-MM-dd_HH:mm:ss";
	private static long getSeekPageOffSetForID(int p, int n) {
		return (p-1)*FileUtil.pageSize+ n;
	}
	public static void addLeaf(RandomAccessFile file, int page, int offset, short plsize, int key, byte[] typeCodes, String[] vals){
		int i = 1;
		try{
			file.seek(getSeekPageOffSetForID(page, offset));
			file.writeShort(plsize);
			file.writeInt(key);
			file.writeByte(vals.length - 1);
			file.write(typeCodes);
			while(i < vals.length){
				if(typeCodes[i-1] == 0x00) {
					file.writeByte(0);
				} else if(typeCodes[i-1] == 0x01) {
					file.writeShort(0);
				} else if(typeCodes[i-1] == 0x02) {
					file.writeInt(0);
				} else if(typeCodes[i-1] == 0x03) {
					file.writeLong(0);
				} else if(typeCodes[i-1] == 0x04) {
					file.writeByte(Byte.parseByte(vals[i]));
				} else if(typeCodes[i-1] == 0x05) {
					file.writeShort(Short.parseShort(vals[i]));
				} else if(typeCodes[i-1] == 0x06) {
					file.writeInt(Integer.parseInt(vals[i]));
				} else if(typeCodes[i-1] == 0x07) {
					file.writeLong(Long.parseLong(vals[i]));
				} else if(typeCodes[i-1] == 0x08) {
					file.writeFloat(Float.parseFloat(vals[i]));
				} else if(typeCodes[i-1] == 0x09) {
					file.writeDouble(Double.parseDouble(vals[i]));
				} else if(typeCodes[i-1] == 0x0A) {
					String s = vals[i];
					Date date = new SimpleDateFormat(datePattern).parse(s.substring(1, s.length()-1));
					file.writeLong(date.getTime());
				} else if(typeCodes[i-1] == 0x0B) {
					String str = vals[i];
					str = str.substring(1, str.length() - 1);
					str = str + "_00:00:00";
					Date date = new SimpleDateFormat(datePattern).parse(str);
					file.writeLong(date.getTime());
				} else {
					file.writeBytes(vals[i]);
				}
				i++;
			}
			int n = BTreeUtil.blockIndex(file, page);
			BTreeUtil.setBlockIndex(file, page, (byte) (n+1));
			file.seek(getSeekPageOffSetForID(page, 12 + n*2));
			file.writeShort(offset);
			file.seek(getSeekPageOffSetForID(page, 2));
			int data = file.readShort();
			if(0 == data || offset < data) {
				file.seek(getSeekPageOffSetForID(page, 2));
				file.writeShort(offset);
			}
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void addInnerBlock(RandomAccessFile file, int page, int descendant, int key){
		try {
			file.seek(getSeekPageOffSetForID(page, 2));
			short data = file.readShort();
			data = (short) (data==0 ? FileUtil.pageSize : data);
			data-=8;

			file.seek(getSeekPageOffSetForID(page, data));
			file.writeInt(descendant);
			file.writeInt(key);
			file.seek(getSeekPageOffSetForID(page, 2));
			file.writeShort(data);

			byte no = BTreeUtil.blockIndex(file, page);
			BTreeUtil.blockOffSetter(file, page ,no, data);
			BTreeUtil.setBlockIndex(file, page, (byte)(no+1));

		} catch(Exception e){
		 	System.out.println(e);
		}
	}
	
	public static void reArrangeCells(RandomAccessFile file, int page) throws IOException{
		 byte num = BTreeUtil.blockIndex(file, page);
		 try{
			 int[] keys = ValidationUtil.getBlocks(file, page);
			 short[] cells = FileUtil.blockArray(file, page);
			 for (int i = 1; i < num; i++) {
				 int j = i;
				 while(j > 0){
	               if(keys[j-1] > keys[j]){
	            	   swap(keys[j], keys[j-1]);
	            	   swap(cells[j], cells[j-1]);
	               }
	               j--;
	           }
	        }
        	file.seek(getSeekPageOffSetForID(page, 12));
        	for(int i = 0; i < num; file.writeShort(cells[i]), i++);
        } catch(Exception e){
        	System.out.println("@@@@@Error in reArrangeCells");
        }
	}
	
	public static void swap(int a, int b) {
		a += b;
		b = a - b;
		a -= b;
	}
	
}

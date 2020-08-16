package src.util;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ValidationUtil {
	private static long getSeekPageOffSetForID(int p, int n) {
		return (p-1)*FileUtil.pageSize+ n;
	}
	private static int getLeafSpace(int data, RandomAccessFile file, int page) throws IOException {
		return data - 20 - 2*BTreeUtil.blockIndex(file, page);
	}
	public static int leafPageAvailability(RandomAccessFile file, int page, int size){////
		try{
			file.seek(getSeekPageOffSetForID(page, 2));
			int data = file.readShort();
			if(data != 0) {
				int availability = getLeafSpace(data, file, page);
				if(availability >= size) {
					return data - size;
				}
			} else {
				return FileUtil.pageSize - size;
			}
		} catch(Exception e){
			System.out.println(e);
		}
		return -1;
	}
	
	public static boolean isInnerAvailable(RandomAccessFile file, int page) throws IOException{ ///
		return BTreeUtil.blockIndex(file, page) > 30;
	}
	
	public static boolean isBlockExist(RandomAccessFile file, int page, int key) throws IOException{///
		boolean blockExist=false;
		for(int i : getBlocks(file, page)) {
			blockExist = i==key;
			if(blockExist) break;
			
		}
		return blockExist;
	}
	
	public static int[] getBlocks(RandomAccessFile file, int page) throws IOException{
		int n = BTreeUtil.blockIndex(file, page);
		int[] blocks = new int[n];

		try{
			file.seek(getSeekPageOffSetForID(page, 0));
			byte typeCode = file.readByte();
			byte offsetVal = (byte) (typeCode == FileUtil.interiorPage ? 4 : 2);
			int i = 0;
			while(i < n){
				file.seek(offsetVal + BTreeUtil.blockLocator(file, page, i));
				blocks[i++] = file.readInt();
			}
		} catch(Exception e){
			System.out.println(e);
		}
		return blocks;
	}
	
	public static int centerPage(RandomAccessFile file, int page) throws IOException{
		file.seek(getSeekPageOffSetForID(page, 0));
		byte typeCode = file.readByte();
		file.seek(BTreeUtil.blockLocator(file, page, (int)Math.ceil((double)BTreeUtil.blockIndex(file, page) / 2) - 1));
		if(typeCode == FileUtil.interiorPage) {
			file.readInt(); 
		} else if(typeCode == FileUtil.leafPage) {
			file.readShort();
		}
		return file.readInt();
	}
}

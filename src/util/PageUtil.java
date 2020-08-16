package src.util;

import java.io.IOException;
import java.io.RandomAccessFile;

public class PageUtil {
	private static FileUtil fileUtil = new FileUtil();
	public static int getNoOfPages(RandomAccessFile file) throws IOException{
		return (int)(file.length()/(long)FileUtil.pageSize);
	}
	public static long getSeekPageOffSetForID(int p, int n) {
		return (p-1)*FileUtil.pageSize+ n*4;
	}
	
	public static void makeLastPage(RandomAccessFile file, int page, int rightLeaf){
		try{
			file.seek(getSeekPageOffSetForID(page, 1));
			file.writeInt(rightLeaf);
		} catch(Exception e) {
			System.out.println("@@@Error at makeLastPage...." + e);
		}

	}
	public static int lastPage(RandomAccessFile file, int page) throws IOException{
		file.seek(getSeekPageOffSetForID(page, 1));
		return file.readInt();
	}
	
	public static void makeRoot(RandomAccessFile file, int page, int root){
		try{
			file.seek(getSeekPageOffSetForID(page, 2));
			file.writeInt(root);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static int root(RandomAccessFile file, int page) throws IOException{
			file.seek(getSeekPageOffSetForID(page, 2));
			return file.readInt();
	}
	
	public static int addAsInnerBlock(RandomAccessFile file){
		int n = 0;
		try {
			n = getNoOfPages(file);
			fileUtil.setFileProps(file, false, FileUtil.interiorPage, FileUtil.pageSize*n, FileUtil.pageSize*(n+1));
		} catch(Exception e) {
			System.out.println(e);
		}
		return n+1;
	}
	
	public static int searchKeyPage(RandomAccessFile file, int key){
		int val = 1;
		try{
			int numPages = PageUtil.getNoOfPages(file);
			for(int page = 1; page <= numPages; page++){
				file.seek((page - 1)*FileUtil.pageSize);
				byte pageType = file.readByte();
				if(pageType == FileUtil.leafPage){
					int[] blocks = ValidationUtil.getBlocks(file, page);
					if(blocks.length == 0)
						return 0;
					int rm = PageUtil.lastPage(file, page);
					if(blocks[0] <= key && key <= blocks[blocks.length - 1]){
						return page;
					} else if (rm == 0 && blocks[blocks.length - 1] < key){
						return page;
					}
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}

		return val;
	}
	
}

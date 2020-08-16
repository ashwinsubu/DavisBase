package src.util;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BTreeUtil {
	private static FileUtil fileUtil= new FileUtil();
	private static long getSeekPageOffSetForID(int p, int id) {
		return p*FileUtil.pageSize+12+id*2;
	}
	private static long getBlockPageOffSetForID(int p) {
		return p*FileUtil.pageSize + 1;
	}
	public static void blockOffSetter(RandomAccessFile file, int page, int id, int offsetVal){
		int p = page - 1;
		try{
			file.seek(getSeekPageOffSetForID(p, id));
			file.writeShort(offsetVal);
		} catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static short blockOffset(RandomAccessFile file, int page, int keyID) throws IOException{
		short offset = 0;
		int p = page - 1;
		file.seek(getSeekPageOffSetForID(p, keyID));
		offset = file.readShort();
		return offset;
	}
	
	public static void setBlockIndex(RandomAccessFile file, int page, byte n){
		int p = page -1;
		try{
			file.seek(getBlockPageOffSetForID(p));
			file.writeByte(n);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static byte blockIndex(RandomAccessFile file, int page) throws IOException{
		int p = page - 1;
		file.seek(getBlockPageOffSetForID(p));
		return file.readByte();
	}
	
	public static long blockLocator(RandomAccessFile file, int page, int keyID) throws IOException{
		int p = page -1;
		file.seek(getSeekPageOffSetForID(p, keyID));
		return p*FileUtil.pageSize + file.readShort();
	}
	
	public static int convertPageToLeaf(RandomAccessFile file){
		int n = 0;
		try{
			n = getNoOfPages(file);
			fileUtil.setFileProps(file, true, FileUtil.leafPage, (n)*FileUtil.pageSize, FileUtil.pageSize*(n+1));
		}catch(Exception e){
			System.out.println(e);
		}
		return n+1;
	}
	
	public static void partitionLeafPage(RandomAccessFile file, int thispage, int overFlownPage){
		try {
			int noOfCells = BTreeUtil.blockIndex(file, thispage);
			int cell1 = (int) Math.ceil((double) noOfCells / 2) - 1;
			int cell2 = noOfCells - cell1;
			int data = FileUtil.pageSize;
			int i=cell1;
			int s = 2*3;
			byte[] c;
			if(i<noOfCells) {
				do{
					file.seek(BTreeUtil.blockLocator(file, thispage, i));
					int size = file.readShort() + s;
					data -= size;
					file.seek(BTreeUtil.blockLocator(file, thispage, i));
					c = new byte[size];
					file.read(c);
					file.seek(PageUtil.getSeekPageOffSetForID(overFlownPage, 0)+data);
					file.write(c);
					BTreeUtil.blockOffSetter(file, overFlownPage, i-cell1, data);
					i++;
				}while(i < noOfCells);
			}
			file.seek(2 + PageUtil.getSeekPageOffSetForID(overFlownPage, 0));
			file.writeShort(data);
			file.seek(FileUtil.pageSize*(thispage-1)+2);
			file.writeShort(BTreeUtil.blockOffset(file, thispage, cell1-1));
			
			PageUtil.makeLastPage(file, overFlownPage, PageUtil.lastPage(file, thispage));
			PageUtil.makeLastPage(file, thispage, overFlownPage);
			
			int parent = PageUtil.root(file, thispage);
			PageUtil.makeRoot(file, overFlownPage, parent);
			
			BTreeUtil.setBlockIndex(file, thispage,(byte) cell1);
			BTreeUtil.setBlockIndex(file, overFlownPage, (byte) cell2);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void partitionLeaf(RandomAccessFile file, int page) throws IOException{
		int overFlownPage = BTreeUtil.convertPageToLeaf(file);
		int mid = ValidationUtil.centerPage(file, page);
		BTreeUtil.partitionLeafPage(file, page, overFlownPage);
		int ancestorPage = PageUtil.root(file, page);
		if(ancestorPage != 0){
			FileUtil.moveToFilePosition(file, FileUtil.currentFilePosition(file, page, ancestorPage), ancestorPage, overFlownPage);
			InsertUtil.addInnerBlock(file, ancestorPage, page, mid);
			InsertUtil.reArrangeCells(file, ancestorPage);
			int i=0;
			for(;ValidationUtil.isInnerAvailable(file, ancestorPage);i++) {
				ancestorPage = createInnerPartition(file, ancestorPage);
			}
		} else {
			int root = PageUtil.addAsInnerBlock(file);
			PageUtil.makeRoot(file, page, root);
			PageUtil.makeRoot(file, overFlownPage, root);
			PageUtil.makeLastPage(file, root, overFlownPage);
			InsertUtil.addInnerBlock(file, root, page, mid);
		}
	}
	
	
	public static int createInnerPartition(RandomAccessFile file, int page) throws IOException{
		int overFlownPage = PageUtil.addAsInnerBlock(file);
		int mid = ValidationUtil.centerPage(file, page);
		partitionInnerPage(file, page, overFlownPage);
		int ancestorPage = PageUtil.root(file, page);
		int divide = ancestorPage;
		if(ancestorPage != 0) {
			FileUtil.moveToFilePosition(file, FileUtil.currentFilePosition(file, page, ancestorPage), ancestorPage, overFlownPage);
			InsertUtil.addInnerBlock(file, ancestorPage, page, mid);
			InsertUtil.reArrangeCells(file, ancestorPage);
			divide = ancestorPage;
		} else {
			int rootPage = PageUtil.addAsInnerBlock(file);
			PageUtil.makeRoot(file, page, rootPage);
			PageUtil.makeRoot(file, overFlownPage, rootPage);
			PageUtil.makeLastPage(file, rootPage, overFlownPage);
			InsertUtil.addInnerBlock(file, rootPage, page, mid);
			divide = rootPage;
		}
		return divide;
	}
	
	public static void partitionInnerPage(RandomAccessFile file, int curPage, int overFlownPage){////
		try{
			int s = 4;
			int noOfCells = BTreeUtil.blockIndex(file, curPage);
			int mid = (int) Math.ceil((double) noOfCells / 2);
			int cell1 = mid - 1;
			int cell2 = noOfCells - cell1 - 1;
			short data = (short) FileUtil.pageSize;
			short size = (short) (s*2);
			byte[] c = new byte[size];
			int i = cell1+1;
			if(i<noOfCells) {
				do{
					data = (short)(data - size);
					file.seek(BTreeUtil.blockLocator(file, curPage, i));
					file.read(c);
					file.seek(data + PageUtil.getSeekPageOffSetForID(overFlownPage, 0));
					file.write(c);
					file.seek(BTreeUtil.blockLocator(file, curPage, i));
					PageUtil.makeRoot(file, file.readInt(), overFlownPage);
					BTreeUtil.blockOffSetter(file, overFlownPage, i-(cell1 + 1), data);
					i++;
				} while(i < noOfCells);
			}
			
			PageUtil.makeLastPage(file, overFlownPage,PageUtil.lastPage(file, curPage));
			
			file.seek(BTreeUtil.blockLocator(file, curPage, mid-1));
			
			PageUtil.makeLastPage(file, curPage, file.readInt());
			
			int addlen=2;
			int seeklen = addlen + (overFlownPage-1)*FileUtil.pageSize;
			file.seek(seeklen);
			file.writeShort(data);
			
			seeklen = addlen + (curPage-1)*FileUtil.pageSize; 
			file.seek(seeklen);
			file.writeShort(BTreeUtil.blockOffset(file, curPage, cell1-1));
			
			PageUtil.makeRoot(file, overFlownPage, PageUtil.root(file, curPage));
			
			BTreeUtil.setBlockIndex(file, curPage, (byte) cell1);
			BTreeUtil.setBlockIndex(file, overFlownPage, (byte) cell2);
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	private static int getNoOfPages(RandomAccessFile file) throws IOException {
		return (int)(file.length()/ (long) FileUtil.pageSize);
	}
	
}

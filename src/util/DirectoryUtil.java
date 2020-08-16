package src.util;

import java.io.File;

public class DirectoryUtil {
	private File directory;
	public DirectoryUtil(File directory) {
		this.directory =  directory;
	}
	public DirectoryUtil() {}

	public boolean deleteDirectory(File directory) {
		if(directory.isDirectory()) {
			File[] filelist = directory.listFiles(); 
			int i=0;
			while(i < filelist.length){
				boolean isDeleted = deleteDirectory(filelist[i++]); 
				if (!isDeleted) return false; 
			} 
		}
		return directory.delete();
	}
}

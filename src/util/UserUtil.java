package src.util;

public class UserUtil {
	public UserUtil(){}
	private int length;
	private String str;
	public UserUtil(int length, String str){
		this.length = length;
		this.str = str;
	}
	public String formatData() {
		return String.format("%-"+(length + 3) + "s", str);
	}
	
}

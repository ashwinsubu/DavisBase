package src.db;

import java.util.HashMap;

public interface StarterConstsInterface {

	HashMap<String, Byte> dataTypeMap = new HashMap<String, Byte>();
	HashMap<String, Byte> nullDataTypeMap = new HashMap<String, Byte>();
	public static byte[] payloadLen = {0x00, 0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08, 0x09, 0x0A, 0x0B};
	public static byte[] payLoadVals = {1, 2,4,8,1,2,4,8,4, 8, 8, 8};
	public static String[] davisbase_column_name = {"rowid", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
	public static HashMap<Byte,Short> davisbase_field_types_map = new HashMap<Byte, Short>();
	public static String[] data_type_array = {"TINYINT", "SMALLINT", "INT", "BIGINT", "REAL", "DOUBLE", "DATETIME","DATE","TEXT"};
	public static byte[] data_type_vals = {0x04, 0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C};
	public static byte[] null_data_type_vals = {0x00, 0x01,0x02,0x03,0x02,0x03,0x03,0x03,0x03};
	  
}
package src.db;
import static java.lang.System.out;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


import src.processor.Comparator;
import src.util.FileUtil;

public class DavisBase {
	/* This can be changed to whatever you like */
	static String prompt = "davisql> ";
	static String version = "v1.0b(example)";
	static String copyright = "©2016 Chris Irwin Davis";
	static boolean isExit = false;
	public static String getCopyright() {
		return copyright;
	}
	/** return the DavisBase version */
	public static String getVersion() {
		return version;
	}
	
	/*
	 * Page size for alll files is 512 bytes by default.
	 * You may choose to make it user modifiable
	 */
	public static final int pageSize = 512;
	
	/* 
	 *  The Scanner class is used to collect user commands from the prompt
	 *  There are many ways to do this. This is just one.
	 *
	 *  Each time the semicolon (;) delimiter is entered, the userCommand 
	 *  String is re-populated.
	 */
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");
	
	
    public static void main(String[] args) {
    	
    	/* Display the welcome screen */
    	splashScreen();
    	/* Variable to collect user input from the prompt */
		String userCommand = ""; 
		
		DavisBaseStarter.start();
		while(!isExit) {
			System.out.print(prompt);
			/* toLowerCase() renders command case insensitive */
			userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			parseUserCommand(userCommand);
		}
		System.out.println("Exiting...");
	}
	
    /**
	 *  Display the splash screen
	 */
	public static void splashScreen() {
		System.out.println(line("-",80));
        System.out.println("Welcome to DavisBaseLite"); // Display the string.
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("-",80));
	}
	
    /**
	 * @param s The String to be repeated
	 * @param num The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself num times.
	 */
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
	/**
	 *  Help: Display supported commands
	 */
	public static void help() {
		out.println(line("*",80));
		out.println("SUPPORTED COMMANDS\n");
		out.println("All commands below are case insensitive\n");
		out.println("SHOW TABLES;");
		out.println("\tDisplay the names of all tables.\n");
		out.println("DROP TABLE <table_name>;");
		out.println("\tRemove table data (i.e. all records) and its schema.\n");
		out.println("CREATE TABLE <table_name> (<column_name datatype>);\n");
		out.println("\tCreates new table in the database\n");
		out.println("INSERT INTO <table_name> VALUES (<value1>,<value2>,..);\n\tInsert new record into given table.\n");
		out.println("SELECT <column_list> FROM <table_name> [WHERE <condition>];");
		out.println("\tDisplay table records whose optional <condition>\n");
		out.println("VERSION;");
		out.println("\tDisplay the program version.\n");
		out.println("HELP;");
		out.println("\tDisplay this help information.\n");
		out.println("EXIT;");
		out.println("\tExit the program.\n");
		out.println(line("*",80));
	}

	public static boolean checkDuplicateTable(String tableName){
		tableName += ".tbl";
		boolean isExist = false;
		try {
			File dataDir = new File(FileUtil.dataFolderPath);
			for(String fileName : dataDir.list()) {
				if(tableName.equalsIgnoreCase(fileName)) {
					isExist = true; 
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("@@@ERROR::: Table already exists! Drop table "+ tableName+" to add it again::: " + e);
		}
		return isExist;
	}


	public static void parseUserCommand (String userCommand) {
		/* commandTokens is an array of Strings that contains one token per array element 
		 * The first token can be used to determine the type of command 
		 * The other tokens can be used to pass relevant parameters to each command-specific
		 * method inside each case statement */
		// String[] commandTokens = userCommand.split(" ");
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));

		/*
		*  This switch handles a very small list of hardcoded commands of known syntax.
		*  You will want to rewrite this method to interpret more complex commands. 
		*/
		switch (commandTokens.get(0)) {
			case "select":
				System.out.println("CASE: SELECT");
				parseQuery(userCommand);
				break;
			case "drop":
				System.out.println("CASE: DROP");
				DavisBaseDrop.dropTable(userCommand);
				break;
			case "create":
				System.out.println("CASE: CREATE");
				DavisBaseCreate.parseCreateTable(userCommand);
				break;

			case "help":
				help();
				break;
			case "version":
				getVersion();
				break;
			case "quit":
				isExit = true;
				
		    case "show":
		    	System.out.println("CASE: SHOW");
			    showTables();
			    break;
			    
			case "insert":
				System.out.println("CASE: INSERT");
				DavisBaseInsert.parseInsertString(userCommand);
				break;

			case "exit":
				isExit=true;
				break;
	
			default:
				System.out.println("I didn't understand the command: \"" + userCommand + "\"");
				System.out.println();
				break;
		}
	} 
	
	/**
	 *  Stub method for executing queries
	 *  @param queryString is a String of the user input
	 */
    public static void parseQuery(String queryString) {
    	System.out.println("STUB: This is the parseQuery method");
		System.out.println("\tParsing the string:\"" + queryString + "\"");
		
		String[] comparator,columnDetails, whereClause = queryString.split("where");
		if(whereClause.length <= 1){
			comparator = new String[0];
		} else {
			String equ = whereClause[1].trim();
			String t[] = new String[2];
			comparator = new String[3];
			
			String op = equ;
			comparator = new String[3];
			
			if(op.contains(Comparator.comparisonOperators.get(0))) {
				t = equ.split(Comparator.comparisonOperators.get(0));
				comparator[0] = t[0].trim();
				comparator[1] = Comparator.comparisonOperators.get(0);
				comparator[2] = t[1].trim();
			}
			if(op.contains(Comparator.comparisonOperators.get(1))) {
				t = equ.split(Comparator.comparisonOperators.get(1));
				comparator[0] = t[0].trim();
				comparator[1] = Comparator.comparisonOperators.get(1);
				comparator[2] = t[1].trim();
			}
			if(op.contains(Comparator.comparisonOperators.get(2))) {
				t = equ.split(Comparator.comparisonOperators.get(2));
				comparator[0] = t[0].trim();
				comparator[1] = Comparator.comparisonOperators.get(2);
				comparator[2] = t[1].trim();
			}
			if(op.contains(Comparator.comparisonOperators.get(3))) {
				t = equ.split(Comparator.comparisonOperators.get(3));
				comparator[0] = t[0].trim();
				comparator[1] = Comparator.comparisonOperators.get(3);
				comparator[2] = t[1].trim();
			}
			if(op.contains(Comparator.comparisonOperators.get(4))) {
				t = equ.split(Comparator.comparisonOperators.get(4));
				comparator[0] = t[0].trim();
				comparator[1] = Comparator.comparisonOperators.get(4);
				comparator[2] = t[1].trim();
			}
		}
		String table = whereClause[0].split("from")[1].trim();
		String cols = whereClause[0].split("from")[0].replace("select", "").trim();
		if(!"*".contains(cols)){
			columnDetails = cols.split(",");
			for(int i = 0; i < columnDetails.length;columnDetails[i] = columnDetails[i].trim(), i++);
		} else {
			columnDetails = new String[1];
			columnDetails[0] = "*";
		}
		
		if(!checkDuplicateTable(table)){
			System.out.println("@@@ERROR::: Table "+table+" does not exist.");
		} else {
		    DavisBaseSelect.selectCommand(table, columnDetails, comparator);
		}
	}
    
    public static void showTables() {
		System.out.println("SHOW METHOD");
		System.out.println("Parsing the string:\"show tables\"");
		String table = "davisbase_tables";
		String[] columns = {"table_name"};
		String[] comparator = new String[0];
		DavisBaseSelect.selectCommand(table, columns, comparator);
	}
	
}
		



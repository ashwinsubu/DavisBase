@Author
AXS190172
Ashwin Subramaniam
******************************************************************
____Steps to compile and run the program____
Extract axs190172.zip and change directory to DB/ folder
Run the following code:

javac src/db/DavisBase.java
java src.db.DavisBase

******************************************************************
______SUPPORTED COMMANDS_____

All commands below are case insensitive

SHOW TABLES;
	Display the names of all tables.

DROP TABLE <table_name>;
	Remove table data (i.e. all records) and its schema.

CREATE TABLE <table_name> (<column_name datatype>);

	Creates new table in the database

INSERT INTO <table_name> VALUES (<value1>,<value2>,..);
	Insert new record into given table.

SELECT <column_list> FROM <table_name> [WHERE <condition>];
	Display table records whose optional <condition>

VERSION;
	Display the program version.

HELP;
	Display this help information.

EXIT;
	Exit the program.

******************************************************************
____Software Requirements____

Java Version: 	  openjdk version "11.0.6"
Operating System: Linux or Ubuntu 

******************************************************************
____Sample Inputs_____

create table dogs ( id int, age smallint, name text );

insert into dogs ( id, age, name ) values (6, 9, "pug");

insert into dogs ( id, age, name ) values ( 12, 3, "pitbull" );

select * from dogs;

select age, name from dogs;

select * from dogs where id >= 6;

******************************************************************
______Note_______

-> Make sure to leave spaces between brackets "(" or ")" and other characters.
-> DavisBase takes first column as primary key by default
-> If NOT NULL is used along with PRIMARY KEY, use NOT NULL at the end 
-> In where clause the “=” operator works for all columns, however other operators 
  like >, >=, <, <= works only on first column since it is the primary key.

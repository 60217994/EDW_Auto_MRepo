package EDW.Edward;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.microsoft.sqlserver.jdbc.SQLServerError;
import com.microsoft.sqlserver.jdbc.SQLServerException;;


public class BaseClass {

	static Connection con;
	static Properties prop;
	
	String server = "CLDREDW-SIT3DB1.nswhealth.net";

	String	db = "EDW2";
	String	dzdb = "DZ";

	
	public void testBase()
	{
		try 
		{
			prop = new Properties();
			
			FileInputStream ip = new FileInputStream("C:\\Users\\60217994\\eclipse-workspace\\Edward\\src\\main\\java\\config.properties");
			prop.load(ip);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		
	}

//------------------------------------------------ Connection scripts --------------------------------------------------------
	
	/** This method opens a integrated SQL connection. */
	public void connOpen()
	{
		System.out.println("Server = " + server + " and Database = " + db);
		
		try 
		{
			//con =  DriverManager.getConnection("jdbc:sqlserver://" + server + ";"+"DatabaseName=" + db); //SQL authentication.
			// Integrated authentication.
			con =  DriverManager.getConnection("jdbc:sqlserver://" + server + ";"+"DatabaseName=" + db + ";"+ "integratedSecurity=true");
			//System.out.println("Connection is Open.");
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/** This method closes a integrated SQL connection. */
	public void connClose() throws SQLException
	{
		con.close();
		System.out.println("Connection has been closed.");
	}

//---------------------------------------------------- DQ scripts ---------------------------------------------------- 
	
	/**  This method will show all the DQ column for each table in a data stream. */
	public void DQcollInATable(int stream) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Select DISTINCT(TABLE_NAME) AS TABLE_NAME From INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = '" + db + "'" + 
				" AND TABLE_SCHEMA = 'STG' AND TABLE_NAME like '%" + stream +"'"  ;
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String tabname = rs.getString("TABLE_NAME");
			count = count+1;
			System.out.println(tabname + " , "+tabname);
		}
		
		System.out.println(count);
		
	 }
	
//------------------------------------- Container Processing and Re-processing scripts ------------------------------------------ 
	
	/**  This method will set CURRENT_FLAG to 'Y' and JOB_STATUS to '20' to aid re-processing a sequence container. */
	public void UpdateDZJobContToReprocess(int stream, String sourcesystemcode, int contseqnumb) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Update edw2.dz.DZ_JOB_CONTAINER SET CURRENT_FLAG = 'Y', JOB_STATUS = '20' where DATA_STREAM_ID = " + stream + 
				" AND SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb ;
		
		int count = ((java.sql.Statement) stmt).executeUpdate(sql);
		System.out.println("Number of rows updated : " + count);
		
	}
	
	/**  This method outputs EDW_CONTAINER_ID for a given container sequence number. */
	public int EdwContainerForASequence(int stream, String sourcesystemcode, int contseqnumb) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "select * from edw2.dz.DZ_JOB_CONTAINER where DATA_STREAM_ID = " + stream + 
				" AND SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb ;
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int datacontid = 0;
		while(rs.next()) 
		{
			datacontid = rs.getInt("DATA_CONTAINER_ID"); // getting DATA_CONTAINER_ID into variable
		}
		
		return datacontid;
	 }
	
	/**  This method outputs CURRENT_FLAG to and JOB_STATUS of given container sequence. */
	public void CheckJobStausANDCurrentFlag(int stream, String sourcesystemcode, int contseqnumb) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "select * from edw2.dz.DZ_JOB_CONTAINER where DATA_STREAM_ID = " + stream + 
				" AND SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb ;
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String jobstatus = rs.getString("JOB_STATUS");
			String currentflag = rs.getString("CURRENT_FLAG");
			count = count+1;
			System.out.println("JOB_STATUS in table is now : " + jobstatus + " CURRENT_FLAG in table is now : " + currentflag);
		}
	 }
	
	/**  This method outputs CURRENT_FLAG and JOB_STATUS of given container sequence. */
	public void UpdateCTLDataContToReprocess(int stream, String sourcesystemcode, int contseqnumb) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "UPDATE EDW2.CTL.CTL_DATA_CONTAINER "
				+ "SET DZ_TO_STG_STATUS_CD = 'ERROR', STG_TO_EDW_STATUS_CD = 'ERROR' "
				+ "where DATA_STREAM_ID = " + stream + " and SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb  ;
		int count = ((java.sql.Statement) stmt).executeUpdate(sql);

		System.out.println("Number of rows updated : " + count );
	}
	
	/**  This method outputs JOB_STATUS of given container sequence. */
	public void CheckStausInCTLDataCont(int stream, String sourcesystemcode, int contseqnumb) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "select TOP(1) * from EDW2.CTL.CTL_DATA_CONTAINER where DATA_STREAM_ID = " + stream + 
				" AND SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb 
				+ " ORDER BY DATA_CONTAINER_ID DESC";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String dtosstat = rs.getString("DZ_TO_STG_STATUS_CD");
			String stoestat = rs.getString("STG_TO_EDW_STATUS_CD");
			count = count+1;
			System.out.println("DZ_TO_STG_STATUS_CD is now : " + dtosstat + " , STG_TO_EDW_STATUS_CD is now : " + stoestat);
		}
	 }
	
	/**  This method will update CREATE_DATA_CONTAINER_ID to NULL for specified table in a stream for the given source system code. */
	public void updateCreateDataContForOneTable(String tablename, int stream, String sourcesystemcode, int contseqnumb) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Update DZ.DBO." + tablename + " SET CREATE_DATA_CONTAINER_ID = NULL WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb ;
		int count = ((java.sql.Statement) stmt).executeUpdate(sql);

		System.out.println("Number of rows updated : " + count);
		
	 }
	
	/**  This method will update CREATE_DATA_CONTAINER_ID to NULL for all tables of specified stream and source system code. */
	public void updateCreateDataContToNull(int stream, String sourcesystemcode, int contseqnumb) throws SQLException
	{
		
		if (stream == 23)
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = "Update DZ.DBO.SERVICE_EVENT_CHOC_MINIMUM_DATA_SET SET CREATE_DATA_CONTAINER_ID = NULL WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb ;
			long rowcount = ((java.sql.Statement) stmt).executeLargeUpdate(sql);
			System.out.println(rowcount + " rows updated in DZ.DBO.SERVICE_EVENT_CHOC_MINIMUM_DATA_SET table");
		}
		
		if (stream == 36)
		{
			List<String> dztables = Arrays.asList("PERINATAL_NOTIFICATION_MOTHER_AND_PREGNANCY", "PERINATAL_NOTIFICATION_NEWBORN_AND_BIRTH");
			Statement stmt = (Statement) con.createStatement();
			for(int i = 0; i < dztables.size(); i++)
			{
				String table =  dztables.get(i);
				String sql = "Update DZ.DBO."+ table +" SET CREATE_DATA_CONTAINER_ID = NULL WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb ;
				long rowcount = ((java.sql.Statement) stmt).executeLargeUpdate(sql);
				System.out.println(rowcount + " rows updated for " + table + " table");
			}
		}
		
		else
		{
			System.out.println("Please wait.... Updating all stream: "+ stream + " tables with Conatiner_Sequence_number:" + contseqnumb + " Data_Conatiner_Id to 'NULL'");
			ArrayList<String> dztables = DZTablesForAStream(stream);
			//System.out.println(dztables);
			for(int j = 0; j < dztables.size(); j++)
			{
				String table =  dztables.get(j) ;
					try 
					{  
						Statement stmt = (Statement) con.createStatement();
						String sql = "Update DZ.DBO." + table + " SET CREATE_DATA_CONTAINER_ID = NULL WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb ;
						long rowcount = ((java.sql.Statement) stmt).executeLargeUpdate(sql);
						System.out.println(rowcount + " rows updated for " + table + " table");
					}	
					catch (SQLException ignore) // Ignores any SQL exception as we want to ignore exceptions for tables like "CLIENT_GEO_BOUNDARY" which is not a DZ table
					{}
			}
		}
		
	}
	
	/**  This method returns next container sequence for given Stream and source. */
	public String getNextContSearchString(int stream, String sourcesystemcode) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "select TOP(1) * from EDW2.DZ.DZ_JOB_CONTAINER where DATA_STREAM_ID = " + stream + 
				" AND SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" 
				+ " ORDER BY DATA_CONTAINER_ID DESC";
		
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int contseq = 0;
		String searchstring = "";
		//System.out.println(rs.next());
		
		if (rs.next() == true) 
		{
			contseq = rs.getInt("CONTAINER_SEQUENCE_NUMBER");
			//String datacontid = rs.getString("DATA_CONTAINER_ID");
			//System.out.println("Current container sequence number is : " + contseq + " Its DATA_CONTAINER_ID is : " + datacontid);
			// for Stream 2, there are 0000 extra
			if (stream == 2)
			{
				searchstring = sourcesystemcode + "_" + stream + "_0000" + (contseq+1) + "_";
			}
			// for all other streams except 2
			searchstring = sourcesystemcode + "_" + stream + "_" + (contseq+1) + "_";
			System.out.println("String for searching next container is: " + searchstring);
		}
		else
		{
			System.out.println("No containers processed previously for source :" + sourcesystemcode);
			searchstring = "none";
		}
	
		return searchstring;
	 }
	
	/**  This method will run a job using job name. */
	public void runJob(String jobname) throws SQLException 
	{  
		//EXEC msdb.dbo.sp_start_job N'MyJobName';
		Statement stmt = (Statement) con.createStatement();
		try 
		{
			String sql = "EXEC msdb.dbo.sp_start_job N'" + jobname +"'";
			((java.sql.Statement) stmt).executeUpdate(sql);
		} 
		catch (SQLException e) //SQLServerException
		{
			e.printStackTrace();
			System.out.println("FIX: Open SQLServerAgent and re-run methods or run from agent manually.");
		}
		//System.out.println();
		
	 }
	
	/**  This method will copy containers to "DROP" folder for provided stream and source 
	 * @throws Exception */
	public void copyACoantinertoSIT3DropFolder(int streamid, String sourcesystemcode) throws SQLException, Exception
	{
		BaseClass bc = new BaseClass();
		String searchstr = bc.getNextContSearchString(streamid,sourcesystemcode);
		String[] splitarray = searchstr.split("_");
		String contseq = splitarray[2];
		//long lastrun =  System.currentTimeMillis();
		//System.out.println(lastrun);
	
		if (searchstr != "none")
		{   // Perform search for source files using search string.
			String filepath = null;
			String destfilepath = null ;
			try 
			{
				filepath = "V:\\"; 		// Make sure folders are open before running test
				destfilepath = "Z:\\DROP";
			} 
			catch (IllegalArgumentException exception) 
			{
				System.out.println("Please keep the paths \"V:\\\\\" AND \"Z:\\\\DROP\" open before running test" );
			}
	
			int count = 0;
	        try 
	        { 	
	        	System.out.println("Searching for files.... ");
	        	//@SuppressWarnings("unchecked") //This is to suppress a warning for below collection
	        	Collection<File> files = FileUtils.listFiles(new File(filepath), null, true);
	        	//int size =  files.size();
	        	
	        	// Look for source files 
	        	for (java.util.Iterator<File> iterator = files.iterator(); iterator.hasNext(); ) 
	        	{ 
	        		File file = iterator.next(); 
	        		if (file.getName().contains(searchstr)) 
	        		{     
	        			count = count + 1;
	        			File src = new File( file.getPath());
	        			File dest = new File( destfilepath );
	        			FileUtils.copyFileToDirectory(src, dest);
	        			// It looks for 20 files maximum for a stream.
	        			if (count == 25)
	        				{
	        					break;
	        				}
	        		}
	        	} 
	        	
	        	if (count == 0)
	    		{
	        		ArrayList<String> tables = bc.DZTablesForAStream(streamid);
	        		//System.out.println(tables);
	        		String table = tables.get(0);
	        		//System.out.print("Files for stream : " + streamid + " and source : " + sourcesystemcode + " Could not be found.");
	        		//con.close();
	        		try 
	        		{
	        			con =  DriverManager.getConnection("jdbc:sqlserver://" + server + ";"+"DatabaseName=" + dzdb + ";"+ "integratedSecurity=true");
	        		} 
	        		catch (SQLException e)
	        		{
	        			e.printStackTrace();
	        		}
	        		
	    			Statement stmtdz = (Statement) con.createStatement();
	    			String sqldz = "select COUNT(*) from DZ.DBO." + table + " where RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "'" + " AND CONTAINER_SEQUENCE_NUMBER =" + contseq;
	    			ResultSet rs = ((java.sql.Statement) stmtdz).executeQuery(sqldz);
	    			while(rs.next()) 
	    			{
	    				System.out.println("Files for stream : " + streamid + " and source : " + sourcesystemcode + " Could not be found. But records already exists in DZ for: " + table + " Conatiner.");
	    			}
	    		}
	        	
	        	else if (count > 0)
	    		{
	    			System.out.println("Files for stream : " + streamid + " and source : " + sourcesystemcode + " has been copied to DROP folder.");
	    		}
	        	 
	        } 
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		
		else
		{
			System.out.println("Loading files works only when there is an exising container loaded for specified stream");
		}
		
	} 
	
	/*
	 * public void copyACoantinertoSIT3DropFoldernew(int streamid, String
	 * sourcesystemcode) throws SQLException { BaseClass bc = new BaseClass();
	 * String searchstr = bc.getNextContSearchString(streamid,sourcesystemcode);
	 * 
	 * if (searchstr != "none") { // Perform search for source files using search
	 * string. String filepath = null; String destfilepath = null ; try { filepath =
	 * "V:\\"; // Make sure folders are open before running test destfilepath =
	 * "Z:\\DROP"; } catch (Exception e) { throw new java.lang.
	 * IllegalArgumentException("Please keep the paths \"V:\\\\\" AND \"Z:\\\\DROP\" open before running test"
	 * ); }
	 * 
	 * int count = 0; try { System.out.print(" Searching for files...");
	 * //@SuppressWarnings("unchecked") //This is to suppress a warning for below
	 * collection Collection<File> files = FileUtils.listFiles(new File(filepath),
	 * new String[]{"csv"}, true); //int size = files.size();
	 * 
	 * // Look for source files for (java.util.Iterator<File> iterator =
	 * files.iterator(); iterator.hasNext(); ) { File file = iterator.next(); if
	 * (file.getName().contains(searchstr)) { count = count + 1; File src = new
	 * File( file.getPath()); String folder = src.toString();
	 * System.out.println(folder ); System.out.println(folder.substring(3,18)); File
	 * dest = new File( destfilepath ); FileUtils.copyFileToDirectory(src, dest);
	 * //@SuppressWarnings("unchecked") Collection<File> files1 =
	 * FileUtils.listFiles(new File(filepath+folder.substring(3,18)), new
	 * String[]{"txt"}, true); for (java.util.Iterator<File> iterator1 =
	 * files1.iterator(); iterator1.hasNext(); ) if
	 * (file.getName().contains(searchstr)) { count = count + 1; File src1 = new
	 * File( file.getPath()); File dest1 = new File( destfilepath );
	 * FileUtils.copyFileToDirectory(src1, dest1); }
	 * 
	 * } if (count == 20) { break; } }
	 * 
	 * if (count == 0) { System.out.println("Could not find any files for stream = "
	 * + streamid + " and source = " + sourcesystemcode); }
	 * 
	 * else if (count > 0) { System.out.println("Files for stream = " + streamid +
	 * " and source = " + sourcesystemcode + " has been copied to DROP folder."); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * else { System.out.
	 * println("Loading files works only when there is an exising container loaded for specified stream"
	 * ); }
	 * 
	 * }
	 * 
	 */
//------------------------------------------ Parameter table scripts --------------------------------------------- 
	/**  This method will update PARAM_VAL for given PARAM_NAME in EDW2.CTL.CTL_PARAM table */
	public void setParamVal(String param_name, String param_val) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "UPDATE EDW2.CTL.CTL_PARAM SET PARAM_VAL = '" + param_val  +"' WHERE PARAM_NAME = '" + param_name + "'" ;
		((java.sql.Statement) stmt).executeUpdate(sql);
		
		Statement stmt1 = (Statement) con.createStatement();
		String sql1 = "SELECT * FROM EDW2.CTL.CTL_PARAM WHERE PARAM_NAME = '" + param_name + "'" ;
		ResultSet rs = ((java.sql.Statement) stmt1).executeQuery(sql1);
		while(rs.next()) 
		{
			String parname = rs.getString("PARAM_NAME");
			String parval = rs.getString("PARAM_VAL");
			System.out.println("PARAM_NAME : " + parname + " and PARAM_VAL : " + parval);
		}
		
	}
	
	
	/**  This method will update PARAM_VAL flag to "Y" in CTL.CTL_PARAM table for PARAM_NAME "AC_CYCLE_ACTIVE_FLAG"
	 *  also PARAM_VAL = 'N' for PARAM_NAME = 'DC_CYCLE_ACTIVE_FLAG' */
	public void setParamTableForACRun() throws SQLException
	{
		setParamVal("DC_CYCLE_ACTIVE_FLAG", "N");
		setParamVal("AC_CYCLE_ACTIVE_FLAG", "Y");
		/*Statement stmt = (Statement) con.createStatement();
		
		String sql = "UPDATE EDW2.CTL.CTL_PARAM SET PARAM_VAL = 'N' WHERE PARAM_NAME = 'DC_CYCLE_ACTIVE_FLAG'" ;
		((java.sql.Statement) stmt).executeUpdate(sql);
		
		String sql1 = "UPDATE EDW2.CTL.CTL_PARAM SET PARAM_VAL = 'Y' WHERE PARAM_NAME = 'AC_CYCLE_ACTIVE_FLAG'" ;
		((java.sql.Statement) stmt).executeUpdate(sql1); */
		
		System.out.println("Updated PARAM table for AC run ");
		
	}
	
	/**  This method will update PARAM_VAL flag to "Y" in CTL.CTL_PARAM table for PARAM_NAME "DC_CYCLE_ACTIVE_FLAG". */
	public void setParamTableForDCRun() throws SQLException
	{
		setParamVal("AC_CYCLE_ACTIVE_FLAG", "N");
		setParamVal("DC_CYCLE_ACTIVE_FLAG", "Y");

		/*
		// Updating DC_CYCLE_ACTIVE_FLAG to 'Y' in CTL_PARAM table.
		String sql = "UPDATE EDW2.CTL.CTL_PARAM SET PARAM_VAL = 'Y' WHERE PARAM_NAME = 'DC_CYCLE_ACTIVE_FLAG'" ;
		((java.sql.Statement) stmt).executeUpdate(sql);
		*/
		
		// Updating DC_START_TIME in CTL_PARAM table.
		java.util.Date date=new java.util.Date();  
		String time =(date.toString().substring(11,16));
		setParamVal("DC_START_TIME", time);
		
		/*
		// Updating AC_CYCLE_ACTIVE_FLAG to 'N' in CTL_PARAM table.
		String sql1 = "UPDATE EDW2.CTL.CTL_PARAM SET PARAM_VAL = 'N' WHERE PARAM_NAME = 'AC_CYCLE_ACTIVE_FLAG'" ;
		((java.sql.Statement) stmt).executeUpdate(sql1); */
		
		System.out.println("Updated PARAM table for DC run ");
	
	}
	
	/**  This method will update PARAM_VAL flag to "Y" in CTL.CTL_PARAM table for PARAM_NAME "PLP", "PROV" and "DIST". */
	public void setParamTableForPlpProvDist() throws SQLException
	{
		String[] paramtype = {"PLPRunFlag", "PROVRunFlag", "DISTRunFlag"};
		
		for(int i=0; i< paramtype.length; i++)
		{
			setParamVal(paramtype[i], "Y");
		}
		
		System.out.println("Updated PARAM_VAL flag to \"Y\" for PARAM_NAME \"PLPRunFlag\", \"PROVRunFlag\" and \"DISTRunFlag\".");
	}
	
	/**  This method will update PARAM_VAL flag to "Y" in CTL.CTL_PARAM table for PARAM_NAME "Initial Load" and sets all containers to ERROR in ctl.ctl_DC_JOB for PROV*/
	public void setParamTableForIntialloadAndUpdateCtl_Dc_JobTable() throws SQLException
	{
		setParamVal("InitialLoad", "Y");
		
		//sets all containers to ERROR in ctl.ctl_DC_JOB for PROV(DP2.2*)
		Statement stmt = (Statement) con.createStatement();
		String sql1 = "UPDATE CTL.CTL_DC_JOB SET STATUS_CD = 'ERROR' WHERE DC_JOB_CD LIKE  '%DP2.2%'" ;
		((java.sql.Statement) stmt).executeUpdate(sql1);
		
		System.out.println("Updated CTL.CTL_PARAM table for Initial Load and also set all jobs in DC to \"ERROR\" ");
	}

//-----------------------------------------------Data Dictionary Methods-----------------------------------------------
	
	/**  This method will return all DZ tables for a stream entered as parameter. **/
		public ArrayList<String> DZTablesForAStream(int stream) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = null;
			if(stream < 10)
			{
				sql = "SELECT DISTINCT(TABLE_NAME), SUBSTRING(TABLE_NAME,7, (LEN(TABLE_NAME)-8)) AS 'DZTABLE'  FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME LIKE 'STAGE_%@_" + stream + "' ESCAPE '@'";
			//String sql = "SELECT DISTINCT(TABLE_NAME), TABLE_ABBR FROM Factory.StagingColumnDataDictionary  WHERE DATA_STREAM_ID = " + stream ;
			}
			else
			{
				sql = " SELECT DISTINCT(TABLE_NAME), SUBSTRING(TABLE_NAME,7, (LEN(TABLE_NAME)-9)) AS 'DZTABLE'  FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME LIKE 'STAGE_%@_" + stream + "' ESCAPE '@'";
			}
			
			ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
			int count = 0;
			ArrayList<String> dztables = new ArrayList<String>();
			while(rs.next()) 
			{
				String tabnamedz = rs.getString("DZTABLE");
				count = count+1;
				//System.out.println( "table name in DZ : "+ tabname + ");
				dztables.add(tabnamedz);
			}

			//System.out.println("DZ tables : " + dztables);
			count = 0;
			return dztables;
		 }
		
	/**  This method will return all core tables for a stream entered as parameter. **/
	/*	public ArrayList<String> coreTablesForAStreamold(int stream) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = null;
			if(stream < 10)
			{
				sql = "SELECT DISTINCT(TABLE_NAME), TABLE_ABBR FROM Factory.StagingColumnDataDictionary WHERE DATA_STREAM_ID = " + stream 
				+ " AND TABLE_NAME IN (SELECT SUBSTRING(TABLE_NAME,7, (LEN(TABLE_NAME)-8)) AS 'DZTABLE'  FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME LIKE 'STAGE_%@_" + stream + "' ESCAPE '@')";
			}
			//String sql = "SELECT DISTINCT(TABLE_NAME), TABLE_ABBR FROM Factory.StagingColumnDataDictionary  WHERE DATA_STREAM_ID = " + stream ;
			else
			{
			    sql = "SELECT DISTINCT(TABLE_NAME), TABLE_ABBR FROM Factory.StagingColumnDataDictionary WHERE DATA_STREAM_ID = " + stream 
				+ " AND TABLE_NAME IN (SELECT SUBSTRING(TABLE_NAME,7, (LEN(TABLE_NAME)-9)) AS 'DZTABLE'  FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME LIKE 'STAGE_%@_" + stream + "' ESCAPE '@')";
			}
			
			ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
			int count = 0;
			ArrayList<String> coretables = new ArrayList<String>();
			
			while(rs.next()) 
			{
				String tabnamecore = rs.getString("TABLE_ABBR");
				count = count+1;
				//System.out.println( "table name in Core is : " + tabnamecore);
				coretables.add(tabnamecore);
			}
			//System.out.println("coretables : " + coretables);
			count = 0;
			return coretables;
		 } */
		
		/**  This method will return all core tables for a stream entered as parameter. **/
		public ArrayList<String> coreTablesForAStream(int stream) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = null;
			ArrayList<String> dztables = DZTablesForAStream(stream);
			ArrayList<String> coretables = new ArrayList<String>();
			int len = dztables.size();
			int count = 0;
			for(int i=0; i< len; i++)
			{
				sql = "SELECT DISTINCT(TABLE_ABBR) FROM Factory.StagingColumnDataDictionary WHERE DATA_STREAM_ID = " + stream + " AND TABLE_NAME = '" + dztables.get(i) + "'";
				ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
				while(rs.next()) 
				{
					String tabnamecore = rs.getString("TABLE_ABBR");
					count = count+1;
					//System.out.println( "table name in Core is : " + tabnamecore);
					coretables.add(tabnamecore);
				}
			}
			//System.out.println("coretables : " + coretables);
			return coretables;
		 }
		
		/**  This method will return CBK for given DZ table. **/
		public String findCbkForaTableInDZ(String table) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = null;
			sql = "SELECT DISTINCT(COLUMN_ABBR), COLUMN_NAME, CONVERT(int,ORDINAL_POSITION) AS ORDINAL_POSITION  FROM factory.EDWColumnDataDictionary WHERE TABLE_NAME = '"+ table +"' AND WILL_BE_PK= 'Y' GROUP BY COLUMN_NAME, COLUMN_ABBR, ORDINAL_POSITION ORDER BY ORDINAL_POSITION DESC";
		 	ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
			String cbkv = new String();
			String cbk = new String();
				try {
						while(rs.next())
						{
							String cbkcols =rs.getString("COLUMN_NAME");
							cbkv = cbkcols + " + " + cbkv;
						}
						cbk = cbkv.substring(0, cbkv.lastIndexOf("+"));
						cbk = cbk.trim();
				   } 
				catch (Exception ignore) {}
				
			return cbk;
		 }
		
		/**  This method will return CBK for given CORE table. **/
		public String findCbkForaTableInCore(String coretable) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = "SELECT DISTINCT(COLUMN_ABBR), COLUMN_NAME, CONVERT(int,ORDINAL_POSITION) AS ORDINAL_POSITION  FROM factory.EDWColumnDataDictionary WHERE TABLE_ABBR = '"+ coretable +"' AND WILL_BE_PK= 'Y' GROUP BY COLUMN_NAME, COLUMN_ABBR, ORDINAL_POSITION ORDER BY ORDINAL_POSITION DESC";
		 	ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		 	//System.out.println(sql);
			String cbkv = new String();
			String cbk = new String();
				try {
						while(rs.next())
						{
							String cbkcols =rs.getString("COLUMN_ABBR");
							cbkv = cbkcols + " + " + cbkv;
						}
						cbk = cbkv.substring(0, cbkv.lastIndexOf("+"));
						cbk = cbk.trim();
				   } 
				catch (Exception ignore) {}
				
			return cbk;
		 }
		
		/**  This method will compare COLUMN_NAME , DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION and NUMERIC_SCALE as part of meta data tests for a stream between Core tables and factory.EDWColumnDataDictionary.(source of truth) */
		public void comparefactoryToCore(int stream) throws SQLException
		{
			Statement stmt1 = (Statement) con.createStatement();
			Statement stmt2 = (Statement) con.createStatement();
			// Query on Factory tables
			String sqlf = "SELECT DISTINCT(TABLE_NAME), COLUMN_NAME , DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, TABLE_ABBR, COLUMN_ABBR,  IS_NULLABLE"
					+ " FROM factory.EDWColumnDataDictionary WHERE DATA_STREAM_ID = " + stream + " AND WILL_COLUMN_BE_USED = 'Y'";
			ResultSet rsf = ((java.sql.Statement) stmt1).executeQuery(sqlf);
		
			int count = 0;
			while(rsf.next()) 
			{	
				//Dz var's for feeding into Core table query
				String tabnamecore = rsf.getString("TABLE_ABBR");
				String colnamecore = rsf.getString("COLUMN_ABBR");
				
				String tabnamedz = rsf.getString("TABLE_NAME");
				String colnamedz = rsf.getString("COLUMN_NAME");
				
				String datatypedz =rsf.getString("DATA_TYPE");
				String lengthdz = rsf.getString("CHARACTER_MAXIMUM_LENGTH");
				String numprecdz = rsf.getString("NUMERIC_PRECISION");
				String isnullabledz = rsf.getString("IS_NULLABLE");
				
				count = count+1;
				
				
				String sqlc = "SELECT TABLE_NAME, COLUMN_NAME , DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE,  IS_NULLABLE " + 
						" FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = " + "'"+ tabnamecore + "'"  + " AND COLUMN_NAME = "+ "'"+ colnamecore+ "'";
				ResultSet rsc = ((java.sql.Statement) stmt2).executeQuery(sqlc);
				
				while (rsc.next()) 
				{
					//Core vars
					String datatypecore =rsc.getString("DATA_TYPE");
					String lengthcore = rsc.getString("CHARACTER_MAXIMUM_LENGTH");
					String numpreccore = rsc.getString("NUMERIC_PRECISION");
					String isnullablecore = rsc.getString("IS_NULLABLE");
					
					//Comparing data type between DZ and CORE
					String datatype = "";
					if (Objects.equals(datatypedz, datatypecore))
					{
						datatype = "Equal";
					}
					else datatype = "Not Equal";
					
					//Comparing length between DZ and CORE
					String datalength = "";
					if (Objects.equals(lengthdz, lengthcore))
					{
						datalength = "Equal";
					}
					else datalength = "Not Equal";
					
					//Comparing numeric precision between DZ and CORE
					String numprec = "";
					if (Objects.equals(numprecdz, numpreccore))
					{
						numprec = "Equal";
					}
					else numprec = "Not Equal";
					
					//Comparing is nullable between DZ and CORE
					String isnullable = "";
					if (Objects.equals(isnullabledz, isnullablecore))
					{
						isnullable = "Equal";
					}
					else isnullable = "Not Equal";

					System.out.println( tabnamedz + ", " + colnamedz+ ", " + datatypedz+ ", " + lengthdz+ ", " + numprecdz + ", " + isnullabledz+ ", " + tabnamecore+ ", " + colnamecore + ", " + datatypecore+ ", " +lengthcore+ ", " +numpreccore+ ", " + isnullablecore+ ", "+ datatype +  ", " +datalength +  ", "+ numprec + ", " + isnullable);
					
				}
				
			}

			System.out.println("no of Columns : " + count);
			
			count = 0;
		 }
		
		/** This method will generate scripts for counts test for specified Stream **/
		//@SuppressWarnings("null")
		public void generateCountsScripts(int stream, String sourcesystemcode, int contseqnumb) throws SQLException
		{
			// to Know cbk, use WILL_BE_PK = Y AND sort table by ORDINAL_POSITION
			//SELECT * FROM factory.EDWColumnDataDictionary WHERE TABLE_NAME = 'CLIENT' AND WILL_BE_PK= 'Y' ORDER BY COLUMN_SEQ
			BaseClass bc = new BaseClass();
			int datacontid = bc.EdwContainerForASequence(stream, sourcesystemcode, contseqnumb);
			ArrayList<String> dztables = DZTablesForAStream(stream);
			
			for(int i = 0; i< dztables.size(); i++) 
			{
				Statement stmt = (Statement) con.createStatement();
				// Query on Factory tables
				String sql = "SELECT * FROM factory.EDWColumnDataDictionary WHERE TABLE_NAME = '" + dztables.get(i) + "' AND WILL_BE_PK= 'Y' ORDER BY CONVERT(int, ORDINAL_POSITION)";
				ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
				//ArrayList<String> cbk = new ArrayList<String>();
				String cbkv = new String();
				
				while(rs.next())
				{
					String cbkcols =rs.getString("COLUMN_NAME");
					cbkv = cbkcols + " + " + cbkv;
							//cbk.add(cbkcols);
				}
				String cbk = new String();
				cbk = cbkv.substring(0, cbkv.lastIndexOf("+"));
				
				String query =new String();
			    query = "SELECT '" + dztables.get(i) + "' AS TABLE_NAME,COUNT(DISTINCT " + cbk + ") AS TOTAL_COUNT FROM DZ.DBO." + dztables.get(i) + " WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "' AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb;
			    
			    // prints Comment "--DZ Tables Queries" word after every Query except for last query
			    if(i == 0 )
					{
			    		System.out.println("    ");
				    	System.out.println("--DZ Tables Queries"); //(dztables.get(i) + ":" + cbk);
					}
			    
			    // Prints Query.
				System.out.println(query);
				
				// prints "UNION" word after every Query except for last query
				if(i != dztables.size() -1 )
					{
						System.out.println("UNION");
					}
				cbkv = "";
				cbk = "";
			} 
			
			// For Core tables
			ArrayList<String> coretables = coreTablesForAStream(stream);

			for(int i = 0; i< coretables.size(); i++) 
			{
				Statement stmt1 = (Statement) con.createStatement();
				// Query on Factory tables
				String sql1 = "SELECT * FROM factory.EDWColumnDataDictionary WHERE TABLE_NAME = '" + dztables.get(i) + "' AND WILL_BE_PK= 'Y' ORDER BY COLUMN_SEQ DESC";
				ResultSet rs = ((java.sql.Statement) stmt1).executeQuery(sql1);
				//ArrayList<String> cbk = new ArrayList<String>();
				String cbkv = new String();
				while(rs.next())
				{
					String cbkcols =rs.getString("COLUMN_ABBR");
					cbkv = cbkcols + " + " + cbkv;
				}
				String cbk = new String();
				cbk = cbkv.substring(0, cbkv.lastIndexOf("+"));
				
				String query =new String();
			    query = "SELECT '" + coretables.get(i) + "' AS COLUMN_ABBR, COUNT(DISTINCT " + cbk + ") AS TOTAL_COUNT FROM " + coretables.get(i) + " WHERE REC_SRC_SYS_CD = '" + sourcesystemcode + "' AND EDW_DATA_CONTAINER_ID = " + datacontid;
			    
			    // prints Comment "--DZ Tables Queries" word after every Query except for last query
			    if(i == 0 )
					{
			    		System.out.println("    ");
				    	System.out.println("--CORE Tables Queries"); //(dztables.get(i) + ":" + cbk);
					}
			    
			    // Prints Query.
				System.out.println(query);
				
				// prints "UNION" word after every Query except for last query
				if(i != dztables.size() -1 )
					{
						System.out.println("UNION");
					}
			} 
		}
		
		//@SuppressWarnings("null")
		public void countsMatchingbetweenDZandCore(int stream, String sourcesystemcode, int contseqnumb) throws SQLException
		{
			int datacontid = EdwContainerForASequence(stream, sourcesystemcode, contseqnumb);
			ArrayList<String> dztables = DZTablesForAStream(stream);
			//System.out.println(dztables);
			ArrayList<String> coretables = coreTablesForAStream(stream);
			//System.out.println(coretables);
			Statement stmt1 = (Statement) con.createStatement();
			Statement stmt2 = (Statement) con.createStatement();
			String cbkdz = null;
			String cbkc = null;
			
			for(int i = 0; i< dztables.size(); i++) 
			{
				cbkdz = findCbkForaTableInDZ(dztables.get(i));
				//System.out.println(cbkdz);
				String query1 = "SELECT COUNT(DISTINCT " + cbkdz + ") AS DZ_COUNT FROM DZ.DBO." + dztables.get(i) + " WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "' AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb;
			    //query1 = "SELECT '" + dztable + "' AS TABLE_NAME, COUNT(DISTINCT " + cbkdz + ") AS DZ_COUNT FROM DZ.DBO." + dztables.get(i) + " WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "' AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb;
			    ResultSet rsd = ((java.sql.Statement) stmt1).executeQuery(query1);
			    
			    while (rsd.next())
				    {
				    	int tablecount = rsd.getInt("DZ_COUNT");
				    	String coretable = coretables.get(i);
				    	//System.out.println(coretable);
				    	cbkc = findCbkForaTableInCore(coretable);
				    	//System.out.println(cbkc);
				    	String queryc = "SELECT COUNT(DISTINCT(" + cbkc + ")) AS CORE_COUNT FROM [" + coretable + "] WHERE REC_SRC_SYS_CD = '" + sourcesystemcode + "' AND EDW_DATA_CONTAINER_ID = " + datacontid;
				    	//System.out.println(queryc);
				    	ResultSet rsc = ((java.sql.Statement) stmt2).executeQuery(queryc);
				    	while (rsc.next())
					    {
						    int tablecountc = rsc.getInt("CORE_COUNT");
						    
						    if(tablecount == tablecountc)
							{
						    	System.out.println("Counts matched between " + dztables.get(i) + " - " + tablecount + " and " + coretable + " - " + tablecountc);
							}	
						    
						    else
						    {
						    	System.out.println("Counts did not match between " + dztables.get(i) + " - " + tablecount + " and " + coretable + " - " + tablecountc + " , please use below Intersect and Except Quries for analysis.");
						    	System.out.println("");
							    System.out.println("SELECT DISTINCT (" + cbkdz + ") FROM DZ.dbo."+ dztables.get(i) + " WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "' AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb);
							    System.out.println("EXCEPT");
							    System.out.println("SELECT DISTINCT (" + cbkc + ") FROM "+ coretable + " WHERE REC_SRC_SYS_CD = '" + sourcesystemcode + "' AND EDW_DATA_CONTAINER_ID = " + datacontid);
							    System.out.println("INTERSECT");
							    System.out.println("SELECT DISTINCT (" + cbkdz + ") FROM DZ.dbo."+ dztables.get(i) + " WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "' AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb);
							    System.out.println("");
						    }
					    }
				    }
			}
		}		

	
//------------------------------------------------ Meta data scripts ------------------------------------------------
	
	/**  This method outputs CHARACTER_MAXIMUM_LENGTH for given table. */
	public void MaxlengthForTable(String tablename, String schema,String umnnames) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "SELECT TABLE_NAME, COLUMN_NAME, CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + tablename + "'AND"
		+ " TABLE_SCHEMA = '" + schema + "'" + " AND COLUMN_NAME IN = " //+ collumnnames 
				+ " ORDER BY DATA_CONTAINER_ID DESC";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String dtosstat = rs.getString("DZ_TO_STG_STATUS_CD");
			String stoestat = rs.getString("STG_TO_EDW_STATUS_CD");
			count = count+1;
			System.out.println("DZ_TO_STG_STATUS_CD is now : " + dtosstat);
			System.out.println("STG_TO_EDW_STATUS_CD is now : " + stoestat);
		}
	 }	
	
	/**  This method counts no of columns in a table. */
	public void collCount(String tablename) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Select COUNT(*) AS Count_of_Columns From INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = '" + db + "'" + 
				" AND TABLE_NAME = '" + tablename + "'";
		
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		while(rs.next()) 
		{
			String collcount = rs.getString("Count_of_Columns");
			System.out.println("number of column = " + collcount);
		}
	    
	}
	
	/**  This method will show all the column headers in a table. */
	public void collHeadersIntable(String tablename) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Select COLUMN_NAME AS Columns_headers From INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = '" + db + "'" + 
				" AND TABLE_NAME = '" + tablename + "'";
		
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String collname = rs.getString("Columns_headers");
			count = count+1;
			System.out.println(collname);
		}
		
		System.out.println(count);
		
	 }
	
	/**  This method will show all the column headers matching 'Like' statement in a table. */
	public void collHeadersIntableLike(String tablename, String searchstring) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Select COLUMN_NAME AS Columns_headers From INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = '" + db + "'" + 
				" AND TABLE_NAME = '" + tablename + "'" + "AND COLUMN_NAME Like '%" + searchstring + "%" + "'";
		
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String collname = rs.getString("Columns_headers");
			count = count+1;
			System.out.println(collname);
		}
		
		System.out.println(count);
		
	 }
	
	/**  This method will show all the column headers matching 'Like' statement in a database. */
	public void collHeadersInDBLike(String searchstring) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Select COLUMN_NAME AS Columns_headers From INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = '" + db + "'" + 
				" AND TABLE_SCHEMA = 'dbo' AND COLUMN_NAME Like '" + "%" + searchstring + "%" + "'" ;
		
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String collname = rs.getString("Columns_headers");
			count = count+1;
			System.out.println(collname);
		}
		
		System.out.println(count);
	    
	 }
	
	/**  This method will show all the column headers in a database. */
	public void collHeadersInDB(String tablename) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Select COLUMN_NAME AS Columns_headers From INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = '" + db + "'" + 
				" AND TABLE_NAME = '" + tablename + "'";
		
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String collname = rs.getString("Columns_headers");
			count = count+1;
			System.out.println(collname);
		}
		
		System.out.println(count);
		
	 }
	
	/**  This method will show all the column headers in a schema. */
	public void collHeadersInSchema(String schema) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Select COLUMN_NAME AS Columns_headers From INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = '" + db + "'" + 
				" AND TABLE_SCHEMA = '" + schema + "'";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String collname = rs.getString("Columns_headers");
			count = count+1;
			System.out.println(collname);
		}
		
		System.out.println(count);
		
	 }
	
	/**  This method will show all the column headers in a Schema and table. */
	public void collHeadersInSchemaWithTable(String schema, String searchstring) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "Select COLUMN_NAME, TABLE_NAME From INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = '" + db + "'" + 
				" AND TABLE_SCHEMA = '" + schema + "'" + " AND COLUMN_NAME Like '" + "%" + searchstring + "%" + "'"  ;
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		int count = 0;
		while(rs.next()) 
		{
			String collname = rs.getString("COLUMN_NAME");
			String tabname = rs.getString("TABLE_NAME");
			count = count+1;
			System.out.println(collname + " , "+tabname);
		}
		
		System.out.println(count);
		
	 }
			
// ---------------------------------------------------Logical deletes-----------------------------------------------
	/**  This method will update 10(or less) records in a container to Logical_Delete = 'Y' in the given table. **/
	public void updateSomeRecordsAsLogicalDeletesInAContainer(String sourcesystemcode, int contseqnumb, String table) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "select * from DZ.DBO." + table + " where RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + 
			"' AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb + "AND ACTION_TYPE != 'D'";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		ArrayList<String> encidlist = new ArrayList<String>();
		int count = 0;
		//int count1 = 0;
		
		// List of all Encounter id's.
		while(rs.next() && count < 10) 
		{
			// Pick each Encounter_ID
			String encid = rs.getString("SERVICE_ENCOUNTER_RECORD_ID");
			count = count+1;
			//System.out.println(encid);
			// adding each Encounter_ID to list
			encidlist.add(encid);
		}

		//System.out.println("Random Encounters : " + encidlist);
		
		// SQL update all rows with the Encounter id's in the encidlist to Logical delete "Y"
		for(int i=0; i < encidlist.size(); i++)
		{
			Statement stmt1 = (Statement) con.createStatement();
			String sql1 = "UPDATE DZ.DBO." + table + " SET ACTION_TYPE = 'D' where SERVICE_ENCOUNTER_RECORD_ID = '" + encidlist.get(i)+
			"' AND RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "' AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb + "AND ACTION_TYPE != 'D'";
			((java.sql.Statement) stmt1).executeUpdate(sql1);
		}

		System.out.println("Updated " + count + " records with ACTION_TYPE = 'D' for following SERVICE_ENCOUNTER_RECORD_ID" + encidlist );
		
		/*while(rs1.next() && count1<=10 ) 
		{
			// Pick each Encounter_ID
			String encid = rs.getString("SERVICE_ENCOUNTER_RECORD_ID");
			count1 = count+1;
			System.out.println(encid);
			// adding each Encounter_ID to list
			encidlist.add(encid);
		} */
		
		count = 0;
		
	 }
	
	//-------------------------------------------AGRO LOAD---------------------------------------------------

//	Run File Handler for next container
	
//	Pick records which are not already in the existing containers
//	SELECT * FROM DZ.DBO.CLIENT_SERVICE_EVENT WHERE CONTAINER_SEQUENCE_NUMBER = 3323 AND RECORD_SOURCE_SYSTEM_CODE = '7448-003'
//	AND CLIENT_ID_TYPE_CODE + '|' + CLIENT_ID_ISSUING_AUTHORITY + '|' + CLIENT_ID NOT IN 
//	(SELECT (CL_ID_TYP_CD+ '|' + CL_ID_ISSUING_AUTH + '|' + CL_ID) FROM CL_ID)
	
//	Run AC to see AGRO loads.
	
	
	
	/**  This method will return all SK's in a table. */
	public ArrayList<String> sksInACoreTable(String tablename) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"+ tablename + "' AND COLUMN_NAME LIKE '%SK' AND ORDINAL_POSITION != 1";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		System.out.println(rs);
		
		ArrayList<String> sklist = new ArrayList<String>();
		
		while(rs.next()) 
			{
				String sk = rs.getString("COLUMN_NAME");
				//System.out.println(sk);
				sklist.add(sk);
			}
		System.out.println(sklist);
		return sklist;
	 }
			
	
	
	
}//Class

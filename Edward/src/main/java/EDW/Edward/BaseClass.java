package EDW.Edward;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;;


public class BaseClass {

	static Connection con;
	//static Properties prop;
	
	String server = "CLDREDW-SIT3DB1.nswhealth.net";
	Properties prop = new Properties();

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
			// Integrated authentication.
			con =  DriverManager.getConnection("jdbc:sqlserver://" + server + ";"+"DatabaseName=" + db + ";"+ "integratedSecurity=true");
			//System.out.println("Connection is Open.");
		} 
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	/** This method opens a integrated SQL connection. */
	public void connOpenSqlAuth(String db, String un , String pwd)
	{
		System.out.println("Server = " + server + " and Database = " + db);
		
		try 
		{
			con =  DriverManager.getConnection("jdbc:sqlserver://" + server + ";"+"DatabaseName=" + db, un ,pwd); //SQL authentication.
			System.out.println("Connection is Open.");
		} 
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/** This method closes a integrated SQL connection. */
	public void connClose() throws SQLException
	{
		con.close();
		System.out.println("Connection has been closed.");
	}

//---------------------------------------------------- Executing SQL script ---------------------------------------------------- 	
	
	/**  This method Execute a sql select statement */
	public ResultSet executeSqlSelect(String sql) throws SQLException
	{	
		ResultSet rs = null;
		try 
		{
			Statement stmt = (Statement) con.createStatement();
			rs = ((java.sql.Statement) stmt).executeQuery(sql);
		}
		catch (Exception e) 
		{
            System.out.println(e.getMessage());
        }
		return rs;
	 }
	
	/**  This method Execute a sql select statement for Counts */
	public int countsForATable(String tablename) throws SQLException
	{
		int count = 0;
		try 
		{
			ResultSet rs = executeSqlSelect("SELECT COUNT(*) AS count FROM " + tablename);
			while(rs != null && rs.next())
			{
				count = rs.getInt(1);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
            //System.out.println(e.getMessage());
        }
		return count;
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
			System.out.println("Please wait.... Updating all stream "+ stream + " tables with Conatiner_Sequence_number = " + contseqnumb + " to 'NULL' in CREATE_DATA_CONTAINER_ID column");
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
			
			if (stream == 2) // for Stream 2, there are 0000 extra
				{
					searchstring = sourcesystemcode + "_" + stream + "_0000" + (contseq+1) + "_";
				}
			
			else // for all other streams except 2
				{
				searchstring = sourcesystemcode + "_" + stream + "_" + (contseq+1) + "_";
				}
			System.out.println("String for searching next container is: " + searchstring);
		}
		else
		{
			System.out.println("No containers processed previously for source :" + sourcesystemcode + " for Stream: " + stream);
			searchstring = "none";
		}
	
		return searchstring;
	 }
	
	/**  This method will copy containers to "DROP" folder for provided stream and source 
	 * @throws Exception */
	public void copyACoantinertoSIT3DropFolder(int streamid, String sourcesystemcode) throws SQLException, Exception
	{
		BaseClass bc = new BaseClass();
		
			String searchstr = bc.getNextContSearchString(streamid,sourcesystemcode);
			//long lastrun =  System.currentTimeMillis();
			//System.out.println(lastrun);
		
		if (searchstr != "none")
		{   
			String[] splitarray = searchstr.split("_");
			String contseq = splitarray[2];
			
			// Perform search for source files using search string.
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
	        		}
	        	} 
	        	
	        	if (count == 0)
	    		{
	        		ArrayList<String> tables = bc.DZTablesForAStream(streamid);
	        		//System.out.println(tables);
	        		String table = tables.get(0);
	        		
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
	    				System.out.print("Files for stream :" + streamid + " and source :" + sourcesystemcode + " and cont_seq :"+ contseq +" Could not be found. But records already exists in DZ for: " + table + " Container.");
	    				System.out.println();
	    			}
	    		}
	        	
	        	else if (count > 0)
	    		{
	    			System.out.print("Files for stream :" + streamid + " , source :" + sourcesystemcode + " and cont_seq :"+ contseq +" has been copied to DROP folder.");
	    		}
	        	 
	        } 
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		
		else
		{
			System.out.println("Loading files works only when there is an exising container loaded for specified stream");
			System.out.println();
		}
		
	} 
	
	/**  This method will create flag files in the "FTPDROP" folder. -- Not Complete
	 * @throws Exception */
	public void createFLGFileInFTPFolder(int streamid) throws SQLException, Exception
	{
		String filepath = null;
		
		if(streamid == 55)
		{
			filepath = "Z:\\FTPDROP\\CHAMB";
		}
		
		else if(streamid == 56)
		{
			filepath = "Z:\\FTPDROP\\MHOAT";
		}
		
		File np1 = new File("Z:\\FTPDROP\\CHAMB");
		np1.createNewFile();
		
		Collection<File> files = FileUtils.listFiles(new File(filepath), null, true);
    	
    	for (java.util.Iterator<File> iterator = files.iterator(); iterator.hasNext(); ) 
    	{ 
    		File file = iterator.next(); 
    		String filename = file.getName();
    		System.out.println(filename);
    		String flgfilename = filename.replace("dat", "flg");
    		
    		System.out.println(filepath);
    		System.out.println(flgfilename);
    		
    		//np.renameTo(filename + ".flg");
    		
    	} 
		
	}
	
	/**  This method will insert "-1" container into edw2.dz.DZ_JOB_CONTAINER table for processing when Coontainer files not found and when its present in DZ */
	public void insertIntoDzJobContinerTable(int stream, String sourcesystemcode, int contseqnumb) throws SQLException, Exception
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "INSERT INTO edw2.dz.DZ_JOB_CONTAINER VALUES ('-1','" + stream + "' ,'" + sourcesystemcode + "' ,'" + contseqnumb +  "' ,'1005560','A','20','2020-07-31','2020-07-31','2020-08-01','2021-09-22 14:30:52.0300000','Input file handler',NULL,NULL,'Y')";
		((java.sql.Statement) stmt).executeUpdate(sql);
		
		System.out.println("Sequence " + contseqnumb +  " inserted into EDW2.dz.DZ_JOB_CONTAINER to be processed");
		
		updateCreateDataContToNull(stream, sourcesystemcode, contseqnumb);
		System.out.println("Updated Create_Dat_Conatiner to NULL");
		
	}
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
		setParamVal("RUN_MODE", "NORMAL"); // Setting RUN_MODE(PARAM_NAME) to "NORMAL"(PARAM_VAL)
		System.out.println("Updated PARAM table for AC run ");
		
	}
	
	/**  This method will update PARAM_VAL flag to "Y" in CTL.CTL_PARAM table for PARAM_NAME "DC_CYCLE_ACTIVE_FLAG". */
	public void setParamTableForDCRun() throws SQLException
	{
		setParamVal("AC_CYCLE_ACTIVE_FLAG", "N");
		setParamVal("DC_CYCLE_ACTIVE_FLAG", "Y");
		setParamVal("RUN_MODE", "NORMAL"); // Setting RUN_MODE(PARAM_NAME) to "NORMAL"(PARAM_VAL)
		// Updating DC_START_TIME in CTL_PARAM table.
		java.util.Date date=new java.util.Date();  
		String time =(date.toString().substring(11,16));
		setParamVal("DC_START_TIME", time);
		
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
		
		//System.out.println("Updated PARAM_VAL flag to \"Y\" for PARAM_NAME \"PLPRunFlag\", \"PROVRunFlag\" and \"DISTRunFlag\".");
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
	
//--------------------------------------------------------------------------Job Activity monitor scripts------------------------------------------------------------------------------
		/**  This method will truncate DQ_ITEM table(if truncate) = "true" and then runs a job specified in jobname. */
		public void runJob(String jobname, boolean truncate) throws SQLException 
		{  
			if ( jobname == "AC_DC_JobScheduler") // Truncates DQ_ITEM Tables for AC run.
			{
				if(truncate == true)
				{
					// truncate DQ_ITEM table
					Statement stmt = (Statement) con.createStatement();
					try 
						{
							String sql = "DELETE FROM dbo.DQ_ITEM"; 
							((java.sql.Statement) stmt).execute(sql);
						}
					catch (SQLException e) //SQLServerException
						{
							e.printStackTrace();
						}
				}
				
			}
			
			// runs the job.
			Statement stmt1 = (Statement) con.createStatement();
			try 
				{
					String sql1 = "EXEC msdb.dbo.sp_start_job N'" + jobname +"'";   //EXEC msdb.dbo.sp_start_job N'MyJobName';
					((java.sql.Statement) stmt1).executeUpdate(sql1);
				} 
			catch (SQLException e) //SQLServerException
				{
				    int err = e.getErrorCode();
				    System.out.println("ERROR code = " + err);
					
					if(err == 22022)
					{
						System.out.println("Job is already running.");
					}
					
					e.printStackTrace();
				}
			//System.out.println();
			
		 }
		
		public String jobStatus(String jobname) throws SQLException, InterruptedException 
		{ 
			String status = null;
			
			Statement stmt = (Statement) con.createStatement();
			String sql = "IF EXISTS(SELECT 1 FROM msdb.dbo.sysjobs J JOIN msdb.dbo.sysjobactivity A ON A.job_id=J.job_id WHERE J.name=N'" + jobname + "' AND A.run_requested_date IS NOT NULL AND A.stop_execution_date IS NULL) PRINT 'running' ELSE PRINT 'not running'";
			stmt.execute(sql);
			SQLWarning warning = stmt.getWarnings();
				while (warning != null)
				{
				   status = warning.getMessage();
				   TimeUnit.SECONDS.sleep(60);
				   if(status != "running")
				   {
					   System.out.println( warning.getMessage());//("Running....");
				   }
				   
				   else
				   {
					   System.out.println("Not Running....");
				   }
				}
				
		return status;
		}

//-------------------------------------------------------------------Data Dictionary Methods----------------------------------------------------------------------
	
	/**  This method will return all DZ tables for a stream entered as parameter. **/
		public ArrayList<String> DZTablesForAStream(int stream) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = null;
			if(stream < 10)
			{
				sql = "SELECT DISTINCT(TABLE_NAME), SUBSTRING(TABLE_NAME,7, (LEN(TABLE_NAME)-8)) AS 'DZTABLE' FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME LIKE 'STAGE_%[_]" + stream + "'";
			//String sql = "SELECT DISTINCT(TABLE_NAME), TABLE_ABBR FROM Factory.StagingColumnDataDictionary  WHERE DATA_STREAM_ID = " + stream ;
			}
			else
			{
				if(stream == 55) // Stream 55(CHAMB_) has only one DZ file [DZ].[dbo].[DS_55_350_HIE_CHAMB_ACTIVITY_RECORD]
				{
					sql = "SELECT 'DS_55_350_HIE_CHAMB_ACTIVITY_RECORD' AS 'DZTABLE'"; //Hard coded
				}
				else if(stream == 56) // Stream 55(CHAMB_) has only one DZ file [DZ].[dbo].[DS_55_350_HIE_CHAMB_ACTIVITY_RECORD]
				{
					sql = "USE DZ\r\n"
							+ "SELECT DISTINCT(TABLE_NAME) AS 'DZTABLE' FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME LIKE 'DS_56_%'"; //Hard coded
				}
				else
				{
				sql = "SELECT DISTINCT(TABLE_NAME), SUBSTRING(TABLE_NAME,7, (LEN(TABLE_NAME)-9)) AS 'DZTABLE' FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME LIKE 'STAGE_%[_]" + stream + "'";
				}
			}
			
			ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
			int count = 0;
			ArrayList<String> dztables = new ArrayList<String>();
			//System.out.println(dztables);
			while(rs.next()) 
			{
				String tabnamedz = rs.getString("DZTABLE");
				count = count+1;
				//System.out.println( "table name in DZ : "+ tabnamedz);
				dztables.add(tabnamedz);
			}

			System.out.println("DZ tables : " + dztables);
			return dztables;
		 }
		
		/**  This method will return all core tables for a stream entered as parameter. **/
		public ArrayList<String> DZTablesForAStreamUsingAuditLogs(int stream) throws SQLException
		{
			ArrayList<String> dztables = new ArrayList<String>();
			int count = 0;
			
			Statement stmt = (Statement) con.createStatement();
			
			String sql = null;
			
			if(stream < 10)
				{
					sql =  "SELECT DISTINCT(SUBSTRING(TABLE_NAME,7, (LEN(TABLE_NAME)-8))) AS 'TABLE_NAME' FROM CTL.AUDIT_TRAIL WITH(NOLOCK) WHERE PACKAGE_NAME LIKE 'AP1 " + stream + "%' AND TABLE_NAME LIKE 'STAGE%'";
				}
			else
			{
				if(stream == 55) // Stream 55(CHAMB_) has only one DZ file [DZ].[dbo].[DS_55_350_HIE_CHAMB_ACTIVITY_RECORD]
					{
						sql = "SELECT 'DS_55_350_HIE_CHAMB_ACTIVITY_RECORD' AS 'TABLE_NAME'"; //Hard coded
					}
				else if(stream == 56) // Stream 55(MHOAT_) has only one DZ file [DZ].[dbo].
					{
						sql = "USE DZ\r\n"
								+ "SELECT DISTINCT(TABLE_NAME) AS 'TABLE_NAME' FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME LIKE 'DS_56_%'"; //Hard coded
					}
				else
					{
					    sql =  "SELECT DISTINCT(SUBSTRING(TABLE_NAME,7, (LEN(TABLE_NAME)-9))) AS 'TABLE_NAME' FROM CTL.AUDIT_TRAIL WITH(NOLOCK) WHERE PACKAGE_NAME LIKE 'AP1 " + stream + "%' AND TABLE_NAME LIKE 'STAGE%'";
					}
			}
			
			ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
			
			while(rs.next()) 
				{
					String tabnamedz = rs.getString("TABLE_NAME");
					count = count+1;
					dztables.add(tabnamedz);
				}
			System.out.println("dztables : " + dztables);
			return dztables;
		 }
		
		
	/**  This method will return all core tables for a stream entered as parameter. **/
		public ArrayList<String> coreTablesForAStreamusingStage(int stream) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = null;
			String sql1 = null;
			
			if(stream < 10)
			{
				sql = "SELECT DISTINCT I.TABLE_NAME, SUBSTRING(I.TABLE_NAME,7, (LEN(I.TABLE_NAME)-8)) AS 'DZTABLE', F.TABLE_ABBR AS 'CoreTABLE' FROM INFORMATION_SCHEMA.COLUMNS I JOIN Factory.EDWColumnDataDictionary F ON " 
							 + "SUBSTRING(I.TABLE_NAME,7, (LEN(I.TABLE_NAME)-8)) = F.TABLE_NAME WHERE I.TABLE_NAME LIKE '%[_]" + stream + "'";
			}
			
			else
			{
			    sql = "SELECT DISTINCT I.TABLE_NAME, SUBSTRING(I.TABLE_NAME,7, (LEN(I.TABLE_NAME)-9)) AS 'DZTABLE', F.TABLE_ABBR AS 'CoreTABLE' FROM INFORMATION_SCHEMA.COLUMNS I JOIN Factory.EDWColumnDataDictionary F ON " 
						 + "SUBSTRING(I.TABLE_NAME,7, (LEN(I.TABLE_NAME)-9)) = F.TABLE_NAME WHERE I.TABLE_NAME LIKE '%[_]" + stream + "'";
			}
			
			ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
			ArrayList<String> coretables = new ArrayList<String>();
			
			while(rs.next()) 
			{
				String tabnamecore = rs.getString("CoreTABLE");
				coretables.add(tabnamecore);
			}
			System.out.println( "Table count : " + coretables.size());
			System.out.println("coretables : " + coretables);
			
			// To get count of number of staging tables 
			Statement stmt1 = (Statement) con.createStatement();
			sql1 = "SELECT DISTINCT(I.TABLE_NAME) FROM INFORMATION_SCHEMA.COLUMNS I WHERE I.TABLE_NAME LIKE '%[_]" + stream +"' AND I.TABLE_NAME LIKE '%STAGE_%'";
			ResultSet rs1 = ((java.sql.Statement) stmt1).executeQuery(sql1);
			//System.out.println(sql1);
			
			ArrayList<String> stagetables = new ArrayList<String>();
			while (rs1.next())
			{
				String stgtable = rs1.getString("TABLE_NAME");
				stagetables.add(stgtable);
			}
			int stagetablecount = stagetables.size();
			System.out.println( "Table count : " + stagetablecount);
			System.out.println("Stage tables are : " + stagetables);
			
			//Checking if Staging table counts is equal to core table count
			if(stagetablecount != coretables.size())
			{
				if(stagetablecount < coretables.size())
				{
					System.out.println("Core table counts greater than Stage table count. There could be derived tables in the core table list, please check but coretables list has all tables + derived tables");
				}
				else
				System.out.println("Core table count is : " + coretables.size() + " and Staging table count is : " + stagetablecount + ", It could not fetch all core tables because tables are missing in Factory.");
			}
			
			
			return coretables;
		 } 
		
		/**  This method will return all core tables for a stream entered as parameter. **/
		public ArrayList<String> coreTablesForAStreamusingFactory(int stream) throws SQLException
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
			System.out.println("coretables : " + coretables);
			return coretables;
		 }
		
		/**  This method will return all core tables for a stream entered as parameter. **/
		public ArrayList<String> coreTablesForAStreamUsingAuditLogs(int stream) throws SQLException
		{
			ArrayList<String> coretables = new ArrayList<String>();
			int count = 0;
			
			Statement stmt = (Statement) con.createStatement();
			String sql = "SELECT DISTINCT(TABLE_NAME) FROM CTL.AUDIT_TRAIL WITH(NOLOCK) WHERE PACKAGE_NAME LIKE 'AP1_" + stream + "%' AND TASK_NAME = '20 MERGE STG to EDW' AND TABLE_NAME IS NOT NULL";
			ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
			
			while(rs.next()) 
				{
					String tabnamecore = rs.getString("TABLE_NAME");
					count = count+1;
					coretables.add(tabnamecore);
				}
			System.out.println("coretables : " + coretables);
			return coretables;
		 }
		
		/**  This method will return CBK for given DZ table. **/
		public String findCbkForaTableInDZ(String dztable) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = null;
			sql = "SELECT DISTINCT(COLUMN_ABBR), COLUMN_NAME, CONVERT(int,ORDINAL_POSITION) AS ORDINAL_POSITION  FROM factory.EDWColumnDataDictionary WHERE TABLE_NAME = '"+ dztable +"' AND WILL_BE_PK= 'Y' GROUP BY COLUMN_NAME, COLUMN_ABBR, ORDINAL_POSITION ORDER BY ORDINAL_POSITION DESC";
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
		
		/**  This method will return CBK for given CORE table using factory.EDWColumnDataDictionary table. **/
		public String findCbkForaTableInCore(String coretable) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = "SELECT DISTINCT COLUMN_NAME, COLUMN_ABBR, convert(int, ORDINAL_POSITION) FROM factory.EDWColumnDataDictionary WHERE TABLE_ABBR = '"+ coretable +"' "
					+ "AND WILL_BE_PK= 'Y' AND WILL_COLUMN_BE_USED = 'Y' AND DATA_STREAM_ID = (SELECT MIN(DATA_STREAM_ID)  FROM factory.EDWColumnDataDictionary WHERE TABLE_ABBR = '"+ coretable +"' )"
					+ "GROUP BY COLUMN_NAME, COLUMN_ABBR, ORDINAL_POSITION ORDER BY convert(int, ORDINAL_POSITION) DESC";
		 	ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		 	
		 	//System.out.println(sql);
			String cbkv = new String();
			//System.out.println(cbkv);
			String cbk = new String();
						try {
								
								while(rs.next())
									{
										String cbkcols =rs.getString("COLUMN_ABBR");
//										System.out.println(cbkcols);
										//cbkv = cbkcols + " + " + cbkv;
										cbkv = cbkcols + " + '|' + " + cbkv;
									}
								cbk = cbkv.substring(0, cbkv.lastIndexOf(" + '|' + "));
								cbk = cbk.trim();
								System.out.println(coretable + ": " + cbk);
								
						   } 
					   catch (Exception ignore) {}
			//System.out.println(cbk);
			if(cbk.equals(""))
			{
 				System.out.println("Table name does not exist, please check if '" + coretable  + "' is a core table");
 		    }
			 	   
			return cbk;
			
		 }
		
		/**  This method will check if the Checksum hash is correct for a table by comparing checksum in the table to a derived checksum **/
		public void CheckCBKForATable(String coretable, int edwcontainer) throws SQLException
		{ 
			String cbk = findCbkForaTableInCore(coretable); // fetching Checksum string from its method
			System.out.println(cbk);
			Statement stmt = (Statement) con.createStatement();
			String sql = "SELECT " + coretable + "_CBK," + cbk + " AS 'Derived_cbk'" + ", EDW_DATA_CONTAINER_ID ,EDW_STUB_IND, EDW_EFFT_START_DTTM , IIF("+ coretable + "_CBK = " + cbk + ", 'Equal', 'Not Equal') FROM " + coretable + " WHERE EDW_DATA_CONTAINER_ID = " + edwcontainer ;
			ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		 	//System.out.println(sql);
			
			System.out.println(coretable + "_CBK" +"\t" + "Derived_CBK" +"\t"+ "EDW_DATA_CONTAINER_ID" + "\t"+ "EDW_STUB_IND" +"\t"+ "EDW_EFFT_START_DTTM"+"\t"+ "Result");
		 	while(rs.next())
			{
		 		System.out.println(rs.getString(1) +"\t"+  rs.getString(2)+ "\t"+rs.getString(3) +"\t"+rs.getString(4) +"\t"+rs.getString(5) +"\t"+rs.getString(6));
			}
		 }

//---------------------------------------------------------------- non CBK column methods ------------------------------------------------------------	
		
		/**  This method will return non CBK columns for a stream using factory.EDWColumnDataDictionary table. **/
		public void nonCbkColumnsForaStreamInCoreForStreamUsingFactory(int stream) throws SQLException
		{
			ArrayList<String> coretables = coreTablesForAStreamusingFactory(stream);
			int nooftablesinstream = coretables.size();
			//System.out.println(nooftablesinstream);
			
				for(int i=0 ; i<nooftablesinstream; i++)
				{
					nonCbkColumnsForaTableInCoreUsingFactoryJoiningInforationSchema(coretables.get(i));
				}
			
		 }
	
		/**  This method will return non CBK columns for a given CORE table. **/
		public String nonCbkColumnsForaTableInCoreUsingCoreTable(String coretable) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
		
			String sql = "SELECT DISTINCT COLUMN_NAME, COLUMN_ABBR, ORDINAL_POSITION FROM factory.EDWColumnDataDictionary WHERE TABLE_ABBR ='"+ coretable
					+"' AND WILL_BE_PK= 'N' AND WILL_COLUMN_BE_USED = 'Y' AND COLUMN_ABBR  NOT IN ('DE_TABLE_CBK')"
					+ " AND COLUMN_NAME NOT LIKE '%CONTAINER_ID'  AND COLUMN_NAME NOT LIKE 'EDW_%' AND COLUMN_NAME NOT LIKE 'SOURCE_%' GROUP BY COLUMN_NAME, COLUMN_ABBR, ORDINAL_POSITION ORDER BY ORDINAL_POSITION DESC";
		 ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		 	
		 	//System.out.println(sql);
			String noncbkv = new String();
			//System.out.println(cbkv);
			String noncbk = new String();
						try {
								
								while(rs.next())
									{
										String noncbkcols =rs.getString("COLUMN_ABBR");
//										System.out.println(cbkcols);
										noncbkv = noncbkcols + " + " + noncbkv;
										
									}
								noncbk = noncbkv.substring(0, noncbkv.lastIndexOf("+"));
								noncbk = noncbk.trim();
								System.out.println(coretable + ": " + noncbk);
								
						   } 
					   catch (Exception ignore) {}
			//System.out.println(cbk);
			if(noncbk.equals(""))
			{
 				System.out.println("Table name does not exist, please check if '" + coretable  + "' is a core table");
 		    }
			 	   
			return noncbk;
			
		 }
		
		/**  This method will return non CBK columns for a given CORE table using factory.EDWColumnDataDictionary table. **/
		public String nonCbkColumnsForaTableInCoreUsingFactoryJoiningInforationSchema(String coretable) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			String sql = "SELECT DISTINCT F.COLUMN_NAME, F.COLUMN_ABBR, F.ORDINAL_POSITION, F.DATA_TYPE, F.CHARACTER_MAXIMUM_LENGTH,F.NUMERIC_PRECISION FROM factory.EDWColumnDataDictionary F"
					+ " JOIN INFORMATION_SCHEMA.COLUMNS I ON F.COLUMN_ABBR = I.COLUMN_NAME WHERE F.TABLE_ABBR ='"+ coretable
					+"' AND F.WILL_BE_PK= 'N' AND F.WILL_COLUMN_BE_USED = 'Y' AND F.COLUMN_ABBR NOT IN ('DE_TABLE_CBK')"
					+ " AND F.COLUMN_NAME NOT LIKE '%CONTAINER_ID'  AND F.COLUMN_NAME NOT LIKE 'EDW_%' AND F.COLUMN_NAME NOT LIKE 'SOURCE_%' ORDER BY F.ORDINAL_POSITION";
		 	ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		 	
		 	//System.out.println(sql);
			String noncbkv = new String();
			//System.out.println(cbkv);
			String noncbk = new String();
	
						try {
								
								while(rs.next())
									{
										String noncbkcols =rs.getString("COLUMN_ABBR");
//										System.out.println(cbkcols);
										noncbkv = noncbkcols + " + " + noncbkv;
										
									}
								noncbk = noncbkv.substring(0, noncbkv.lastIndexOf("+"));
								noncbk = noncbk.trim();
								System.out.println(coretable + ": " + noncbk);
								
						   } 
					   catch (Exception ignore) {}
		
			if(noncbk.equals(""))
			{
 				System.out.println("Table name does not exist, please check if '" + coretable  + "' is a core table");
 		    }
			 	   
			return noncbk;
			
		 }
		
		/**  This method will return a CHECKSUM String for a given CORE table. **/
		public String checkSumStringForATable(String coretable) throws SQLException
		{
			Statement stmt = (Statement) con.createStatement();
			// Query Joining Factory and Information schema to get non cbk columns.
			String sql = "SELECT DISTINCT F.COLUMN_NAME, F.COLUMN_ABBR, F.ORDINAL_POSITION, F.DATA_TYPE, F.CHARACTER_MAXIMUM_LENGTH,F.NUMERIC_PRECISION,F.NUMERIC_SCALE FROM factory.EDWColumnDataDictionary F"
					+ " JOIN INFORMATION_SCHEMA.COLUMNS I ON F.COLUMN_ABBR = I.COLUMN_NAME WHERE F.TABLE_ABBR ='"+ coretable
					+"' AND F.WILL_BE_PK= 'N' AND F.WILL_COLUMN_BE_USED = 'Y' AND F.COLUMN_ABBR NOT IN ('DE_TABLE_CBK')"
					+ " AND F.COLUMN_NAME NOT LIKE '%CONTAINER_ID'  AND F.COLUMN_NAME NOT LIKE 'EDW_%' AND F.COLUMN_NAME NOT LIKE 'SOURCE_%' ORDER BY F.ORDINAL_POSITION";
		 	ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		 	
		 	//System.out.println(sql);
			String checksumv = new String();
			String checksum = new String();
			String checksumf = new String();
	
						try {
								
								while(rs.next())
									{
										String checksumcols =rs.getString("COLUMN_ABBR");
										if( rs.getString("DATA_TYPE").equals("datetime2") || rs.getString("DATA_TYPE").equals("date"))
										{
											checksumv = "TRY_CONVERT([" + rs.getString("DATA_TYPE") + "], case when ["+ checksumcols + "]='' then 'NULL DATE' else [" + checksumcols + "] end,(126))";
											
										}
										else if( rs.getString("DATA_TYPE").equals("int") || rs.getString("DATA_TYPE").equals("numeric") || rs.getString("DATA_TYPE").equals("decimal"))
										{
											checksumv = "TRY_CAST([" + checksumcols + "] AS ["  + rs.getString("DATA_TYPE") + "]("+ rs.getString("NUMERIC_PRECISION") + "," + rs.getString("NUMERIC_SCALE") +"))";
											//checksumv = "TRY_CAST([" + checksumcols + "] AS ["  + rs.getString("DATA_TYPE") + "]("+ rs.getString("NUMERIC_PRECISION") + "))";
											
										}
										else
										{
											checksumv = "TRY_CAST([" + checksumcols + "] AS ["  + rs.getString("DATA_TYPE") + "]("+ rs.getString("CHARACTER_MAXIMUM_LENGTH") + "))";
										}
     									//System.out.println(checksumv);
     									
										checksum = checksum + ",'|'," + checksumv;
										
									}

								//System.out.println("Checksum :" + checksum);
								checksumf = "hashbytes('SHA2_256',concat(" + checksum.substring(5) + "))";
								System.out.println("       "); // for Cosmetic look.
								System.out.println("CheckSum for the " + coretable + " is: " + checksumf);
						   } 
					   catch (Exception ignore) {}
		
			if(checksumf.equals(""))
			{
 				System.out.println("Table name does not exist, please check if '" + coretable  + "' is a core table");
 		    }
			 	   
			return checksumf;
		}
		
		/**  This method will check if the Checksum hash is correct for a table by comparing checksum in the table to a derived checksum **/
		public void CheckCheckSumHashForATable(String coretable, int edwcontainer) throws SQLException
		{ 
			String csstring = checkSumStringForATable(coretable); // fetching Checksum string from its method
			//System.out.println(csstring);
			Statement stmt = (Statement) con.createStatement();
			//String sql = "SELECT SVC_ACT_CBK, EDW_DATA_CONTAINER_ID ,EDW_STUB_IND,EDW_CHECK_SUM ," + csstring + ", EDW_EFFT_START_DTTM  FROM " + coretable ;
			String sql = "SELECT " + coretable + "_CBK, EDW_DATA_CONTAINER_ID ,EDW_STUB_IND,EDW_CHECK_SUM ," + csstring + ", EDW_EFFT_START_DTTM , IIF(EDW_CHECK_SUM = " + csstring + ", 'Equal', 'Not Equal') FROM " + coretable + " WHERE EDW_DATA_CONTAINER_ID = " + edwcontainer ;
			ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		 	//System.out.println(sql);
			System.out.println("  "); // for Cosmetic look.
			System.out.println(coretable + "_CBK" +"\t"+  "EDW_DATA_CONTAINER_ID" + "\t"+ "EDW_STUB_IND" +"\t"+ "EDW_CHECK_SUM" +"\t"+ "DerivedCHECKSUM" +"\t"+"EDW_EFFT_START_DTTM"+"\t"+ "Result");
		 	while(rs.next())
			{
		 		System.out.println(rs.getString(1) +"\t"+  rs.getString(2)+ "\t"+rs.getString(3) +"\t"+rs.getString(4) +"\t"+rs.getString(5) +"\t"+rs.getString(6) +"\t"+rs.getString(7));
			}
		 }
		
		/**  This method will check if the Checksum hash is correct for each table of core by comparing checksum in the table to a derived checksum **/
		public void CheckCheckSumHashForAllCoreTablesInAStream(int stream, int edwcontainer) throws SQLException
		{ 
			ArrayList<String> coretables = coreTablesForAStreamusingFactory(stream); // Uses factory for getting core tables
			
			for(int i = 0; i< coretables.size(); i++) 
			{
				String csstring = checkSumStringForATable(coretables.get(i)); // fetching Checksum string from its method
				Statement stmt = (Statement) con.createStatement();
				
				String sql = "SELECT " + coretables.get(i) + "_CBK, EDW_DATA_CONTAINER_ID ,EDW_STUB_IND,EDW_CHECK_SUM ," + csstring + ", EDW_EFFT_START_DTTM , IIF(EDW_CHECK_SUM = " + csstring + ", 'Equal', 'Not Equal') FROM " + coretables.get(i) + " WHERE EDW_DATA_CONTAINER_ID = " + edwcontainer ;
				System.out.println(sql);
				ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
			 	
				System.out.println("  "); // for Cosmetic look.
				System.out.println(coretables.get(i) + "_CBK" +"\t"+  "EDW_DATA_CONTAINER_ID" + "\t"+ "EDW_STUB_IND" +"\t"+ "EDW_CHECK_SUM" +"\t"+ "DerivedCHECKSUM" +"\t"+"EDW_EFFT_START_DTTM"+"\t"+ "Result");
				 	while(rs.next())
					{
				 		System.out.println(rs.getString(1) +"\t"+  rs.getString(2)+ "\t"+rs.getString(3) +"\t"+rs.getString(4) +"\t"+rs.getString(5) +"\t"+rs.getString(6) +"\t"+rs.getString(7));
					}
			}
		 }
			
		/**  This method will compare COLUMN_NAME , DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION and NUMERIC_SCALE as part of meta data tests for a stream between Core tables(USING INFORMATIONSCHEMA) and factory.EDWColumnDataDictionary.(source of truth) */
		public void comparefactoryToCore(int stream) throws SQLException
		{
			Statement stmt1 = (Statement) con.createStatement();
			Statement stmt2 = (Statement) con.createStatement();
			// Query on Factory tables
			String sqlf = "SELECT DISTINCT(TABLE_NAME), COLUMN_NAME , DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, TABLE_ABBR, COLUMN_ABBR,  IS_NULLABLE"
					+ " FROM factory.EDWColumnDataDictionary WHERE DATA_STREAM_ID = " + stream + " AND WILL_COLUMN_BE_USED = 'Y'";
			ResultSet rsf = ((java.sql.Statement) stmt1).executeQuery(sqlf);
			// Printing headers
			System.out.println("DD_TABLE_NAME, DD_COLUMN_NAME_IN_DZ , DD_DATA_TYPE, DD_CHARACTER_MAXIMUM_LENGTH, DD_NUMERIC_PRECISION, DD_NUMERIC_SCALE, IS_TABLE_ABBR, IS_COLUMN_ABBR,  IS_DATA_TYPE, IS_CHARACTER_MAXIMUM_LENGTH, IS_NUMERIC_PRECISION, IS_NUMERIC_SCALE,  DATA_TYPE_Check, CHARACTER_MAXIMUM_LENGTH_Check, NUMERIC_PRECISION_Check, NUMERIC_SCALE_Check");
			
			int count = 0;
				while(rsf.next()) 
				{	
					//DZ var's for feeding into Core table query
					String tabnamecore = rsf.getString("TABLE_ABBR");
					String colnamecore = rsf.getString("COLUMN_ABBR");
					
					String tabnamedz = rsf.getString("TABLE_NAME");
					String colnamedz = rsf.getString("COLUMN_NAME");
					
					String datatypedz =rsf.getString("DATA_TYPE");
					String lengthdz = rsf.getString("CHARACTER_MAXIMUM_LENGTH");
					
					// Converting from NULL in the factory sheet to match DB "null"
					if (lengthdz.equals("NULL"))
					{
						lengthdz = null;
					}
					
					String numprecdz = rsf.getString("NUMERIC_PRECISION");
					
					// Converting from NULL in the factory sheet to match DB "null"
					if (numprecdz.equals("NULL"))
					{
						numprecdz = null;
					}
					
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
						//Comparing two Strings
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

		 }
		
		public void printMetaDataForCoreTable(int stream) throws SQLException
		{
			ArrayList<String> coretables = coreTablesForAStreamUsingAuditLogs(stream);

			System.out.println(coretables);
			for(int i = 0; i< coretables.size(); i++) 
			{
				Statement stmt = (Statement) con.createStatement();
				String sql = "SELECT TABLE_NAME, COLUMN_NAME , DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE,  IS_NULLABLE " + 
						" FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = " + "'"+ coretables.get(i);
				ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
				
				while (rs.next()) 
				{
					//Core vars
					String tablename =rs.getString("TABLE_NAME");
					String columnname =rs.getString("COLUMN_NAME");
					String datatypecore =rs.getString("DATA_TYPE");
					String lengthcore = rs.getString("CHARACTER_MAXIMUM_LENGTH");
					String numpreccore = rs.getString("NUMERIC_PRECISION");
					String isnullablecore = rs.getString("IS_NULLABLE");
					
					System.out.println( tablename + ", " + columnname + ", " + datatypecore+ ", " + lengthcore + ", " + numpreccore+ ", " + isnullablecore);
	
				}
			}
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
				String cbk = bc.findCbkForaTableInDZ(dztables.get(i));
				String query =new String();
			    query = "SELECT '" + dztables.get(i) + "' AS TABLE_NAME,COUNT(DISTINCT " + cbk + ") AS TOTAL_COUNT FROM DZ.DBO." + dztables.get(i) + " WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "' AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb + " AND ACTION_TYPE != 'D'";
			    
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

			} 
			
			// For Core tables
			ArrayList<String> coretables = coreTablesForAStreamusingFactory(stream);

			for(int i = 0; i< coretables.size(); i++) 
			{
				String cbk = bc.findCbkForaTableInCore(coretables.get(i));
				String query =new String();
			    query = "SELECT '" + coretables.get(i) + "' AS COLUMN_ABBR, COUNT(DISTINCT " + cbk + ") AS TOTAL_COUNT FROM " + coretables.get(i) + " WHERE REC_SRC_SYS_CD = '" + sourcesystemcode + "' AND EDW_DATA_CONTAINER_ID = " + datacontid + "AND EDW_LOGICAL_DELETE_FG = 'N'";
			    
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
			ArrayList<String> coretables = coreTablesForAStreamusingFactory(stream);
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

		public void generateUpdateScripts(int stream, String sourcesystemcode, int contseqnumb) throws SQLException
		{
			//SELECT * FROM factory.EDWColumnDataDictionary WHERE TABLE_NAME = 'CLIENT' AND WILL_BE_PK= 'Y' ORDER BY COLUMN_SEQ
			BaseClass bc = new BaseClass();
			//int datacontid = bc.EdwContainerForASequence(stream, sourcesystemcode, contseqnumb);
			ArrayList<String> coretables = bc.coreTablesForAStreamusingFactory(stream);
			
			for(int i = 0; i< coretables.size(); i++) 
			{
				// fetching columns to feed to next query
				Statement stmt = (Statement) con.createStatement();
				String sql = "SELECT TABLE_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + coretables.get(i) + "' AND IS_NULLABLE = 'YES' AND COLUMN_NAME NOT LIKE '%_SK' AND COLUMN_NAME NOT IN ('SRC_CREATE_DTTM', 'SRC_MOD_DTTM')";
				ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
				
				System.out.println("---------------------------------------------------------------------------------------------------");
				
				while(rs.next()) 
				{
					String coretabname =rs.getString("TABLE_NAME");
					String corecolname =rs.getString("COLUMN_NAME");
					
					Statement stmt1 = (Statement) con.createStatement();
					String sql1 = "SELECT DISTINCT(TABLE_NAME), TABLE_ABBR, COLUMN_NAME,COLUMN_ABBR FROM Factory.EDWColumnDataDictionary WHERE DATA_STREAM_ID = " + stream +" AND TABLE_ABBR = '" + coretabname + "' AND COLUMN_ABBR = '" + corecolname + "'";
					ResultSet rs1 = ((java.sql.Statement) stmt1).executeQuery(sql1);
					while(rs1.next()) 
						{
							System.out.println("UPDATE " + rs1.getString("TABLE_NAME") + " SET " + rs1.getString("COLUMN_NAME") + " = ''" + " WHERE CONTAINER_SEQUENCE_NUMBER = " + contseqnumb + " AND RECORD_SOURCE_SUSTEM_CODE = '" + sourcesystemcode + "'");
						}
					
				}
			
			}
			
		}
	
//------------------------------------------------ Meta data scripts ------------------------------------------------
	
	/**  This method outputs CHARACTER_MAXIMUM_LENGTH for given table. */
	public void MaxlengthForTable(String tablename, String schema) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "SELECT TABLE_NAME, COLUMN_NAME, CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + tablename + "'AND"
		+ " TABLE_SCHEMA = '" + schema + "'";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		
		while(rs.next()) 
		{
			String tabname = rs.getString("TABLE_NAME");
			String colname = rs.getString("COLUMN_NAME");
			String maxlen = rs.getString("CHARACTER_MAXIMUM_LENGTH");
			System.out.println( tabname + "," + colname + ',' + maxlen);
		}
	 }	
	
	/**  This method outputs DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUllable for given table. */
	public void tableProperties(String tablename, String schema) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		
		String sql = "SELECT TABLE_NAME, COLUMN_NAME , DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, IS_NULLABLE , ORDINAL_POSITION FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" 
					  + tablename + "' AND TABLE_SCHEMA = '" + schema + "'" + " ORDER by COLUMNS.ORDINAL_POSITION";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		
		while(rs.next()) 
		{
			String tabname = rs.getString("TABLE_NAME");
			String colname = rs.getString("COLUMN_NAME");
			String datatype = rs.getString("DATA_TYPE");
			String maxlen = rs.getString("CHARACTER_MAXIMUM_LENGTH");
			String numprec = rs.getString("NUMERIC_PRECISION");
			String isnullable = rs.getString("IS_NULLABLE");
			
			System.out.println( tabname + "," + colname + ',' + datatype + ',' + maxlen + "," + numprec + "," + isnullable);
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
	
//--------------------------------------------------- AC test's ----------------------------------------------------
	
	/**  This method will return all SK's in a table. */
	public ArrayList<String> sksInACoreTable(String tablename) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"+ tablename + "' AND COLUMN_NAME LIKE '%SK' AND ORDINAL_POSITION != 1";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		
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
	
	/**  This method will return all columns which has to be checked for updates for Each table in a stream (Ex: columns not in CBK, SK's and CTL). */
	public ArrayList<String> updatableColumnsInACoreTable(String tablename) throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "SELECT DISTINCT(COLUMN_NAME), COLUMN_ABBR, ORDINAL_POSITION FROM factory.EDWColumnDataDictionary WHERE TABLE_NAME ='"+ tablename + "' AND WILL_BE_PK= 'N' AND COLUMN_ABBR NOT IN ('DE_KEY', 'CUR_IND_FG', 'QUALITY_IND', 'DE_TABLE_CBK')\r\n"
				+ "AND COLUMN_NAME NOT LIKE '%CONTAINER_ID'  AND COLUMN_NAME NOT LIKE 'EDW_%' AND COLUMN_NAME NOT LIKE 'SOURCE_%' GROUP BY COLUMN_NAME, COLUMN_ABBR, ORDINAL_POSITION ORDER BY ORDINAL_POSITION";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		
		ArrayList<String> collist = new ArrayList<String>();
		
		while(rs.next()) 
			{
				String sk = rs.getString("COLUMN_NAME");
				//System.out.println(sk);
				collist.add(sk);
			}
		System.out.println(collist);
		return collist;
	 }
//--------------------------------------------------------------------- AC coloumn Updates test's -------------------------------------------
	
	/**  (In-progress)This method will print update scripts for all tables in a data stream. */
	public void updateAllColumnsForCoreTables(int stream) throws SQLException
	{
		ArrayList<String> dztables = DZTablesForAStream(stream); // Fetching DZ tables
		for(int i=0; i< dztables.size(); i++)
		{
			ArrayList<String> collist = updatableColumnsInACoreTable(dztables.get(i));
			System.out.println(collist);
			for(int c = 0; c < collist.size(); c++)
			{	
				//get cbk and find collist.size() cbks in each table
				String cbk = findCbkForaTableInDZ(dztables.get(i));
				
				// Get top collist.size() rows to update.
				
				// Update each DZ table for 1 column at a time for all the columns in collist;
				Statement stmt = (Statement) con.createStatement();
				String sql = "UPDATE dztables.get(i) SET collist.get(c)";
				ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
				while(rs.next()) 
				{
					
				}
			}
		}
		
	}
			
//---------------------------------------------------------------------Validation--------------------------------------------------------------------------
	
	/**  (In-progress)This method will show if there are duplicate CBK's in a table. */
	public void checkDuplicateCBKInCLSE() throws SQLException
	{
		Statement stmt = (Statement) con.createStatement();
		// REGRESSION Duplicate Record Check as per 1941-- SIT check 9/9/2021 no issues (should return NO rows): As expected
		String sql = "select se_typ_cd, se_src_id, svc_enc_rec_id, se_rec_id, count(*) as 'COUNT' from edw2.dbo.CL_SE group by se_typ_cd, se_src_id, svc_enc_rec_id, se_rec_id having count(*) > 1";
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		System.out.println(rs);
		
		//int count = rs.getInt("COUNT");
			if(rs.next())
				{
					System.out.println("FAIL: Duplicate CBK's in CL_SE");
				}
				
		    else
		        {
		        	System.out.println("PASS: No Duplicate CBK's in CL_SE");
		        }		
		
		
	 }

//	public void findCbkForaTableInCore(int i) {
//		// TODO Auto-generated method stub
//		
//	}
	
//--------------------------------------------------------------------- Security methods --------------------------------------------------------------------------
	
	/**  This method will return list of tables for a role in DB_Roles_to_Table_View_Access table(Ex: AP user or ED User. */
	public ArrayList<String> TablesforARole(String user) throws SQLException
	{
		connOpen();
		ArrayList<String> tablellist = new ArrayList<String>();
		ResultSet tables = executeSqlSelect("SELECT * FROM LRS_MOH.[dbo].[DB_Roles_to_Table_View_Access] WHERE Role_For_Select = '" + user + "'");
			
		while(tables.next())
			{
				String table = "LRS_MOH." + tables.getString("Table_Schema") + "." + tables.getString("Table_Name");
				//System.out.println(table);
				tablellist.add(table);
			}
		
		connClose();	
		return (ArrayList<String>) tablellist;
	}			
	
}//Class

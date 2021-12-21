package EDW.Edward;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.util.Properties;

public class containerLoad extends BaseClass{
	BaseClass bc = new BaseClass();
	static Connection con;
	static Properties prop;
	
		@Before
		public void openConn() throws SQLException, Exception
		{
			bc.connOpen();
		}	
	
	
		@Test
		public void test() throws Exception
		{
//			bc.copyACoantinertoSIT3DropFolder(17,"7448-003"); 
//			bc.copyACoantinertoSIT3DropFolder(36,"20867-01"); 

			//----- File handler--------

//			bc.runJob("FileHandler_AC");
			
			// Job status.
			//bc.jobStatus("FileHandler_AC");
			//String jobstat = bc.jobStatus("FileHandler_AC");
			// System.out.println(jobstat);
			
			// ----------AC------------
//			bc.setParamTableForACRun(); 
//			bc.runJob("AC_DC_JobScheduler");
			
			// Job status.
			//bc.jobStatus("AC_DC_JobScheduler");
			
			// ----------- DC Run -------------
			// Individual Flags.
//			bc.setParamVal("DISTRunFlag", "Y");
//			bc.setParamVal("PROVRunFlag", "N");
//			bc.setParamVal("PLPRunFlag", "N");
			
			// E2E DC Run
//			bc.setParamTableForPlpProvDist();
//			bc.setParamTableForDCRun();
//		    bc.runJob("AC_DC_JobScheduler");
		
		}
	
		@After
		public void closeConn() throws SQLException
		{
			bc.connClose();
		}

}

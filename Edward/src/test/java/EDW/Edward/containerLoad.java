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

			//----- File handler--------
			
//			 bc.runJob("FileHandler_AC");
//			 bc.jobStatus("FileHandler_AC");
//			 String jobstat = bc.jobStatus("FileHandler_AC");
//			 System.out.println(jobstat);
			
			// ----------AC------------
//			bc.setParamTableForACRun(); 
//			bc.runJob("AC_DC_JobScheduler");
//			bc.jobStatus("AC_DC_JobScheduler");
			
			// ----------- DC Run -------------
//			bc.setParamVal("PLPRunFlag", "N");
//			bc.setParamVal("DISTRunFlag", "Y");
			bc.setParamTableForPlpProvDist();
			bc.setParamTableForDCRun();
		    bc.runJob("AC_DC_JobScheduler");
		    String status =  bc.jobStatus("AC_DC_JobScheduler");
		    System.out.println(status);
		
		}
	
		@After
		public void closeConn() throws SQLException
		{
			bc.connClose();
		}

}

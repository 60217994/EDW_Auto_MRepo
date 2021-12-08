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
//			bc.copyACoantinertoSIT3DropFolder(18,"7448-001"); 
//			bc.copyACoantinertoSIT3DropFolder(36,"20867-01"); 
//			bc.runJob("FileHandler_AC");
//			bc.jobStatus("FileHandler_AC");
//			 String jobstat = bc.jobStatus("FileHandler_AC");
//			 System.out.println(jobstat);
			
			// ----------AC------------
//			bc.setParamTableForACRun(); 
//			bc.runJob("AC_DC_JobScheduler");
//			//bc.jobStatus("AC_DC_JobScheduler");
			
			// ----------- DC Run -------------
//			bc.setParamVal("DISTRunFlag", "N");
//			bc.setParamVal("PROVRunFlag", "N");
//			bc.setParamVal("PLPRunFlag", "Y");
			bc.setParamTableForPlpProvDist();
			bc.setParamTableForDCRun();
		    bc.runJob("AC_DC_JobScheduler");
		
		}
	
		@After
		public void closeConn() throws SQLException
		{
			bc.connClose();
		}

}

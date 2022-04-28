package EDW.Edward;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
//			bc.copyACoantinertoSIT3DropFolder(5,"7448-005"); //(5,"7448-003");
//			//NAP 23
//			bc.copyACoantinertoSIT3DropFolder(23,"7448-001A");
////			//----- File handler--------
//			bc.runJob("FileHandler_AC", false);
			
					// Job status.
					//bc.jobStatus("FileHandler_AC");
					//String jobstat = bc.jobStatus("FileHandler_AC");
					// System.out.println(jobstat);
			
			// ----------AC------------
//			bc.setParamTableForACRun(); 
//			bc.runJob("AC_DC_JobScheduler", true);
			
					// Job status.
					//bc.jobStatus("AC_DC_JobScheduler");
			
			// ----------- DC Run -------------
//			// Individual Flags.
//			bc.setParamVal("PLPRunFlag", "Y");
//			bc.setParamVal("PROVRunFlag", "Y");
			bc.setParamVal("DISTRunFlag", "Y");
			
//			// E2E DC Run
//			bc.setParamTableForPlpProvDist();
//			// DC Run
//			bc.setParamTableForDCRun();
//		    bc.runJob("AC_DC_JobScheduler", false);		
		}
	
		@After
		public void closeConn() throws SQLException
		{
			bc.connClose();
		}

}

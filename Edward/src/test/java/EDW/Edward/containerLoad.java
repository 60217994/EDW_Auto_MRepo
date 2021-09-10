package EDW.Edward;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.apache.commons.io.FileUtils;
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
			bc.getNextContSearchString(1,"7448-003");
			//bc.copyACoantinertoSIT3DropFolder(1,"7448-003"); 
			//bc.copyACoantinertoSIT3DropFolder(23,"7760-002");
			//bc.copyACoantinertoSIT3DropFolder(17,"7448-003");
			//bc.copyACoantinertoSIT3DropFolder(18,"7448-001"); 
//  		bc.copyACoantinertoSIT3DropFolder(5,"7448-003");
//			bc.runJob("FileHandler_AC");
			
			// ----------AC------------
			//bc.setParamTableForACRun(); 
			//bc.runJob("AC_DC_JobScheduler");
			
			// ----------- DC Run -------------
//			bc.setParamVal("PROVRunFlag", "N");
//			bc.setParamVal("DISTRunFlag", "N");
			//bc.setParamTableForPlpProvDist();
//			bc.setParamTableForDCRun();
//		    bc.runJob("AC_DC_JobScheduler");
			
		}
		
		@After
		public void closeConn() throws SQLException
		{
			bc.connClose();
		}

}

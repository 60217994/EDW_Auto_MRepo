package EDW.Edward;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class E2EDataLoad extends BaseClass
{

	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpen();
	}
	
	@Test
	public void loadingContainersForAllStream() throws SQLException, Exception
	{
		// Copies files from P01 server to "DROP" Folder of SIT3 server
		try { 
			 //Load CL 1 
			  bc.copyACoantinertoSIT3DropFolder(1,"7448-002"); 
			 //ISP 2
			  bc.copyACoantinertoSIT3DropFolder(2,"9475-001");
			 //ISP 3
			  bc.copyACoantinertoSIT3DropFolder(3,"7760-001");
			 //NAP 5
			  bc.copyACoantinertoSIT3DropFolder(5,"7448-003");
			 //WL 6
			  bc.copyACoantinertoSIT3DropFolder(6,"7448-003"); 
			 //AP 17
			  bc.copyACoantinertoSIT3DropFolder(17,"7448-003");
			 //ED 18
			  bc.copyACoantinertoSIT3DropFolder(18,"7448-001"); 
			 //NAP 23
			  bc.copyACoantinertoSIT3DropFolder(23,"7760-002");
			 //NAP 29
			  bc.copyACoantinertoSIT3DropFolder(29,"7546-001b"); 
			 //NAP 36
			  bc.copyACoantinertoSIT3DropFolder(36,"20867-01"); 
			 //20867-01");
			}  
		
		catch (SQLException e) 
		{
			e.printStackTrace();
		}			
		
		//Run Filehandler_AC schedule job
	    bc.runJob("FileHandler_AC");
	    
	    //Setup run for AC by Making AC flag "Y" and DC flag N in Parameter table
	    bc.setParamTableForACandDCRun();
	    
	    // Run the AC Job
	    //bc.runJob("AC_DC_JobScheduler");
			
	}
	
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}

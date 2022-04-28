package EDW.Edward;

import java.lang.reflect.Array;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class randtests extends BaseClass
{

	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpen(); // windows auth
	}
	
	@Test
	public void jobStatus() throws SQLException, Exception
	{
//		ArrayList<String> tables = new ArrayList<String>(Arrays.asList("HIE_DA_EPISODE","HIE_DA_OTHER_DRUGS","HIE_DA_OTHER_SERVICES","HIE_DA_PREVIOUS_TREATMENT","HIE_DA_SERVICE_CONTACTS","HIE_PATIENT_CONTACT_DETAILS"));
//		
//		for(int i= 0; i< tables.size(); i++)
//		{
//			bc.printMetaDataForCoreTable(tables.get(i));
//		}
		
		//bc.DZTableProperties("DS_54_256_HIE_DA_EPISODE");
		bc.coreTablesForAStreamUsingAuditLogs(23);
	}
	
	
    @After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
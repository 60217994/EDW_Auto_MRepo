package EDW.Edward;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class randtests extends BaseClass
{

	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpen();
	}
	
	@Test
	public void jobStatus() throws SQLException, Exception
	{
		//bc.collHeadersIntable("CL_SE");
		//bc.findCbkForaTableInDZ("DIAGNOSIS");
		//bc.findCbkForaTableInCore("SVC_ACT");
		//bc.comparefactoryToCore(39);
//		bc.findCbkForaTableInCore("SE_ATTR");
//		System.out.println("------------------------------------------------------------------");
		bc.coreTablesForAStreamUsingAuditLogs(23);
//		bc.nonCbkColumnsForaTableInCore("SE_ATTR");
		bc.nonCbkColumnsForaTableInCoreForStream(23);
		
	}
	
	
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
package EDW.Edward;

import java.lang.reflect.Array;
import java.sql.DriverManager;
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
		bc.connOpen(); // windows auth
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
//		bc.coreTablesForAStreamUsingAuditLogs(23);
//		bc.nonCbkColumnsForaTableInCoreUsingFactory("SVC_ACT");
//		bc.findCbkForaTableInCore("SVC_ACT");
//		bc.nonCbkColumnsForaTableInCoreUsingCoreTable("SVC_ACT");
//		bc.nonCbkColumnsForaTableInCoreUsingFactoryJoiningInforationSchema("SVC_ACT");
//		bc.CheckCheckSumHashForATable("SVC_ACT" , 7452);
//		bc.coreTablesForAStreamusingStage(1);
//		bc.coreTablesForAStreamusingStage(17);
//		bc.coreTablesForAStreamusingFactory(54);
//		bc.coreTablesForAStreamusingStage(54);
//		printMetaDataForCoreTable(53);
//		bc.CheckCBKForATable("SVC_ACT" , 7452);
		
		
	}
	
	
    @After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
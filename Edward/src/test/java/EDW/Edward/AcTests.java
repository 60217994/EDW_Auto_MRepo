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

public class AcTests extends BaseClass
{

	BaseClass bc = new BaseClass();
	int stream = 17;
	String source = "7448-003";
	int edwcontid  = 7452;
	
	@Before
	public void openConn()
	{
		bc.connOpen();
	}
	
	@Test
	public void step0() throws SQLException, Exception
	{
		
		// Meta Data tests
		bc.comparefactoryToCore(stream);
		
	}
	
	//@Test
	public void step1() throws SQLException, Exception
	{
		
		// Load the next container.
		bc.copyACoantinertoSIT3DropFolder(stream,source); 
		
	}
	
	//@Test
	public void step2() throws SQLException, Exception
	{
		// CBK test
		bc.findCbkForaTableInCore(db);
		
		//CheckSum Comparison
		bc.CheckCheckSumHashForATable("SVC_ACT" , 7452);
	}
		
	//@Test
	public void step3() throws SQLException, Exception
	{
		// Counts Tests
		bc.countsMatchingbetweenDZandCore(stream,source, edwcontid);
		
		//
		bc.updateAllColumnsForCoreTables(stream);
		
		//
		bc.updateSomeRecordsAsLogicalDeletesInAContainer(source,stream, "SVC_ACT");
	}
	
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
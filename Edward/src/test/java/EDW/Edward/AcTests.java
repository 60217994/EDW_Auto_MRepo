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
	
	@Before
	public void openConn()
	{
		bc.connOpen();
	}
	
	@Test
	public void jobStatus() throws SQLException, Exception
	{
		// Meta Data tests
		bc.comparefactoryToCore(17);
		
		// Load the next container.
		bc.copyACoantinertoSIT3DropFolder(17,"7448-003"); 
		
		
		//write next conatiner string for a stream and source
		
		// CBK test
		bc.findCbkForaTableInCore(db);
		
		
		// Counts Tests
		bc.countsMatchingbetweenDZandCore(17,"7448-003", 0);
		
		//
		bc.updateAllColumnsForCoreTables(17);
		
		//
		bc.updateSomeRecordsAsLogicalDeletesInAContainer("7448-003",17, db);
	}
	
	
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
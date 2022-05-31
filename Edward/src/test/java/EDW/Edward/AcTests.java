package EDW.Edward;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AcTests extends BaseClass
{
	
	// Parameters
	int stream = 17;
	String source = "7448-003";
	int contseqnum = 2887;
	int edwcontid  = 8072;
	
	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpen();
	}
	
	@Test
	public void step0() throws SQLException, Exception
	{
		
		// Step 1: Meta Data tests
		bc.comparefactoryToCore(stream);
		
	}
	
	//@Test
	public void step1() throws SQLException, Exception
	{
		
		// Step 2: Run This step after "STEP1".
		//Load the next container.
		bc.copyAContainertoSIT3DropFolder(stream,source); 
		
	}
	
	//@Test
	public void step2() throws SQLException, Exception
	{
		// Step 3: Run This after the previous step has been completed..
		ArrayList<String> coretables = coreTablesForAStreamexcludingDerivedTablesusingFactory(stream);
		// CBK test
		for(String i : coretables)
		{
			bc.CheckCBKForATable(i, edwcontid);
		}
		
		//CheckSum Comparison
		bc.CheckCheckSumHashForAllCoreTablesInAStream(stream, edwcontid); // ORDINAL_POSITION Order has not been followed, derived checksum is not same.
		
		// Counts Tests
		bc.countsMatchingbetweenDZandCore(stream,source, contseqnum);
		
		bc.logicalDeletecountsMatchBetweenDZandCore(stream,source, contseqnum);
//		bc.updateSomeRecordsAsLogicalDeletesInAContainer(source,stream, "SVC_ACT"); // Unfinished method.
	}
		
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
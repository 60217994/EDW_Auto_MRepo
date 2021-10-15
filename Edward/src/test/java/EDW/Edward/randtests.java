package EDW.Edward;

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
		bc.copyACoantinertoSIT3DropFolder(17,"7448-003");
		//bc.generateCountsScripts(17, "7448-003", 2830);
		//bc.MaxlengthForTable("SE","dbo");
		//bc.comparefactoryToCore(1);
		//bc.collHeadersIntable("DIAG");
		//bc.generateUpdateScripts(17, "7448-001",3129);
//		bc.EdwContainerForASequence(17, "7448-003", 3330);
//		bc.EdwContainerForASequence(18, "7448-003", 3330);
//		bc.coreTablesForAStream(55);
//		bc.coreTablesForAStream(53);
		
		
	}
	
	
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
package EDW.Edward;

import java.sql.SQLException;
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
	public void loadingContainersForAllStream() throws SQLException, Exception
	{
		/*
		 * int streamid = 1; ArrayList<String> tables = bc.DZTablesForAStream(streamid);
		 * String table = tables.get(0);
		 * 
		 * System.out.println(table);
		 */
		bc.sksInACoreTable("SE");
		
			
	}
	
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
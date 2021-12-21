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
		bc.coreTablesForAStreamUsingAuditLogs(55);
		bc.DZTablesForAStreamUsingAuditLogs(55);
		
		bc.coreTablesForAStreamUsingAuditLogs(56);
		bc.DZTablesForAStreamUsingAuditLogs(56);
		bc.DZTablesForAStream(56);
	}
	
	
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
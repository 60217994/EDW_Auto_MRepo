package EDW.Edward;

import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class cbkAndChecksumChecks extends BaseClass
{

	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpen();
	}
	
	@Test
	public void cbkCheck() throws SQLException, Exception
	{
		bc.findCbkForaTableInCore("SE_CANCER_TRT");
		System.out.println("------------------------------------------------------------------");
	}
	
	@After
	public void closeConn() throws SQLException
	{
		bc.connClose();
	}
	
	
}
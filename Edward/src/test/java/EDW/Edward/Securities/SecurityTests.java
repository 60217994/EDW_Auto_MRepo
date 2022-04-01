package EDW.Edward.Securities;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import EDW.Edward.BaseClass;

public class SecurityTests {
	
BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpenSqlAuth("LRS_MOH", "Suresh_WL_All", "Mynameis1234"); // sql auth
	}
	
	@Test
	public void jobStatus() throws SQLException, Exception
	{
		try 
		{
			int counts = bc.countsForATable("LRS_MOH.[CERTIFIED].v_FACT_AP_SE");
			System.out.println(counts);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
	}
	
    @After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}

}

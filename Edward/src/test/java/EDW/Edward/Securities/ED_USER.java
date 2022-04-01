package EDW.Edward.Securities;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import EDW.Edward.BaseClass;

public class ED_USER {
	
	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpenSqlAuth("LRS_MOH", "Suresh_ED_ALL", "Mynameis1234"); // sql auth
	}
	
	@Test
	public void jobStatus() throws SQLException, Exception
	{
		List<String> edtables = Arrays.asList("");
		for(int i=0; i < edtables.size() ; i++)
		{
			try 
			{
				int counts = bc.countsForATable("LRS_MOH.CERTIFIED." + edtables.get(i));
				System.out.println(edtables.get(i)+" : " + counts);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		
		}
	}
	
    @After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}


}

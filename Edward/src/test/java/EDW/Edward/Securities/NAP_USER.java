package EDW.Edward.Securities;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import EDW.Edward.BaseClass;

public class NAP_USER {
	
	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpenSqlAuth("LRS_MOH", "Suresh_NAP_ALL", "Mynameis1234"); // sql auth
	}
	
	@Test
	public void jobStatus() throws SQLException, Exception
	{
		List<String> naptables = Arrays.asList("");
		for(int i=0; i < naptables.size() ; i++)
		{
			try 
			{
				int counts = bc.countsForATable("LRS_MOH.CERTIFIED." + naptables.get(i));
				System.out.println(naptables.get(i)+" : " + counts);
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

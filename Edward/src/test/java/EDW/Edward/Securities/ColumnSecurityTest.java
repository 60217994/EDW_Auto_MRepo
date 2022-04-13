package EDW.Edward.Securities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import EDW.Edward.BaseClass;

public class ColumnSecurityTest {
	
BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpen();
	}
	
	@Test
	public void jobStatus() throws SQLException, Exception
	{
		// Get Distinct tables from [LRS_MOH].[dbo].[PII Masking Level of Columns] table
		ArrayList<String> tables = new ArrayList<String>();
		String tablename = "";
		ArrayList<String> columns = new ArrayList<String>();
		String allcolumns = "";
		
		ResultSet rs  = bc.executeSqlSelect("SELECT DISTINCT TABLENAME FROM [LRS_MOH].[dbo].[PII Masking Level of Columns]");
		while(rs.next())
		{
			tablename = rs.getString("TABLENAME");
			tables.add(tablename);
		}
		System.out.println(tables);

//		bc.connClose();
//		bc.connOpenSqlAuth("LRS_MOH", "Suresh_WL_All", "Mynameis1234"); // sql auth
	}
	
    @After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}

}

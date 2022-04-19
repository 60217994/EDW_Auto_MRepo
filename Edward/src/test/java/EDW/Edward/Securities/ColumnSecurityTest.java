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
		String columnname = "";
		String allcolumns = "";
		
		ResultSet rs  = bc.executeSqlSelect("SELECT DISTINCT TABLENAME FROM [LRS_MOH].[dbo].[PIIMaskingLevelofColumns]");
		while(rs.next())
		{
			tablename = rs.getString("TABLENAME");
			tables.add(tablename);
		}
		System.out.println(tables);
		System.out.println(tables.size());
		for(int i = 0; i < tables.size() ; i++)
		{
			// To get all columns for a table and pass it to main query
			ResultSet rsc  = bc.executeSqlSelect("SELECT * FROM [LRS_MOH].[dbo].[PIIMaskingLevelofColumns] WHERE TABLENAME = '" + tables.get(i) + "' ORDER BY Columnname DESC");
			while (rsc.next())
			{ 
				columnname = rsc.getString("COLUMNNAME");
				allcolumns = columnname + "," + allcolumns;
			}
			//System.out.println(tables.get(i));
			allcolumns = allcolumns.substring(0, allcolumns.length() - 1);  ;
			System.out.println(tables.get(i) + ": " + allcolumns);
			// Design Queries and run the query from various LRS users (Ex: Fully-Identified, Semi-Identified and De-Identified)
			ResultSet rss  = bc.executeSqlSelect("SELECT TOP(10) " + allcolumns + " FROM [LRS_MOH]." + tables.get(i));
			System.out.println("SELECT TOP(10) " + allcolumns + " FROM [LRS_MOH]." + tables.get(i));
//			while (rss.next())
//			{ 
//				
//			}
			
			columnname = "";
			allcolumns = "";
		}

//		bc.connClose();
//		bc.connOpenSqlAuth("LRS_MOH", "Suresh_WL_All", "Mynameis1234"); // sql auth
	}
	
    @After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}

}

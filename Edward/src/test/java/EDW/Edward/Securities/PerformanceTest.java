package EDW.Edward.Securities;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Test;

import EDW.Edward.BaseClass;

public class PerformanceTest {
BaseClass bc = new BaseClass();

	
	@Test
	public void performaceCheck() throws SQLException, Exception
	{
		// Ensure the [LRS_MOH].[dbo].[DBRolestoTableViewAccess] is upto date with the requirement in "https://nswhealth.sharepoint.com/:f:/r/sites/EDWARD-MoH-EDWARDDocumentation/Shared%20Documents/EDWARD%20Documentation/99%20-%20Documentation/000%20-%20As%20Built/06%20Security/R12?csf=1&web=1&e=3mdoW4"
		//All tables in DBrolestoTableViewAccess
		List<String> alltables = bc.allTables();
		System.out.println("Total tables in the list :" + alltables.size());
	
		String countsandtime = "";
		try 
		{
			bc.connOpenSqlAuth("LRS_MOH", "Suresh_Datareader", "Password123"); // sql auth 
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		for(int i=0; i < alltables.size() ; i++)
		{
			try 
			{
				countsandtime = bc.countsForATableForPerformance(alltables.get(i));
				String[] text = countsandtime.split("\\|");
				
				System.out.println(alltables.get(i)+" : " + text[0] + " , Time taken to run counts: "  + text[1] + " milli Seconds");
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
		bc.connClose();
	
	}
}

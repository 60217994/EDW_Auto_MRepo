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
		//All tables in DBrolestoTableViewAccess
		List<String> alltables = bc.allTables();
		System.out.println("Total tables in the list :" + alltables.size());
	
		String countsandtime = "";
		try 
		{
			bc.connOpenSqlAuth("LRS_MOH", "Suresh_Datareader", "Mynameis1234"); // sql auth 
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

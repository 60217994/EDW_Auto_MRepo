package EDW.Edward.Securities;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import EDW.Edward.BaseClass;

public class PERI_USER {
BaseClass bc = new BaseClass();

	
	@Test
	public void ApSecuritiesCheck() throws SQLException, Exception
	{
		// Ensure the [LRS_MOH].[dbo].[DBRolestoTableViewAccess] is upto date with the requirement in "https://nswhealth.sharepoint.com/:f:/r/sites/EDWARD-MoH-EDWARDDocumentation/Shared%20Documents/EDWARD%20Documentation/99%20-%20Documentation/000%20-%20As%20Built/06%20Security/R12?csf=1&web=1&e=3mdoW4"
		//PERI tables
		List<String> peritables = bc.TablesforARole("LRS_USER_PERI") ;
		
	    //UNSEC tables list
		List<String> unsectables = bc.TablesforARole("LRS_USER_UNSEC");
				
		//Negative test list
		List<String> nonperitables = bc.TablesforARole("LRS_USER_ED");
		
		// PERI tables check
		System.out.println("----------------------- PERI Tables and Views(Should have permissions) ----------------------------");
		int counts = 0;
		try 
		{
			bc.connOpenSqlAuth("LRS_MOH", "Suresh_PERI_All", "Password123"); // sql auth
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		for(int i=0; i < peritables.size() ; i++)
		{
			try 
			{
				counts = bc.countsForATable(peritables.get(i));
				System.out.println(peritables.get(i)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
		// For Non PERI Tables
		System.out.println("----------------------- non AP Tables and views(Should not have permissions) ----------------------------");
		counts = 0;
		for(int j=0; j < nonperitables.size() ; j++)
		{
			try 
			{
				counts = bc.countsForATable(nonperitables.get(j));
				System.out.println(nonperitables.get(j)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
		// For UnSecure Tables
		System.out.println("----------------------- UnSecure Tables(Should have permissions) ----------------------------");
		counts = 0;
		for(int u=0; u < unsectables.size() ; u++)
		{
			try 
			{
				counts = bc.countsForATable(unsectables.get(u));
				System.out.println(unsectables.get(u)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}	
				
		}	
		bc.connClose();
				
	}
}

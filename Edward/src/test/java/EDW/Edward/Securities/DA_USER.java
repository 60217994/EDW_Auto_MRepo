package EDW.Edward.Securities;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import EDW.Edward.BaseClass;

public class DA_USER {
	
BaseClass bc = new BaseClass();

	
	@Test
	public void DASecuritiesCheck() throws SQLException, Exception
	{
		// Ensure the [LRS_MOH].[dbo].[DBRolestoTableViewAccess] is upto date with the requirement in (https://nswhealth.sharepoint.com/:f:/r/sites/EDWARD-MoH-EDWARDDocumentation/Shared%20Documents/EDWARD%20Documentation/99%20-%20Documentation/000%20-%20As%20Built/06%20Security/R12?csf=1&web=1&e=3mdoW4)
		//DA tables
		List<String> datables = bc.TablesforARole("LRS_USER_HIE_INTERIM_DA") ;
		
	    //UNSEC tables list
		List<String> unsectables = bc.TablesforARole("LRS_USER_UNSEC");
				
		//Negative test list
		List<String> nondatables = bc.TablesforARole("LRS_USER_ED");
		
		// AP tables check
		System.out.println("----------------------- DA Tables(Should have permissions) ----------------------------");
		int counts = 0;
		
		try 
		{
			bc.connOpenSqlAuth("LRS_MOH", "Suresh_DA_ALL", "Password123"); // sql auth
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		for(int i=0; i < datables.size() ; i++)
		{
			try 
			{
				counts = bc.countsForATable(datables.get(i));
				System.out.println(datables.get(i)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
		// For Non AP Tables
		System.out.println("----------------------- non DA Tables(Should not have permissions. Denied : function as expected.) ----------------------------");
		counts = 0;
		for(int j=0; j < nondatables.size() ; j++)
		{
			try 
			{
				counts = bc.countsForATable(nondatables.get(j));
				System.out.println(nondatables.get(j)+" : " + counts);
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

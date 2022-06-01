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
	
	@Test
	public void ApSecuritiesCheck() throws SQLException, Exception
	{
		// Ensure the [LRS_MOH].[dbo].[DBRolestoTableViewAccess] is upto date with the requirement in "https://nswhealth.sharepoint.com/:f:/r/sites/EDWARD-MoH-EDWARDDocumentation/Shared%20Documents/EDWARD%20Documentation/99%20-%20Documentation/000%20-%20As%20Built/06%20Security/R12?csf=1&web=1&e=3mdoW4"
		//ED tables (Positive test)
		List<String> edtables = bc.TablesforARole("LRS_USER_ED") ;
		
	    //UNSEC tables list
		List<String> unsectables = bc.TablesforARole("LRS_USER_UNSEC");
				
		//Negative test list
		List<String> nonedtables = bc.TablesforARole("LRS_USER_AP");;
		
		// AP tables check
		System.out.println("----------------------- ED Tables(Should have permissions) ----------------------------");
		bc.connOpenSqlAuth("LRS_MOH", "Suresh_ED_ALL", "Password123"); // sql auth
		for(int i=0; i < edtables.size() ; i++)
		{
			try 
			{
				int counts = bc.countsForATable(edtables.get(i));
				System.out.println(edtables.get(i)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
		// For Non AP Tables
		System.out.println("----------------------- non ED Tables(Should not have permissions) ----------------------------");
		for(int j=0; j < nonedtables.size() ; j++)
		{
			try 
			{
				int counts = bc.countsForATable(nonedtables.get(j));
				System.out.println(nonedtables.get(j)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
		// For UnSecure Tables
		System.out.println("----------------------- UnSecure Tables(Should have permissions) ----------------------------");
		for(int u=0; u < unsectables.size() ; u++)
		{
			try 
			{
				int counts = bc.countsForATable(unsectables.get(u));
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

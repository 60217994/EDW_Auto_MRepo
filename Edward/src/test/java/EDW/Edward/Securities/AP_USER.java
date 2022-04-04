package EDW.Edward.Securities;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import EDW.Edward.BaseClass;

public class AP_USER {
	
	BaseClass bc = new BaseClass();

	
	@Test
	public void ApSecuritiesCheck() throws SQLException, Exception
	{
		//AP tables
		List<String> aptables = bc.TablesforAUser("LRS_USER_AP") ;
		
	    //UNSEC tables list
		List<String> unsectables = bc.TablesforAUser("LRS_USER_UNSEC");
				
		//Negative test list
		List<String> nonaptables = bc.TablesforAUser("LRS_USER_ED");;
		
		// AP tables check
		System.out.println("----------------------- AP Tables ----------------------------");
		bc.connOpenSqlAuth("LRS_MOH", "Suresh_AP_ALL", "Mynameis1234"); // sql auth
		for(int i=0; i < aptables.size() ; i++)
		{
			try 
			{
				int counts = bc.countsForATable(aptables.get(i));
				System.out.println(aptables.get(i)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
		// For Non AP Tables
		System.out.println("----------------------- non AP Tables ----------------------------");
		for(int j=0; j < nonaptables.size() ; j++)
		{
			try 
			{
				int counts = bc.countsForATable(nonaptables.get(j));
				System.out.println(nonaptables.get(j)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
		// For UnSecure Tables
		System.out.println("----------------------- UnSecure Tables ----------------------------");
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
				
	}
	
}

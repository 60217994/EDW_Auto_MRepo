package EDW.Edward.Securities;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import EDW.Edward.BaseClass;

public class ClientTableUserIdentified_USER 
{

BaseClass bc = new BaseClass();

	@Test
	public void ApSecuritiesCheck() throws SQLException, Exception
	{
		//Client Table User Identified tables
		List<String> ctuitables = bc.TablesforARole("CLIENT_TABLE_USER_IDENTIIFED") ;
		
//	    //UNSEC tables list
//		List<String> unsectables = bc.TablesforARole("LRS_USER_UNSEC");
				
		//Negative test list
		List<String> nonctuitables = bc.TablesforARole("LRS_USER_ED");
		
		// Client Table User Identified tables check
		System.out.println("----------------------- Client Table User Identified Tables(Should have permissions) ----------------------------");
		int counts = 0;
		try 
		{
			bc.connOpenSqlAuth("LRS_MOH", "Suresh_AP_ALL", "Mynameis1234"); // sql auth
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		for(int i=0; i < ctuitables.size() ; i++)
		{
			try 
			{
				counts = bc.countsForATable(ctuitables.get(i));
				System.out.println(ctuitables.get(i)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
		// For Non CTUI Tables
		System.out.println("----------------------- non AP Tables(Should not have permissions) ----------------------------");
		counts = 0;
		for(int j=0; j < nonctuitables.size() ; j++)
		{
			try 
			{
				counts = bc.countsForATable(nonctuitables.get(j));
				System.out.println(nonctuitables.get(j)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
		
//		// For UnSecure Tables
//		System.out.println("----------------------- UnSecure Tables(Should have permissions) ----------------------------");
//		counts = 0;
//		for(int u=0; u < unsectables.size() ; u++)
//		{
//			try 
//			{
//				counts = bc.countsForATable(unsectables.get(u));
//				System.out.println(unsectables.get(u)+" : " + counts);
//			}
//			catch (SQLException e)
//			{
//				System.out.println(e.getMessage());
//			}	
//				
//		}	
		bc.connClose();
				
	}
}

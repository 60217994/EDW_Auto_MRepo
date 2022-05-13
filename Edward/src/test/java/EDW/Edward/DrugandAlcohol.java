package EDW.Edward;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DrugandAlcohol {

BaseClass bc = new BaseClass();
// DZ tables
ArrayList<String> dztables = new ArrayList<>();
// Core tables
ArrayList<String> coretables = new ArrayList<String>(Arrays.asList("HIE_DA_EPISODE","HIE_DA_OTHER_DRUGS","HIE_DA_OTHER_SERVICES","HIE_DA_PREVIOUS_TREATMENT","HIE_DA_SERVICE_CONTACTS","HIE_PATIENT_CONTACT_DETAILS"));
static Connection con;


	@Before
	public void openConn()
	{
		bc.connOpen(); // windows auth
	}
	
    @After
	public void closeConn() throws SQLException, Exception
	{
			bc.connClose();
	}
	// DZ Meta Data tests
	//@Test
	public void dzMetaData() throws SQLException, Exception
	{
		dztables = bc.DZTablesForAStream(54);
		for(int i = 0; i< dztables.size(); i++)
		{
			bc.DZTableProperties(dztables.get(i));
		}
		
	}
	// Core Meta Data tests
	//@Test
	public void coreMetaData() throws SQLException, Exception
	{
	
		for(int i = 0; i< coretables.size(); i++)
		{
			bc.printMetaDataForCoreTable(coretables.get(i));
		}
		
	}
	
	@Test
	public void CBKCheck() throws SQLException, Exception
	{
		// Core tables
		bc.coreTablesForAStreamUsingAuditLogs(54);
		
		// User defined cbks
		String cbk_Episode = "ESTABLISHMENT_IDENTIFIER + CONTACT_IDENTIFIER";
		String cbk_Other_drugs = "ESTABLISHMENT_IDENTIFIER + EPISODE_IDENTIFIER + OTHER_DRUG_GAMBLING";
		String cbk_other_services= "ESTABLISHMENT_IDENTIFIER + EPISODE_IDENTIFIER + OTHER_SERV_PROVIDED";
		String cbk_previous_treatment= "ESTABLISHMENT_IDENTIFIER + EPISODE_IDENTIFIER + PREV_SERV_RECEIVED";
		String cbk_services_contacts= "ESTABLISHMENT_IDENTIFIER + EPISODE_IDENTIFIER + SERVICE_CONTACT_DATE";
		String cbk_patient_contact_details ="ESTABLISHMENT_IDENTIFIER + CONTACT_IDENTIFIER";
		
		String checksumv = new String();
		String checksum = new String();
		String checksumf = new String();
		
		String server = "CLDREDW-SIT3DB1.nswhealth.net";
		Properties prop;

		String	db = "EDW2";
		String	dzdb = "DZ";
		
		try 
		{
			// Integrated authentication.
			con =  DriverManager.getConnection("jdbc:sqlserver://" + server + ";"+"DatabaseName=" + db + ";"+ "integratedSecurity=true");
			//System.out.println("Connection is Open.");
		} 
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		Statement stmt = (Statement) con.createStatement();
		
		for(int i = 0; i< coretables.size(); i++)
		{ 
			checksumv = "";
			checksumf = "";
			checksum = "";
			
			// Query Joining Factory and Information schema to get non cbk columns.
			String sql = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS I WHERE I.COLUMN_NAME NOT LIKE '%CONTAINER_ID'  AND I.COLUMN_NAME NOT LIKE 'EDW_%' AND I.COLUMN_NAME NOT LIKE 'SOURCE_%'"
					+ "AND I.COLUMN_NAME NOT IN ('SRC_CREATE_DTTM', 'SRC_MOD_DTTM','EDW_EFFT_START_DTTM','EDW_EFFT_END_DTTM') AND I.COLUMN_NAME NOT LIKE '%SK' AND I.COLUMN_NAME NOT LIKE '%CBK' "
					+ "AND TABLE_NAME = '" + coretables.get(i) + "'";
		 	ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		 	
		 	//System.out.println(sql);
		 	while(rs.next())
		 	{
		 		String checksumcols =rs.getString("COLUMN_NAME");
				if( rs.getString("DATA_TYPE").equals("datetime2") || rs.getString("DATA_TYPE").equals("date"))
				{
					checksumv = "TRY_CONVERT([" + rs.getString("DATA_TYPE") + "], case when ["+ checksumcols + "]='' then 'NULL DATE' else [" + checksumcols + "] end,(126))";
					
				}
				else if( rs.getString("DATA_TYPE").equals("int") || rs.getString("DATA_TYPE").equals("numeric") || rs.getString("DATA_TYPE").equals("decimal"))
				{
					checksumv = "TRY_CAST([" + checksumcols + "] AS ["  + rs.getString("DATA_TYPE") + "]("+ rs.getString("NUMERIC_PRECISION") + "," + rs.getString("NUMERIC_SCALE") +"))";
					
				}
				else
				{
					checksumv = "TRY_CAST([" + checksumcols + "] AS ["  + rs.getString("DATA_TYPE") + "]("+ rs.getString("CHARACTER_MAXIMUM_LENGTH") + "))";
				}
					
					
				checksum = checksum + ",'|'," + checksumv;
				//checksum 
				checksumf = "hashbytes('SHA2_256',concat(" + checksum.substring(5) + "))";
		 		
		 	}
		 	System.out.println(coretables.get(i) + ":" + checksumf);
		}
			
	}
   
	
	
	
	
}

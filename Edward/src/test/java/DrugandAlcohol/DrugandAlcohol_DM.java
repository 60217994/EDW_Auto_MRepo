package DrugandAlcohol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import EDW.Edward.BaseClass;

public class DrugandAlcohol_DM {
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
	    
	    // checked CORE and DM Tables and columns.
	    @Test
	    public void columns() throws SQLException
	    {
//	    	bc.printMetaDataForaTable("EDW2", "DBO", "HIE_DA_EPISODE"); // CORE
//	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_EPISODE"); // DM
	    	
	    	bc.printMetaDataForaTable("EDW2", "DBO", "HIE_DA_OTHER_DRUGS");
	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_OTHER_DRUGS");
	  
//	    	bc.printMetaDataForaTable("EDW2", "DBO", "HIE_DA_OTHER_SERVICES"); // CORE
//	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_OTHER_SERVICES"); // DM
	    	
//	    	bc.printMetaDataForaTable("EDW2", "DBO", "HIE_DA_PREVIOUS_TREATMENT"); // CORE
//	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_PREVIOUS_TREATMENT"); // DM
	    	
//	    	bc.printMetaDataForaTable("EDW2", "DBO", "HIE_DA_SERVICE_CONTACTS"); // CORE
//	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_SERVICE_CONTACTS"); // DM
	    	
//	    	bc.printMetaDataForaTable("EDW2", "DBO", "HIE_PATIENT_CONTACT_DETAILS"); // CORE
//	    	bc.printMetaDataForaTable("EDW2", "DM", "PATIENT_CONTACT_DETAILS"); // DM
	    }
	    
}

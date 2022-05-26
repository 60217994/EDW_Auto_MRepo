package DrugandAlcohol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import EDW.Edward.BaseClass;

public class DrugAndAlcohol_Dist {
	BaseClass bc = new BaseClass();
	// DZ tables
	ArrayList<String> dztables = new ArrayList<>();
	// Core tables
	ArrayList<String> coretables = new ArrayList<String>(Arrays.asList("DA_EPISODE","DA_OTHER_DRUGS","DA_OTHER_SERVICES","DA_PREVIOUS_TREATMENT","DA_SERVICE_CONTACTS","PATIENT_CONTACT_DETAILS"));
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
	    public void metaDataTest() throws SQLException
	    {
//	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_EPISODE"); // DM
//	    	bc.printMetaDataForaTable("LRS_MOH", "CERTIFIED", "DA_EPISODE"); // LRS_MOH
	    	
//	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_OTHER_DRUGS"); 
//	    	bc.printMetaDataForaTable("LRS_MOH", "CERTIFIED", "DA_OTHER_DRUGS");
	  
//	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_OTHER_SERVICES"); // DM
//	    	bc.printMetaDataForaTable("LRS_MOH", "CERTIFIED", "DA_OTHER_SERVICES"); // LRS_MOH
	    	
//	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_PREVIOUS_TREATMENT"); // DM
//	    	bc.printMetaDataForaTable("LRS_MOH", "CERTIFIED", "DA_PREVIOUS_TREATMENT"); // LRS_MOH

//	    	bc.printMetaDataForaTable("EDW2", "DM", "DA_SERVICE_CONTACTS"); // DM
//	    	bc.printMetaDataForaTable("LRS_MOH", "CERTIFIED", "DA_SERVICE_CONTACTS"); // LRS_MOH

	    	bc.printMetaDataForaTable("EDW2", "DM", "PATIENT_CONTACT_DETAILS"); // DM	    	
	    	bc.printMetaDataForaTable("LRS_MOH", "CERTIFIED", "PATIENT_CONTACT_DETAILS"); // LRS_MOH

	    }
}

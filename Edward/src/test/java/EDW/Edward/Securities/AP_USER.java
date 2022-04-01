package EDW.Edward.Securities;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import EDW.Edward.BaseClass;

public class AP_USER {
	
	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpenSqlAuth("LRS_MOH", "Suresh_AP_ALL", "Mynameis1234"); // sql auth
	}
	
	@Test
	public void jobStatus() throws SQLException, Exception
	{
		List<String> aptables = Arrays.asList("FACT_ED_SE","FACT_AP_SE", "FACT_AP_SE_KPI", "FACT_AP_SE_NOTIF_CANCER", "FACT_AP_SE_SEG", "FACT_AP_SE_WAU", "FACT_AP_SVC_ENC", "v_FACT_AP_SE", "v_FACT_AP_SE", "v_FACT_AP_SE_DIAGNOSIS", "v_FACT_AP_SE_INTERVENTION", "v_FACT_AP_SE_NOTIF_CA", "v_FACT_AP_SE_NOTIF_CANCER", "v_FACT_AP_SE_NOTIF_CANCER", "v_FACT_AP_SE_SEG", "v_FACT_AP_SE_SEG", "v_FACT_AP_SE_WAU", "v_FACT_AP_SE_WAU", "v_FACT_AP_SERVICE_ENC", "v_FACT_AP_SRV_ENC", "v_FACTF_AP_SE_DIAG", "v_FACTF_AP_SE_INTERVENTION", "v_FACTF_AP_SE_WAU", "v_FACTF_AP_SE_WAU_old");
		for(int i=0; i < aptables.size() ; i++)
		{
			try 
			{
				int counts = bc.countsForATable("LRS_MOH.CERTIFIED." + aptables.get(i));
				System.out.println(aptables.get(i)+" : " + counts);
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		}
	}
	
    @After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}


}

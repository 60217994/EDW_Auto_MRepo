package EDW.Edward;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class randtests extends BaseClass
{

	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn()
	{
		bc.connOpen();
	}
	
	@Test
	public void loadingContainersForAllStream() throws SQLException, Exception
	{
		//bc.updateSomeRecordsAsLogicalDeletesInAContainer("13563-01", 67, "CLINICAL_REFERENCE_DOMAIN_VALUE_MAPPING");

		bc.MaxlengthForTable("CLIN_REF_DOMAIN_VALUE_MAP", "dz", "dbo");
		
	}
	
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
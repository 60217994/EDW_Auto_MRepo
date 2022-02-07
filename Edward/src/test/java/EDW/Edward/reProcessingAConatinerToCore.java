package EDW.Edward;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
//import org.junit.Before;
import org.junit.Test;


public class reProcessingAConatinerToCore extends BaseClass
{
	BaseClass bc = new BaseClass();
	
	@Before
	public void openConn() throws SQLException
	{
		bc.connOpen();
	}
	
	@Test
	public void reprocessAContDZToCore() throws Exception, SQLException
	{
		// Input variables
		int stream = 1;
		String sourcesystemcode = "7760-003";
		int contseqnumb = 2896;
		
			//String tablename = "SERVICE_EVENT_CHOC_MINIMUM_DATA_SET"; // to update "create container id" for one table 
//			bc.UpdateDZJobContToReprocess(stream, sourcesystemcode, contseqnumb);
//			bc.CheckJobStausANDCurrentFlag(stream, sourcesystemcode, contseqnumb);
//			bc.UpdateCTLDataContToReprocess(stream, sourcesystemcode, contseqnumb);
//			bc.CheckStausInCTLDataCont(stream, sourcesystemcode, contseqnumb);
//			bc.updateCreateDataContToNull(stream, sourcesystemcode, contseqnumb);
//			bc.setParamTableForACRun();
			bc.runJob("AC_DC_JobScheduler");
		
	}

    @After
	public void closeConn() throws SQLException
	{
		bc.connClose();
	}
	
}




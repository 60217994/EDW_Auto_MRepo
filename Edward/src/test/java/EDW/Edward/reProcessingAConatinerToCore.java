package EDW.Edward;
import java.sql.SQLException;
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
		int stream = 54;
		String sourcesystemcode = "HIE-X710";
		int contseqnumb = 180;
		
			bc.UpdateDZJobContToReprocess(stream, sourcesystemcode, contseqnumb);
			bc.CheckJobStausANDCurrentFlag(stream, sourcesystemcode, contseqnumb);
			bc.UpdateCTLDataContToReprocess(stream, sourcesystemcode, contseqnumb);
			bc.CheckStausInCTLDataCont(stream, sourcesystemcode, contseqnumb);
			bc.updateCreateDataContToNull(stream, sourcesystemcode, contseqnumb);
			bc.setParamTableForACRun();
			bc.runJob("AC_DC_JobScheduler", true);
		
	}

    @After
	public void closeConn() throws SQLException
	{
		bc.connClose();
	}
	
}




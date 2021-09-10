package EDW.Edward;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class randtests extends BaseClass
{

	BaseClass bc = new BaseClass();
	
	//@Before
	public void openConn()
	{
		bc.connOpen();
	}
	
	@Test
	public void loadingContainersForAllStream() throws SQLException, Exception
	{
		java.util.Date date=new java.util.Date();  
		String time =(date.toString());
		System.out.println(time.substring(11,16)); 
			
	}
	
	//@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
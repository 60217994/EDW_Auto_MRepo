package EDW.Edward;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
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
	public void jobStatus() throws SQLException, Exception
	{
		Statement stmt = (Statement) con.createStatement();
		String sql = "SELECT TOP(4) * FROM EDW2.CTL.AUDIT_TRAIL WITH(NOLOCK) ORDER BY AUDIT_TRAIL_ID DESC" ;
		
		ResultSet rs = ((java.sql.Statement) stmt).executeQuery(sql);
		if (rs.next() == true) 
		{
		
		String mess = rs.getString("AUDIT_TYPE");
		System.out.println(mess);
		if (mess.equals("ERROR"));
		{
		
		int id = rs.getInt("AUDIT_TRAIL_ID") -3;
		System.out.println(id);
		stmt.close();
		
		Statement stmt1 = (Statement) con.createStatement();
		String sql1 = "SELECT AUDIT_TRAIL_ID,* FROM EDW2.CTL.AUDIT_TRAIL WITH(NOLOCK) WHERE AUDIT_TRAIL_ID = " + id + "ORDER BY AUDIT_TRAIL_ID DESC" ;
		ResultSet rs1 = ((java.sql.Statement) stmt1).executeQuery(sql1);
		
		if (rs1.next() == true) 
		{
			String id2 = rs.getString("AUDIT_TRAIL_ID");
			System.out.println(id2);
			String amess = rs.getString("AUDIT_MESSAGE");
			String[] splitarray = amess.split(",");
			System.out.println(splitarray[3]);
		
		
		}
		}
		}
	}
	
	
	@After
	public void closeConn() throws SQLException, Exception
	{
		bc.connClose();
	}
	
	
}
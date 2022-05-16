package EDW.Edward;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.poi.hslf.model.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class randtests extends BaseClass
{

	BaseClass bc = new BaseClass();
	Properties prop = new Properties();
	String propFileName = "config.properties";
	InputStream ipstream = getClass().getClassLoader().getResourceAsStream(propFileName);
	
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
	
	@SuppressWarnings("resource")
	@Test
	public void jobStatus() throws SQLException, Exception
	{
//		//Create an object of File class to open xlsx file
//		FileInputStream file = new FileInputStream(new File("C:\\Users\\60217994\\Downloads"+"\\"+"DS-54-HIE-Drug-and-Alcohol-EDWARD-DZ-to-EDW-Source-to-Target-Mapping-Requirements-V11.xlsx"));
//	    Workbook workbook = new XSSFWorkbook(file);
//
//	    Sheet sheet = (Sheet) workbook.getSheet("Data Stream 54 - Files to load");
//	  
//	    //int rowcount = sheet;
//	    
//	    System.out.println();
		
//		bc.generateCountsScripts(54, "HIE-X710", 177);
//		bc.generateCountsScripts(54, "HIE-X710", 178);
//		bc.generateCountsScripts(54, "HIE-X710", 179);
		
		bc.countsMatchingbetweenDZandCore(17, "7448-003", 2883);

	}
	
	
	
}
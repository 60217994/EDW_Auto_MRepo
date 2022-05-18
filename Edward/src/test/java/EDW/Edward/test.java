package EDW.Edward;

import java.sql.Statement;

public class test {

	public static void main(String[] args) {
		String cbkdz = "SERVICE_EVENT_TYPE_CODE + '|' + SERVICE_EVENT_SOURCE_ID + '|' + SERVICE_ENCOUNTER_RECORD_ID + '|' + SERVICE_EVENT_RECORD_ID";
		String cbkc = "SE_TYP_CD + '|' + SE_SRC_ID + '|' + SVC_ENC_REC_ID + '|' + SE_REC_ID";
		
		String dztable = "CLIENT_SERVICE_EVENT";
		String coretable = "CL_SE";
		
		String sourcesystemcode = "7448-003";
		int datacontid = 7992;
		int contseqnumb = 2883;
		
		String query2 = "SELECT "+ cbkdz + " AS 'Misiing_Cbks' FROM DZ.DBO." + dztable + " WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "' AND " + cbkdz 
    			+ " IN (SELECT DISTINCT " + cbkdz + " FROM DZ.DBO." + dztable + " WHERE RECORD_SOURCE_SYSTEM_CODE = '" + sourcesystemcode + "' AND CONTAINER_SEQUENCE_NUMBER = " + contseqnumb
    			+ " EXCEPT \r\n" 
    			+ "SELECT DISTINCT "+ cbkc + " FROM EDW2.DBO."+ coretable + " WHERE REC_SRC_SYS_CD = '" + sourcesystemcode + "' AND AND EDW_DATA_CONTAINER_ID = " + datacontid + ")";
	    //ResultSet rse = ((java.sql.Statement) stmt).executeQuery(query2);
		System.out.println(query2);

	}

}

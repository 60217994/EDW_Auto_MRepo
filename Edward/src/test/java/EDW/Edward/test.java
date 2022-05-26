package EDW.Edward;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class test {

	public static void main(String[] args) throws InterruptedException, SQLException {

		BaseClass bc = new BaseClass();
		bc.DZTablesForAStream(1);
	}

}

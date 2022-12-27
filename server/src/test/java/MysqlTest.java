import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlTest {

	static final String DB_URL = "jdbc:mysql://keekkewy.xicp.net:59203/tank_war";
	static final String USER_NAME = "tankwar";
	@SuppressWarnings("SpellCheckingInspection")
	static final String USER_PASSWD = "tBcuqeJUJkj59Lu";

	@Test
	void connectionTest() throws SQLException {
		Statement statement = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASSWD).createStatement();
		ResultSet result = statement.executeQuery("SELECT passwd FROM players WHERE name=\"Kekwy\"");
		while (result.next()) {
			System.out.println(result.getString("passwd"));
		}
	}


}

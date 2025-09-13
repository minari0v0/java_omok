package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBmanager {
    public static final String URL = "jdbc:mysql://localhost:3306/omok"; // DB URL
    public static final String USER = "root"; // DB 사용자명
    public static final String PASSWORD = "1234"; // DB 비밀번호

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBC dbc = new DBC("127.0.0.1:3306/", "cell43");

        String queryOnCreate = "CREATE TABLE orders (\n" +
                " id_art int(11) NOT NULL AUTO_INCREMENT,\n" +
                " name varchar(50) NOT NULL,\n" +
                " code varchar(50) NOT NULL,\n" +
                " username varchar(50) NOT NULL,\n" +
                " guide varchar(50) NOT NULL,\n" +
                " PRIMARY KEY (id_art)\n" +
                ")";
        dbc.execUpdate(queryOnCreate);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите количество записей в таблице: ");
        int count = 0;
        try {
            count = Integer.parseInt(bufferedReader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder queryOnInsert = new StringBuilder("INSERT INTO orders (name, code, username, guide) VALUES ");
        for (int i = 1; i <= count; i++) {
            if (i != count)
                queryOnInsert.append("(\"" + "name").append(i).append("\", ").append("\"").append("code").append(i).append("\", ").append("\"").append("username").append(i).append("\", ").append("\"").append("guide").append(i).append("\"), ");
            else
                queryOnInsert.append("(\"" + "name").append(i).append("\", ").append("\"").append("code").append(i).append("\", ").append("\"").append("username").append(i).append("\", ").append("\"").append("guide").append(i).append("\")");
        }
        dbc.insertIntoDatabase(queryOnInsert.toString());

        String SQL_select = "SELECT * FROM orders";
        dbc.readDataFromDatabase(SQL_select);
    }
}

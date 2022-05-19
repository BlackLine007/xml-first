import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class DBC {
    private final String serverUrl; //адрес сервера БД
    private final String dbName;
    private String driver = "com.mysql.cj.jdbc.Driver"; // драйвер используемый по умолчанию при подключении
    private String userName = "root";
    private String password = "root";
    private final boolean readyToWork; // флаг готовности класса к работе с бд

    // далее будут описаны несколько разных конструкторов, чтобы коннектиться разными способами

    /**
     * Конструктор для работы со встроенной системой проверки безопасности
     */
    public DBC(String serverUrl, String dbName) {
        this.serverUrl = serverUrl;
        this.dbName = dbName;

        readyToWork = checkConnection();
    }

    /**
     * Конструктор для работы со встроенной системой проверки безопасности и с указанным драйвером
     */
    public DBC(String serverUrl, String dbName, String driver) {
        this.serverUrl = serverUrl;
        this.dbName = dbName;
        this.driver = driver;

        readyToWork = checkConnection();
    }

    /**
     * Конструктор для работы с использованием имени и пароля пользователя базы данных
     */
    DBC(String serverUrl, String dbName, String userName, String password) {
        this.serverUrl = serverUrl;
        this.dbName = dbName;
        this.userName = userName;
        this.password = password;

        readyToWork = checkConnection();
    }

    /**
     * Конструктор для работы с использованием имени и пароля пользователя базы данных и указанием драйвера
     */
    DBC(String serverUrl, String dbName, String userName, String password, String driver) {
        this.serverUrl = serverUrl;
        this.dbName = dbName;
        this.userName = userName;
        this.password = password;
        this.driver = driver;

        readyToWork = checkConnection();
    }

    /**
     * Создаёт объект подключения
     */
    private Connection createConnection() {
        //  mySQLConnection.setConnectionURL("jdbc:mysql://127.0.0.1:3306/magnit");
        // Формирование строки подключения
        String connStr = "jdbc:mysql://" + this.serverUrl + this.dbName;

        try {
            // Установка драйвера
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Connection conn;

        if (this.userName.equals("") && this.password.equals("")) {
            // Подключение с встроенной проверкой безопасности

            connStr += "integratedSecurity=true;";
            try {
                conn = DriverManager.getConnection(connStr);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // Подключение по имени и паролю пользователя
            try {
                conn = DriverManager.getConnection(connStr, userName, password);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return conn;
    }

    /**
     * Проверка возможности установления подключения с сервером БД
     */
    private boolean checkConnection() {
        Connection testConnection = createConnection();

        if (testConnection == null) {
            System.out.println("Не возможно подключиться к серверу. " + "Проверьте исключения, url, имя пользователя и пароль");
            return false;
        } else {
            try {
                testConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    public ResultSet execQuery(String query) {
        if (!readyToWork) {
            System.out.println("DBC not ready to work! Abort:execQuerySelected");
            return null;
        }
        Connection conn = createConnection();

        Statement stmt;

        try {
            assert conn != null;
            stmt = conn.createStatement(); // инициализируем обработчик запросов и указываем кол-во строк, которые могут одновременно находиться в ОЗУ
            stmt.setFetchSize(100);

            return stmt.executeQuery(query); //выполняем запрос

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка... проверьте ваш sql-запрос");
            return null;
        }
    }

    public void execUpdate(String query) {
        if (!readyToWork) {
            System.out.println("DBC not ready to work! Abort:execUpdate");
        }
        Connection conn = createConnection();

        Statement stmt;

        try {
            assert conn != null;
            stmt = conn.createStatement(); // инициализируем обработчик запросов и указываем кол-во строк, которые могут одновременно находиться в ОЗУ
            stmt.setFetchSize(100);

            stmt.executeUpdate(query); //выполняем запрос

        } catch (SQLException e) {
            if (e.getClass().getSimpleName().equals(("SQLSyntaxErrorException")))
                System.out.println("Table 'orders' already exists");
            else {
                e.printStackTrace();
                System.out.println("Ошибка... проверьте ваш sql-запрос");
            }
        }
    }

    public void insertIntoDatabase(String query) {
        if (!readyToWork) {
            System.out.println("DBC not ready to work! Abort:execQuerySelected");
            return;
        }
        Connection conn = createConnection();

        Statement stmt;
        int count = 0;
        try {
            assert conn != null;
            stmt = conn.createStatement(); // инициализируем обработчик запросов и указываем кол-во строк, которые могут одновременно находиться в ОЗУ
            stmt.setFetchSize(100);

            count = stmt.executeUpdate(query); //выполняем запрос

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка... проверьте ваш sql-запрос");
        }
    }

    public void readDataFromDatabase(String query) {
        if (!readyToWork) {
            System.out.println("DBC not ready to work! Abort:execQuerySelected");
        }
        Connection conn = createConnection();
        Statement stmt;
        ResultSet resultSet;
        try {
            assert conn != null;
            stmt = conn.createStatement(); // инициализируем обработчик запросов и указываем кол-во строк, которые могут одновременно находиться в ОЗУ
            stmt.setFetchSize(100);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("1.xml"));
            resultSet = stmt.executeQuery(query); //выполняем запрос
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            stringBuilder.append("\n");
            stringBuilder.append("<articles>");
            while (resultSet.next()) {
                int id_art = resultSet.getInt("id_art");
                String name = resultSet.getString("name");
                String code = resultSet.getString("code");
                String userName = resultSet.getString("username");
                String guid = resultSet.getString("guide");
                stringBuilder.append("\n" + "\t").append("<article ").append("id_art=").append("\"").append(id_art).append("\"").append(" ").append("name=").append("\"" + name + "\"" + " ").append("code=").append("\"" + code + "\"" + " ").append("username=").append("\"" + userName + "\"" + " ").append("guid=").append("\"" + guid + "\"").append("/>");
            }
            stringBuilder.append("\n").append("</articles>");
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка... проверьте ваш sql-запрос");
        }
    }
}

package helper;

import employeemanagement.Employee;

import java.sql.*;
import java.util.ArrayList;

public class Database {

    private Connection connection;
    private Statement statement;
    private String url = "jdbc:sqlite:employees.db";

    public Database() {
        this.connection = null;
        connect(this.url);
    }

    public Database(boolean firstStart) {
        this.connection = null;
        connect(this.url);
        if(firstStart) firstStart();
    }

    private void connect(String url) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creating DB and Defaultuser on first start
     */
    private void firstStart() {
        try {
            System.out.println("Versuche zu erstellen");
            this.statement = this.connection.createStatement();

            String query = "CREATE TABLE IF NOT EXISTS employees "+
                    "(id INTEGER PRIMARY KEY NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "age INTEGER NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "password TEXT NOT NULL)";

            this.statement.execute(query);
            this.statement.close();
            String pw = Encryption.bcryptHash("admin");
            Employee defaultEmployee = createUser("Admin", 99, "admin@admin.de", pw);
            System.out.println("Defaultlogin: admin@admin.de\n Defaultpassword: admin");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a Employee in the DB as well as Object
     * @param name name
     * @param age age
     * @param email email
     * @param password password
     * @return Employee with DB Data
     */
    public Employee createUser(String name, int age, String email, String password) {
        try {
            this.statement = this.connection.createStatement();

            String query = "INSERT INTO employees (name, age, email, password) VALUES (" +
                    "'" + name + "', " + age + ", '" + email + "', '" + password + "')";

            this.statement.execute(query);
            this.statement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int id = getIDByNameAndMail(name, email);
        return new Employee(id, name, age, email, password);
    }

    /**
     * Gets ID of DB Entry matching given name and email
     * @param name name to search for
     * @param email email to search for
     * @return Integer id of
     */
    public int getIDByNameAndMail(String name, String email) {
        int id = 0;
        try {
            this.statement = this.connection.createStatement();
            String query = "SELECT * FROM employees WHERE name='" + name + "' AND email='" + email + "'";

            ResultSet result = this.statement.executeQuery(query);

            while (result.next()) {
                id = result.getInt("id");
            }
            this.statement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

    /**
     * SQL query to get Employee with a certain ID
     * @param id ID to search for in DB
     * @return Employee with given ID
     */
    public Employee getEmployeeByID(int id) {
        Employee employee = null;
        try {
            this.statement = this.connection.createStatement();
            String query = "SELECT * FROM employees WHERE id="+id;
            ResultSet result = this.statement.executeQuery(query);
            while(result.next()) {
                int qid = result.getInt("id");
                String name = result.getString("name");
                int age = result.getInt("age");
                String email = result.getString("email");
                String password = result.getString("password");

                employee = new Employee(qid, name, age, email, password);
            }
            this.statement.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return employee;
    }

    /**
     * SQL query to get Employee with a certain ID
     * @param email Email to search for in DB
     * @return Employee with given ID
     */
    public Employee getEmployeeByMail(String email) {
        Employee employee = null;
        try {
            this.statement = this.connection.createStatement();
            String query = "SELECT * FROM employees WHERE email='"+email+"'";
            ResultSet result = this.statement.executeQuery(query);
            while(result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                int age = result.getInt("age");
                String qemail = result.getString("email");
                String password = result.getString("password");

                employee = new Employee(id, name, age, qemail, password);
            }
            this.statement.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return employee;
    }

    /**
     * SQL query to get everything from employee DB
     * @return List of all Employees
     */
    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employeeList = new ArrayList<Employee>();
        try {
            this.statement = this.connection.createStatement();
            String query = "SELECT * FROM employees";

            ResultSet result = this.statement.executeQuery(query);
            while(result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                int age = result.getInt("age");
                String email = result.getString("email");
                String password = result.getString("password");

                Employee employee = new Employee(id, name, age, email, password);
                employeeList.add(employee);
            }
            this.statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(employeeList);
        return employeeList;
    }

    /**
     * SQL query to remove an Employee with given ID
     * @param id ID of Employee to be removed
     * @return boolean if successful
     */
    public boolean removeEmployee(int id) {
        boolean result = false;
        try{
            this.statement = this.connection.createStatement();
            String query = "DELETE FROM employees WHERE id="+id;

            this.statement.execute(query);
            this.statement.close();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Updates given Employee in DB
     * @param employee Employee with updated Data
     * @return boolean if successful
     */
    public boolean changeEmployee(Employee employee) {
        boolean result = false;
        try {
            this.statement = this.connection.createStatement();
            String query = "UPDATE employees SET name='"+employee.getName()+"', age="+employee.getAge()+", email='"+employee.getEmail()+"', password='"+employee.getPassword()+"'";
            this.statement.execute(query);
            this.statement.close();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

}

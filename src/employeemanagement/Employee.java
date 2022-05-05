package employeemanagement;

import helper.Encryption;

public class Employee {
    private int id;
    private String name;
    private int age;
    private String email;
    private String password;

    public Employee() {
        this.name = "Admin";
        this.age = 99;
        this.email = "admin@admin.de";
        this.password = Encryption.bcryptHash("admin");
    }

    public Employee(int id, String name, int age, String email, String password) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return this.age; }
    public void setAge(int age) { this.age = age; }
    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }
}

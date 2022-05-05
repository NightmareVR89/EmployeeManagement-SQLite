package views.mainwindow;

import employeemanagement.Employee;
import helper.Database;
import helper.Encryption;

import java.awt.Dimension;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

public class MainWindow extends JPanel {
    private JButton btnLogin;
    private JButton btnLogout;
    private JButton btnAdd;
    private JButton btnDel;
    private JList<String> eList;
    private JTextField txtName;
    private JFormattedTextField txtAge;
    private JTextField txtMail;
    private JPasswordField txtPw;

    private ArrayList<Employee> listEmployees = new ArrayList<Employee>();
    private DefaultListModel<String> listModel = new DefaultListModel<String>();
    private final String filepath = "employees.db";
    private Employee loggedIn = null;
    private NumberFormat ageFormat;
    private final Pattern testForNumber = Pattern.compile("^[^A-Z]|.[^a-z]");

    private Database connection;
    public MainWindow() {
        if(!Files.exists(Path.of(filepath))) {
            JOptionPane.showMessageDialog(null, "Erster Start entdeckt: Defaultbenutzer admin@admin.de mit Passwort admin angelegt.");
            this.connection = new Database(true);
        } else {
            this.connection = new Database();
        }
        loadEmployees();

        this.btnLogin = new JButton ("Login");
        this.btnLogout = new JButton ("Logout");
        this.btnAdd = new JButton ("Hinzufügen");
        this.btnDel = new JButton ("Löschen");
        this.eList = new JList<String> (listModel);
        this.txtName = new JTextField (5);
        this.txtAge = new JFormattedTextField(ageFormat);
        this.txtMail = new JTextField (5);
        this.txtPw = new JPasswordField (5);

        txtPw.setToolTipText ("Passwort");

        super.setPreferredSize (new Dimension (584, 411));
        super.setLayout (null);

        super.add (btnLogin);
        super.add (btnLogout);
        super.add (btnAdd);
        super.add (btnDel);
        super.add (eList);
        super.add (txtName);
        super.add (txtAge);
        super.add (txtMail);
        super.add (txtPw);

        this.btnLogin.setBounds (10, 145, 140, 45);
        this.btnLogout.setBounds (160, 145, 145, 45);
        this.btnAdd.setBounds (10, 355, 140, 45);
        this.btnDel.setBounds (165, 355, 140, 45);
        this.eList.setBounds (325, 10, 250, 395);
        this.txtName.setBounds (10, 10, 225, 35);
        this.txtAge.setBounds (240, 10, 65, 35);
        this.txtMail.setBounds (10, 60, 295, 35);
        this.txtPw.setBounds (10, 100, 295, 35);

        this.btnAdd.addActionListener(e -> addEmployee());
        this.btnDel.addActionListener(e -> deleteEmployee());
        this.btnLogin.addActionListener(e -> checkLogin(txtMail.getText(), String.valueOf(txtPw.getPassword())));
        this.btnLogout.addActionListener(e -> { this.loggedIn = null; resetForm(); });
        this.eList.addListSelectionListener(e -> fillFields());

        resetForm();
    }

    /**
     * Creates new Employee Object, adds it to the Employeelist and writes the new List as JSON to File
     */
    private void addEmployee() {
        if(this.loggedIn == null || !checkFields()) return;
        String encrypted = Encryption.bcryptHash(String.valueOf(txtPw.getPassword()));
        Employee newEmployee = this.connection.createUser(txtName.getText(), (Integer) txtAge.getValue(), txtMail.getText(), encrypted);

        this.listEmployees.add(newEmployee);
        this.listModel.addElement(newEmployee.getName() + " (" + Integer.toString(newEmployee.getAge()) + ")");

        resetForm();
    }

    /**
     * Removes in List selected Employee from Employeelist and writes the new List as JSON to File
     */
    private void deleteEmployee() {
        if(eList.getSelectedIndex() > -1 && this.loggedIn != null) {
            if(this.listEmployees.indexOf(this.loggedIn) == eList.getSelectedIndex()) {
                JOptionPane.showMessageDialog(null, "Du kannst deinen eigenen Account nicht löschen!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(this.connection.removeEmployee(listEmployees.get(eList.getSelectedIndex()).getId())) {
                this.listEmployees.remove(eList.getSelectedIndex());
                this.listModel.removeElementAt(eList.getSelectedIndex());
                resetForm();
            } else {
                JOptionPane.showMessageDialog(null, "Fehler beim Löschen des Accounts!", "SQL Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Checks given Login against Employeelist and loggs the User in as given Employee
     * @param email Email userinput
     * @param password Password userinput
     */
    private void checkLogin(String email, String password) {
        boolean found = false;
        for(Employee e: listEmployees) {
            if(e.getEmail().equals(email) && !e.getPassword().isEmpty()) {
                found = true;
                if(Encryption.comparePassword(password, e.getPassword())) {
                    JOptionPane.showMessageDialog(null, "Login Erfolgreich");
                    this.loggedIn = e;
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(null, "Passwort falsch!", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if(!found) {
            JOptionPane.showMessageDialog(null, "Email oder Passwort falsch!", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Automatically fills Name and Age fields with selected Employeedata
     */
    private void fillFields() {
        if(eList.getSelectedIndex() > -1) {
            Employee active = listEmployees.get(eList.getSelectedIndex());
            this.txtName.setText(active.getName());
            this.txtAge.setValue(active.getAge());
        }
    }

    /**
     * Resets the MainWindow to Default and hides non-accessible Buttons for the User
     */
    private void resetForm() {
        this.txtName.setText("");
        this.txtAge.setValue(0);
        this.txtPw.setText("");
        this.txtMail.setText("");

        boolean isLoggedIn = this.loggedIn != null;

        this.btnAdd.setVisible(isLoggedIn);
        this.btnDel.setVisible(isLoggedIn);
        this.btnLogin.setVisible(!isLoggedIn);
        this.btnLogout.setVisible(isLoggedIn);

        this.txtAge.setEditable(isLoggedIn);
        this.txtName.setEditable(isLoggedIn);
    }

    /**
     * Reads the userdata from file and creates Employeelist
     * Creates a new file with default login data if not existing
     */
    private void loadEmployees() {
        this.listEmployees = this.connection.getAllEmployees();
        for(Employee employee: listEmployees) {
            this.listModel.addElement(employee.getName() + " (" + employee.getAge() + ")");
        }
    }

    /**
     * Prints errormessages if input fields are not filled out correctly and if Name or Email are already in use
     * @return boolean
     */
    private boolean checkFields() {
        Matcher match = testForNumber.matcher(txtName.getText());
        boolean test = match.find();
        if(txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Fehler: Kein Benutzername eingegeben!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(test) {
            JOptionPane.showMessageDialog(null, "Fehler: Benutzername darf keine Sonderzeichen oder Zahlen enthalten und muss mit einem großen Buchstaben beginnen!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(txtMail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Keine gültige Emailadresse eingegeben!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(String.valueOf(txtPw.getPassword()).length() < 5) {
            JOptionPane.showMessageDialog(null, "Es muss ein Passwort mit mindestens 5 Zeichen eingegeben werden!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if((int) txtAge.getValue() <= 0) {
            JOptionPane.showMessageDialog(null, "Es muss ein gültiges Alter eingegeben werden!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(this.connection.getEmployeeByMail(txtMail.getText()) != null)  {
                JOptionPane.showMessageDialog(null, "Name existiert schon!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
        }
        return true;
    }
}

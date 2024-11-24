
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.*;
import javax.swing.JOptionPane;

public class ContactManagementSystem extends Frame {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3307/contacts";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Aryan@1311";
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;
    private static TextField nameField, emailField, phoneField, addressField, simCompanyField, occupationField, interestedField;
    private static Button addButton, viewButton, searchButton, editButton, deleteButton;
    private static TextArea resultArea;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public ContactManagementSystem() {
        setTitle("Contact Management System");
        setSize(800, 400); // Adjusted default window size
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Labels and Text Fields
        addLabelAndField("Name:", gbc, 0, 0);
        addLabelAndField("Email:", gbc, 0, 1);
        addLabelAndField("Phone:", gbc, 0, 2);
        addLabelAndField("Address:", gbc, 0, 3);
        addLabelAndField("Sim Company:", gbc, 0, 4);
        addLabelAndField("Occupation Type:", gbc, 0, 5);
        addLabelAndField("Interested (yes/no):", gbc, 0, 6);

        // Text Area for Result
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        resultArea = new TextArea();
        resultArea.setEditable(false);
        add(resultArea, gbc);

        // Buttons
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        addButton = new Button("Add Contact");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    addContact();
                    JOptionPane.showMessageDialog(null, "Contact added successfully.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(addButton, gbc);

        viewButton = new Button("View Contacts");
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    viewContacts();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        gbc.gridy = 1;
        add(viewButton, gbc);

        searchButton = new Button("Search Contact");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    searchContact();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        gbc.gridy = 2;
        add(searchButton, gbc);

        editButton = new Button("Edit Contact");
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    editContact();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        gbc.gridy = 3;
        add(editButton, gbc);

        deleteButton = new Button("Delete Contact");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    deleteContact();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        gbc.gridy = 4;
        add(deleteButton, gbc);
    }

    public static void main(String[] args) {
        ContactManagementSystem gui = new ContactManagementSystem();
        gui.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        gui.setVisible(true);
    }
               
    public static void connect() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public void addLabelAndField(String labelText, GridBagConstraints gbc, int x, int y) {
        Label label = new Label(labelText);
        gbc.gridx = x;
        gbc.gridy = y;
        add(label, gbc);
        TextField field = new TextField(20);
        gbc.gridx = x + 1;
        gbc.gridy = y;
        add(field, gbc);
        switch (labelText) {
            case "Name:":
                nameField = field;
                break;
            case "Email:":
                emailField = field;
                break;
            case "Phone:":
                phoneField = field;
                break;
            case "Address:":
                addressField = field;
                break;
            case "Sim Company:":
                simCompanyField = field;
                break;
            case "Occupation Type:":
                occupationField = field;
                break;
            case "Interested (yes/no):":
                interestedField = field;
                break;
        }
    }

    public static void addContact() throws SQLException {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String simCompany = simCompanyField.getText();
        String occupationType = occupationField.getText();
        String interested = interestedField.getText();

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(null, "Invalid email format. Please enter a valid Gmail address.");
            return;
        }
        if (!isValidPhoneNumber(phone)) {
            JOptionPane.showMessageDialog(null, "Invalid phone number format. Please enter a 10-digit number without spaces or special characters.");
            return;
        }

        connect();
        preparedStatement = connection.prepareStatement("INSERT INTO contact_details (name, email, phone, address, sim_company, occupation_type, interested) VALUES (?, ?, ?, ?, ?, ?, ?)");
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, phone);
        preparedStatement.setString(4, address);
        preparedStatement.setString(5, simCompany);
        preparedStatement.setString(6, occupationType);
        preparedStatement.setString(7, interested);
        preparedStatement.executeUpdate();
        close();
        clearFields();
    }

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        simCompanyField.setText("");
        occupationField.setText("");
        interestedField.setText("");
    }

    public static void viewContacts() throws SQLException {
        connect();
        preparedStatement = connection.prepareStatement("SELECT * FROM contact_details");
        resultSet = preparedStatement.executeQuery();
        StringBuilder result = new StringBuilder();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String email = resultSet.getString("email");
            String phone = resultSet.getString("phone");
            String address = resultSet.getString("address");
            String simCompany = resultSet.getString("sim_company");
            String occupationType = resultSet.getString("occupation_type");
            String interested = resultSet.getString("interested");
            result.append("ID: ").append(id).append(", Name: ").append(name).append(", Email: ").append(email).append(", Phone: ").append(phone).append(", Address: ").append(address).append(", Sim Company: ").append(simCompany).append(", Occupation Type: ").append(occupationType).append(", Interested: ").append(interested).append("\n");
        }
        resultArea.setText(result.toString());
        close();
    }

    public static void searchContact() throws SQLException {
        String[] options = { "ID", "Phone", "Sim Company", "Occupation Type", "Interested (yes/no)" };
        String selectedOption = (String) JOptionPane.showInputDialog(null, "Choose an option:", "Search Contact",
                JOptionPane.DEFAULT_OPTION, null, options, options[0]);
        if (selectedOption != null) {
            String searchText = JOptionPane.showInputDialog("Enter value to search:");
            if (searchText != null) {
                connect();
                String query = "";
                switch (selectedOption) {
                    case "ID":
                        query = "SELECT * FROM contact_details WHERE id = ?";
                        break;
                    case "Phone":
                        query = "SELECT * FROM contact_details WHERE phone LIKE ?";
                        break;
                    case "Sim Company":
                        query = "SELECT * FROM contact_details WHERE sim_company LIKE ?";
                        break;
                    case "Occupation Type":
                        query = "SELECT * FROM contact_details WHERE occupation_type LIKE ?";
                        break;
                    case "Interested (yes/no)":
                        query = "SELECT * FROM contact_details WHERE interested LIKE ?";
                        break;
                    default:
                        break;
                }
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "%" + searchText + "%");
                resultSet = preparedStatement.executeQuery();
                StringBuilder result = new StringBuilder();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String phone = resultSet.getString("phone");
                    String address = resultSet.getString("address");
                    String simCompany = resultSet.getString("sim_company");
                    String occupationType = resultSet.getString("occupation_type");
                    String interested = resultSet.getString("interested");
                    result.append("ID: ").append(id).append(", Name: ").append(name).append(", Email: ")
                            .append(email).append(", Phone: ").append(phone).append(", Address: ").append(address)
                            .append(", Sim Company: ").append(simCompany).append(", Occupation Type: ")
                            .append(occupationType).append(", Interested: ").append(interested).append("\n");
                }
                resultArea.setText(result.toString());
                close();
            }
        }
    }

    public static void editContact() throws SQLException {
        String[] options = { "ID", "Phone", "Sim Company", "Occupation Type", "Interested (yes/no)" };
        String selectedOption = (String) JOptionPane.showInputDialog(null, "Choose an option:", "Edit Contact",
                JOptionPane.DEFAULT_OPTION, null, options, options[0]);
        if (selectedOption != null) {
            String searchText = JOptionPane.showInputDialog("Enter value to edit:");
            if (searchText != null) {
                connect();
                String query = "";
                switch (selectedOption) {
                    case "ID":
                        query = "SELECT * FROM contact_details WHERE id = ?";
                        break;
                    case "Phone":
                        query = "SELECT * FROM contact_details WHERE phone LIKE ?";
                        break;
                    case "Sim Company":
                        query = "SELECT * FROM contact_details WHERE sim_company LIKE ?";
                        break;
                    case "Occupation Type":
                        query = "SELECT * FROM contact_details WHERE occupation_type LIKE ?";
                        break;
                    case "Interested (yes/no)":
                        query = "SELECT * FROM contact_details WHERE interested LIKE ?";
                        break;
                    default:
                        break;
                }
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "%" + searchText + "%");
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String phone = resultSet.getString("phone");
                    String address = resultSet.getString("address");
                    String simCompany = resultSet.getString("sim_company");
                    String occupationType = resultSet.getString("occupation_type");
                    String interested = resultSet.getString("interested");

                    // Now you can populate the fields with the retrieved values
                    nameField.setText(name);
                    emailField.setText(email);
                    phoneField.setText(phone);
                    addressField.setText(address);
                    simCompanyField.setText(simCompany);
                    occupationField.setText(occupationType);
                    interestedField.setText(interested);

                    // Perform any necessary UI updates or validation checks
                } else {
                    JOptionPane.showMessageDialog(null, "Contact not found.");
                }
                close();
            }
        }
    }

    public static void deleteContact() throws SQLException {
        String[] options = { "ID", "Phone", "Sim Company", "Occupation Type", "Interested (yes/no)" };
        String selectedOption = (String) JOptionPane.showInputDialog(null, "Choose an option:", "Delete Contact",
                JOptionPane.DEFAULT_OPTION, null, options, options[0]);
        if (selectedOption != null) {
            String searchText = JOptionPane.showInputDialog("Enter value to delete:");
            if (searchText != null) {
                connect();
                String query = "";
                switch (selectedOption) {
                    case "ID":
                        query = "DELETE FROM contact_details WHERE id = ?";
                        break;
                    case "Phone":
                        query = "DELETE FROM contact_details WHERE phone LIKE ?";
                        break;
                    case "Sim Company":
                        query = "DELETE FROM contact_details WHERE sim_company LIKE ?";
                        break;
                    case "Occupation Type":
                        query = "DELETE FROM contact_details WHERE occupation_type LIKE ?";
                        break;
                    case "Interested (yes/no)":
                        query = "DELETE FROM contact_details WHERE interested LIKE ?";
                        break;
                    default:
                        break;
                }
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "%" + searchText + "%");
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Contact deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Contact not found.");
                }
                close();
            }
        }
    }

    public static void close() throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}

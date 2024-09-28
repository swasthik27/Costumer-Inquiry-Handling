package sop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationPage extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;

    public RegistrationPage() {
        setTitle("Customer Registration");
        setSize(400, 650); // Increased height to accommodate the new buttons
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(144, 238, 144)); // Light green background
        panel.setLayout(null); // Use absolute positioning
        add(panel);

        // Add the image icon
        ImageIcon icon = new ImageIcon("C:\\Users\\91789\\Downloads\\fullreg.png");
        JLabel iconLabel = new JLabel(icon);
        int imageWidth = icon.getIconWidth();
        int imageHeight = icon.getIconHeight();
        int panelWidth = 400; // Width of the panel (frame size)
        int xPos = (panelWidth - imageWidth) / 2; // Center the image horizontally

        // Position the image and ensure it does not overlap with other components
        iconLabel.setBounds(xPos, 10, imageWidth, imageHeight); // Position and size of the image
        panel.add(iconLabel);

        // Place components below the image
        placeComponents(panel, imageHeight + 20); // Passing the height of the image plus some padding

        setVisible(true);
    }

    private void placeComponents(JPanel panel, int yOffset) {
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, yOffset, 80, 25);
        panel.add(nameLabel);

        nameField = new JTextField(20);
        nameField.setBounds(150, yOffset, 200, 25);
        panel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, yOffset + 40, 80, 25);
        panel.add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(150, yOffset + 40, 200, 25);
        panel.add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(10, yOffset + 80, 80, 25);
        panel.add(phoneLabel);

        phoneField = new JTextField(20);
        phoneField.setBounds(150, yOffset + 80, 200, 25);
        panel.add(phoneField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, yOffset + 120, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, yOffset + 120, 200, 25);
        panel.add(passwordField);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(50, yOffset + 180, 120, 25); // Increased width
        panel.add(registerButton);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(200, yOffset + 180, 120, 25); // Increased width
        panel.add(loginButton);

        JButton adminButton = new JButton("Admin");
        adminButton.setBounds(50, yOffset + 220, 120, 25); // Positioned below Login
        panel.add(adminButton);

        JButton departmentButton = new JButton("Department");
        departmentButton.setBounds(200, yOffset + 220, 120, 25); // Positioned below Login
        panel.add(departmentButton);

        // Action listeners for buttons
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerCustomer();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginPage(); // Open login page
                dispose(); // Close registration page
            }
        });

        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdminPage(); // Open admin page
                dispose(); // Close registration page
            }
        });

        departmentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new DepartmentView(); // Open department view
                dispose(); // Close registration page
            }
        });
    }

    private void registerCustomer() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Input Validation
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid Email! Please enter a valid email address.");
            return;
        }

        if (!isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Invalid Phone Number! Please enter a valid phone number.");
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty!");
            return;
        }

        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO customers (name, email, phone, password) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setString(4, password); // Hash this in a real application

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful! Please log in.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private boolean isValidEmail(String email) {
        // Email validation regex pattern
        return email.matches("^[a-z0-9._%+-]+@gmail\\.com$");
    }

    private boolean isValidPhone(String phone) {
        // Phone validation pattern (10 digits)
        return phone.matches("\\d{10}");
    }
}

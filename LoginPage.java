package sop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Customer Login");
        setSize(400, 500); // Increased height to accommodate the image
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(144, 238, 144)); // Light green background
        panel.setLayout(null); // Use absolute positioning
        add(panel);

        // Add the image icon
        ImageIcon icon = new ImageIcon("C:\\Users\\91789\\Downloads\\reg.png");
        JLabel iconLabel = new JLabel(icon);
        int imageWidth = icon.getIconWidth();
        int imageHeight = icon.getIconHeight();
        int panelWidth = 400; // Width of the panel (frame size)
        int xPos = (panelWidth - imageWidth) / 2; // Center the image horizontally

        iconLabel.setBounds(xPos, 10, imageWidth, imageHeight); // Position and size of the image
        panel.add(iconLabel);

        // Place components below the image
        placeComponents(panel, imageHeight + 20); // Passing the height of the image plus some padding

        setVisible(true);
    }

    private void placeComponents(JPanel panel, int yOffset) {
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, yOffset, 80, 25);
        panel.add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(150, yOffset, 200, 25);
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, yOffset + 40, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, yOffset + 40, 200, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, yOffset + 80, 100, 25);
        panel.add(loginButton);

        JButton backButton = new JButton("Back to Registration");
        backButton.setBounds(150, yOffset + 120, 200, 25); // Position below login button
        panel.add(backButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticateCustomer();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegistrationPage(); // Open registration page
                dispose(); // Close login page
            }
        });
    }

    private void authenticateCustomer() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM customers WHERE email = ? AND password = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, email);
                ps.setString(2, password); // Hash this in a real application

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    dispose();
                    new InquiryPage(rs.getInt("id")); // Open inquiry page with customer ID
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials!");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}

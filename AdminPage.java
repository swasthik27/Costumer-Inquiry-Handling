package sop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminPage() {
        setTitle("Admin Login");
        setSize(400, 250); // Increased height to accommodate the header
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(144, 238, 144)); // Light green background
        add(panel);
        placeComponents(panel);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Header label
        JLabel headerLabel = new JLabel("    Welcome Admin!");
        headerLabel.setFont(new Font("Serif", Font.BOLD, 18)); // Font and size for header
        headerLabel.setBounds(100, 10, 200, 30); // Adjusted position and size
        panel.add(headerLabel);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 60, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(150, 60, 200, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 100, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 100, 200, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 140, 100, 25);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticateAdmin();
            }
        });
    }

    private void authenticateAdmin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM admins WHERE username = ? AND password = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, password); // Use hashed password

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Admin Login Successful!");
                    dispose();
                    new AdminControlPanel(); // Open admin control panel
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Admin Credentials!");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}

package sop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InquiryPage extends JFrame {
    private JTextArea inquiryArea;
    private JComboBox<String> departmentCombo;
    private int customerId;

    public InquiryPage(int customerId) {
        this.customerId = customerId;

        setTitle("Customer Inquiry");
        setSize(400, 400); // Adjusted size to fit all components
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(144, 238, 144)); // Light green background
        panel.setLayout(null); // Use absolute positioning
        add(panel);

        // Load and resize the image icon
        ImageIcon icon = new ImageIcon("C:\\Users\\91789\\Downloads\\form.png");
        Image img = icon.getImage(); // Transform it
        Image resizedImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Resize to 100x100
        icon = new ImageIcon(resizedImg);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBounds(150, 10, 100, 100); // Position at the top of the panel
        panel.add(iconLabel);

        // Place components below the image
        placeComponents(panel, 120); // Passing the height of the image plus some padding

        setVisible(true);
    }

    private void placeComponents(JPanel panel, int yOffset) {
        JLabel inquiryLabel = new JLabel("Inquiry:");
        inquiryLabel.setBounds(10, yOffset, 80, 25);
        panel.add(inquiryLabel);

        inquiryArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(inquiryArea);
        scrollPane.setBounds(150, yOffset, 200, 100);
        panel.add(scrollPane);

        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setBounds(10, yOffset + 120, 80, 25);
        panel.add(departmentLabel);

        String[] departments = {"Sales", "General", "Billing", "Technical"};
        departmentCombo = new JComboBox<>(departments);
        departmentCombo.setBounds(150, yOffset + 120, 200, 25);
        panel.add(departmentCombo);

        JButton submitButton = new JButton("Submit Inquiry");
        submitButton.setBounds(150, yOffset + 160, 150, 25);
        panel.add(submitButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(150, yOffset + 200, 150, 25);
        panel.add(backButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitInquiry();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginPage(); // Open login page
                dispose(); // Close inquiry page
            }
        });
    }

    private void submitInquiry() {
        String inquiry = inquiryArea.getText();
        String department = (String) departmentCombo.getSelectedItem();

        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO inquiries (customer_id, details, department_assigned, status) VALUES (?, ?, ?, 'Pending')";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, customerId);
                ps.setString(2, inquiry);
                ps.setString(3, department);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Inquiry Submitted Successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}

package sop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminControlPanel extends JFrame {
    private JTable inquiryTable;
    private JTextField inquiryIdField;
    private JComboBox<String> departmentCombo;
    private DefaultTableModel tableModel;

    public AdminControlPanel() {
        setTitle("Admin Control Panel");
        setSize(1000, 700); // Adjusted size for better UI
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(144, 238, 144)); // Light green background
        add(panel);
        placeComponents(panel);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(new BorderLayout());

        JLabel inquiriesLabel = new JLabel("Inquiries");
        inquiriesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inquiriesLabel.setOpaque(true);
        inquiriesLabel.setBackground(new Color(144, 238, 144)); // Light green background
        panel.add(inquiriesLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Inquiry ID", "Customer ID", "Details", "Department", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        
        inquiryTable = new JTable(tableModel) {
            // Override to provide tooltip for specific cells
            public String getToolTipText(java.awt.event.MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                
                // Only show tooltip for the "Details" column (index 2)
                if (colIndex == 2) {
                    Object value = getValueAt(rowIndex, colIndex);
                    if (value != null) {
                        return "<html><p width='300px'>" + value.toString() + "</p></html>";
                    }
                }
                return null;
            }
        };
        
        JScrollPane scrollPane = new JScrollPane(inquiryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.setBackground(new Color(144, 238, 144)); // Light green background

        JLabel inquiryIdLabel = new JLabel("Inquiry ID:");
        bottomPanel.add(inquiryIdLabel);

        inquiryIdField = new JTextField(5);
        bottomPanel.add(inquiryIdField);

        JLabel departmentLabel = new JLabel("New Department:");
        bottomPanel.add(departmentLabel);

        String[] departments = {"Sales", "General", "Billing", "Technical"};
        departmentCombo = new JComboBox<>(departments);
        bottomPanel.add(departmentCombo);

        JButton assignButton = new JButton("Assign Department");
        bottomPanel.add(assignButton);

        JButton assignAllButton = new JButton("Assign All Inquiries");
        bottomPanel.add(assignAllButton);

        JButton refreshButton = new JButton("Refresh");
        bottomPanel.add(refreshButton);

        JButton removeCompletedButton = new JButton("Remove Completed Inquiries");
        bottomPanel.add(removeCompletedButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadInquiries();
            }
        });

        assignButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignDepartment();
            }
        });

        assignAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignAllInquiries();
            }
        });

        removeCompletedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeCompletedInquiries();
            }
        });

        loadInquiries(); // Load inquiries initially
    }

    private void loadInquiries() {
        tableModel.setRowCount(0); // Clear the table
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM inquiries WHERE is_active = true"; // Only load active inquiries
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);

                while (rs.next()) {
                    int id = rs.getInt("id");
                    int customerId = rs.getInt("customer_id");
                    String details = rs.getString("details");
                    String department = rs.getString("department_assigned");
                    String status = rs.getString("status");

                    tableModel.addRow(new Object[]{id, customerId, details, department, status});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void assignDepartment() {
        String inquiryIdStr = inquiryIdField.getText().trim();
        String newDepartment = (String) departmentCombo.getSelectedItem();

        if (inquiryIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Inquiry ID.");
            return;
        }

        int inquiryId;
        try {
            inquiryId = Integer.parseInt(inquiryIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Inquiry ID.");
            return;
        }

        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                // Check if the inquiry is completed or not active
                String checkQuery = "SELECT status, is_active FROM inquiries WHERE id = ?";
                PreparedStatement checkPs = connection.prepareStatement(checkQuery);
                checkPs.setInt(1, inquiryId);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    String status = rs.getString("status");
                    boolean isActive = rs.getBoolean("is_active");

                    if (!isActive) {
                        JOptionPane.showMessageDialog(this, "This inquiry is not active.");
                        return;
                    }

                    if ("Completed".equals(status)) {
                        JOptionPane.showMessageDialog(this, "This inquiry has already been completed.");
                        return;
                    }

                    // Proceed with assignment
                    String query = "UPDATE inquiries SET department_assigned = ?, status = 'In Progress' WHERE id = ?";
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setString(1, newDepartment);
                    ps.setInt(2, inquiryId);

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Inquiry assigned to " + newDepartment + " department.");
                        loadInquiries(); // Refresh the inquiries list
                    } else {
                        JOptionPane.showMessageDialog(this, "Inquiry ID not found.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Inquiry ID not found.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void assignAllInquiries() {
        String newDepartment = (String) departmentCombo.getSelectedItem();

        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                // Log the department being used for assignment
                System.out.println("Assigning all inquiries in the selected department: " + newDepartment);

                // Adjusted SQL query to select only pending and active inquiries in the selected department
                String query = "UPDATE inquiries SET department_assigned = ?, status = 'In Progress' WHERE department_assigned = ? AND status = 'Pending' AND is_active = true";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, newDepartment);
                ps.setString(2, newDepartment);

                int rowsAffected = ps.executeUpdate();
                
                // Check if any rows were affected
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "All pending inquiries in the selected department have been assigned to " + newDepartment + " department.");
                    loadInquiries(); // Refresh the inquiries list
                } else {
                    JOptionPane.showMessageDialog(this, "No pending inquiries found in the selected department.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void removeCompletedInquiries() {
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                // Set is_active to false for completed inquiries
                String query = "UPDATE inquiries SET is_active = false WHERE status = 'Completed'";
                PreparedStatement ps = connection.prepareStatement(query);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Completed inquiries have been removed.");
                    loadInquiries(); // Refresh the inquiries list
                } else {
                    JOptionPane.showMessageDialog(this, "No completed inquiries found.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}

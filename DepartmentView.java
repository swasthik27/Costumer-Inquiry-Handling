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

public class DepartmentView extends JFrame {
    private JTable inquiryTable;
    private JTextArea customerDetailsArea;
    private DefaultTableModel tableModel;
    private JComboBox<String> departmentComboBox;
    private Connection connection;
    private final Color LIGHT_GREEN = new Color(144, 238, 144); // Light green background color

    public DepartmentView() {
        setTitle("Department View");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        connection = DBConnection.getConnection();

        JPanel panel = new JPanel();
        panel.setBackground(LIGHT_GREEN); // Set background color
        add(panel);
        placeComponents(panel);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(LIGHT_GREEN); // Set background color
        panel.add(topPanel, BorderLayout.NORTH);

        JLabel departmentLabel = new JLabel("Select Department: ");
        topPanel.add(departmentLabel);

        // ComboBox for department selection
        departmentComboBox = new JComboBox<>(new String[]{"Sales", "General", "Billing", "Technical"});
        topPanel.add(departmentComboBox);

        JButton loadButton = new JButton("Load Inquiries");
        topPanel.add(loadButton);

        // Table setup
        String[] columnNames = {"Inquiry ID", "Customer ID", "Details", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        inquiryTable = new JTable(tableModel);
        inquiryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(inquiryTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(LIGHT_GREEN); // Set background color
        panel.add(bottomPanel, BorderLayout.SOUTH);

        customerDetailsArea = new JTextArea(5, 20);
        customerDetailsArea.setEditable(false);
        customerDetailsArea.setBackground(LIGHT_GREEN); // Set background color
        JScrollPane detailsScrollPane = new JScrollPane(customerDetailsArea);
        bottomPanel.add(detailsScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(LIGHT_GREEN); // Set background color
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        JButton viewCustomerButton = new JButton("View Customer Details");
        buttonPanel.add(viewCustomerButton);

        JButton completedButton = new JButton("Mark as Completed");
        buttonPanel.add(completedButton);

        JButton processingButton = new JButton("Mark as In Progress");
        buttonPanel.add(processingButton);

        // Add action listeners
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticateAndLoadInquiries();
            }
        });

        viewCustomerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewCustomerDetails();
            }
        });

        completedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateInquiryStatus("Completed");
            }
        });

        processingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateInquiryStatus("In Progress");
            }
        });
    }

    private void authenticateAndLoadInquiries() {
        String department = departmentComboBox.getSelectedItem().toString();
        String enteredPassword = JOptionPane.showInputDialog(this, "Enter password for " + department + " department:");

        if (enteredPassword != null && validatePassword(department, enteredPassword)) {
            loadDepartmentInquiries(department);
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect password. Access denied.");
        }
    }

    private boolean validatePassword(String department, String enteredPassword) {
        if (connection != null) {
            try {
                String query = "SELECT password FROM dept_auth WHERE department_name = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, department.toLowerCase());
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String correctPassword = rs.getString("password");
                    return correctPassword.equals(enteredPassword);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
        return false;
    }

    private void loadDepartmentInquiries(String department) {
        tableModel.setRowCount(0); // Clear the table

        if (connection != null) {
            try {
                // Load only inquiries that have been assigned a department, are in progress, and are active
                String query = "SELECT * FROM inquiries WHERE department_assigned = ? AND status = 'In Progress' AND is_active = 1";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, department);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    int customerId = rs.getInt("customer_id");
                    String details = rs.getString("details");
                    String status = rs.getString("status");

                    tableModel.addRow(new Object[]{id, customerId, details, status});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void viewCustomerDetails() {
        int selectedRow = inquiryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an inquiry to view customer details.");
            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 1);

        if (connection != null) {
            try {
                String query = "SELECT * FROM customers WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, customerId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String customerName = rs.getString("name");
                    String customerEmail = rs.getString("email");
                    String customerPhone = rs.getString("phone");

                    customerDetailsArea.setText("Customer Details:\n");
                    customerDetailsArea.append("Name: " + customerName + "\n");
                    customerDetailsArea.append("Email: " + customerEmail + "\n");
                    customerDetailsArea.append("Phone: " + customerPhone + "\n");
                } else {
                    customerDetailsArea.setText("Customer not found.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void updateInquiryStatus(String newStatus) {
        int selectedRow = inquiryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an inquiry to update status.");
            return;
        }

        int inquiryId = (int) tableModel.getValueAt(selectedRow, 0);

        if (connection != null) {
            try {
                // Prepare the update query
                String query = "UPDATE inquiries SET status = ? WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, newStatus);
                ps.setInt(2, inquiryId);

                // Execute the update
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Inquiry status updated to " + newStatus + ".");
                    loadDepartmentInquiries(departmentComboBox.getSelectedItem().toString()); // Refresh the inquiries list
                } else {
                    JOptionPane.showMessageDialog(this, "Inquiry ID not found.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

   public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DepartmentView();
        });
    }
}

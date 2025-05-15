import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class OrderHistoryPanel extends JPanel {
    private ShopCartGUI mainFrame;
    private final JTable orderTable;
    private final DefaultTableModel tableModel;
    private final JTextArea orderDetailsArea;
    
    public OrderHistoryPanel(ShopCartGUI frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JLabel titleLabel = new JLabel("Order History");
        titleLabel.setFont(StyleUtils.TITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Create table
        String[] columns = {"Order ID", "Date", "Total Amount (Rs.)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Style the table using StyleUtils
        orderTable.setFont(StyleUtils.NORMAL_FONT);
        orderTable.setRowHeight(30);
        orderTable.setShowGrid(true);
        orderTable.setGridColor(StyleUtils.PRIMARY_COLOR.brighter());
        orderTable.getTableHeader().setFont(StyleUtils.HEADING_FONT);
        orderTable.getTableHeader().setBackground(StyleUtils.PRIMARY_COLOR);
        orderTable.getTableHeader().setForeground(Color.BLACK); // Changed to black
        orderTable.setSelectionBackground(StyleUtils.PRIMARY_COLOR.brighter());
        orderTable.setSelectionForeground(StyleUtils.SECONDARY_COLOR);
        
        JScrollPane tableScrollPane = new JScrollPane(orderTable);
        tableScrollPane.getViewport().setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JLabel detailsLabel = new JLabel("Order Details:");
        detailsLabel.setFont(StyleUtils.HEADING_FONT);
        detailsLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        orderDetailsArea = new JTextArea(10, 40);
        orderDetailsArea.setFont(StyleUtils.NORMAL_FONT);
        orderDetailsArea.setEditable(false);
        orderDetailsArea.setBackground(StyleUtils.SECONDARY_COLOR);
        orderDetailsArea.setForeground(StyleUtils.TEXT_COLOR);
        JScrollPane detailsScrollPane = new JScrollPane(orderDetailsArea);
        detailsScrollPane.getViewport().setBackground(StyleUtils.SECONDARY_COLOR);
        
        detailsPanel.add(detailsLabel, BorderLayout.NORTH);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JButton backButton = StyleUtils.createStyledButton("Back to Menu");
        buttonPanel.add(backButton);
        
        // Create split pane for table and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                            tableScrollPane,
                                            detailsPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Add components to panel
        add(titlePanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        backButton.addActionListener(_ -> mainFrame.showPanel("MAIN_MENU"));
        
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderDetails();
            }
        });
        
        // Load orders when panel is shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadOrders();
            }
        });
    }
    
    private void loadOrders() {
        try {
            tableModel.setRowCount(0);
            String query = "SELECT order_id, order_date, total_amount FROM orders " +
                          "WHERE user_id = ? ORDER BY order_date DESC";
            PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
            pstmt.setInt(1, ShopCartGUI.getCurrentUserId());
            ResultSet rs = pstmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    dateFormat.format(rs.getTimestamp("order_date")),
                    rs.getInt("total_amount")
                };
                tableModel.addRow(row);
            }
            
            // Clear details area
            orderDetailsArea.setText("");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }
    
    private void showOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            orderDetailsArea.setText("");
            return;
        }
        
        try {
            int orderId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String query = "SELECT p.name, p.price, oi.quantity, (p.price * oi.quantity) as subtotal " +
                          "FROM order_details oi " +
                          "JOIN products p ON oi.product_id = p.product_id " +
                          "WHERE oi.order_id = ?";
            PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            StringBuilder details = new StringBuilder();
            details.append("Order #").append(orderId).append("\n\n");
            details.append(String.format("%-30s %-10s %-10s %-10s\n", 
                         "Product", "Price", "Quantity", "Subtotal"));
            details.append("-".repeat(60)).append("\n");
            
            while (rs.next()) {
                details.append(String.format("%-30s %-10d %-10d %-10d\n",
                    rs.getString("name"),
                    rs.getInt("price"),
                    rs.getInt("quantity"),
                    rs.getInt("subtotal")));
            }
            
            details.append("-".repeat(60)).append("\n");
            details.append(String.format("Total Amount: Rs. %d", 
                         tableModel.getValueAt(selectedRow, 2)));
            
            orderDetailsArea.setText(details.toString());
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading order details: " + e.getMessage());
        }
    }
}
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CartPanel extends JPanel {
    private ShopCartGUI mainFrame;
    private final JTable cartTable;
    private final DefaultTableModel tableModel;
    private final JLabel totalLabel;
    private final JProgressBar budgetBar;
    
    public CartPanel(ShopCartGUI frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JLabel titleLabel = new JLabel("Shopping Cart");
        titleLabel.setFont(StyleUtils.TITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Create table
        String[] columns = {"Product", "Price (Rs.)", "Quantity", "Subtotal (Rs.)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(tableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Style the table using StyleUtils
        cartTable.setFont(StyleUtils.NORMAL_FONT);
        cartTable.setRowHeight(30);
        cartTable.setShowGrid(true);
        cartTable.setGridColor(StyleUtils.PRIMARY_COLOR.brighter());
        cartTable.getTableHeader().setFont(StyleUtils.HEADING_FONT);
        cartTable.getTableHeader().setBackground(StyleUtils.PRIMARY_COLOR);
        cartTable.getTableHeader().setForeground(Color.BLACK); // Changed to black
        cartTable.setSelectionBackground(StyleUtils.PRIMARY_COLOR.brighter());
        cartTable.setSelectionForeground(StyleUtils.SECONDARY_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Create info panel
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        totalLabel = new JLabel("Total: Rs. 0");
        totalLabel.setFont(StyleUtils.HEADING_FONT);
        budgetBar = new JProgressBar(0, 100);
        budgetBar.setStringPainted(true);
        budgetBar.setPreferredSize(new Dimension(200, 20));
        budgetBar.setBorderPainted(false);
        
        JLabel budgetLabel = new JLabel("Budget Progress:");
        budgetLabel.setFont(StyleUtils.NORMAL_FONT);
        
        infoPanel.add(totalLabel);
        infoPanel.add(budgetLabel);
        infoPanel.add(budgetBar);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JButton removeButton = StyleUtils.createStyledButton("Remove Selected");
        JButton checkoutButton = StyleUtils.createStyledButton("Checkout");
        JButton backButton = StyleUtils.createStyledButton("Back to Menu");
        
        buttonPanel.add(removeButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(backButton);
        
        // Create bottom panel to hold info and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add components to panel
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        removeButton.addActionListener(_ -> removeFromCart());
        checkoutButton.addActionListener(_ -> checkout());
        backButton.addActionListener(_ -> mainFrame.showPanel("MAIN_MENU"));
        
        // Load cart when panel is shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadCart();
            }
        });
    }
    
    private void loadCart() {
        try {
            tableModel.setRowCount(0);
            String query = "SELECT p.name, p.price, c.quantity FROM cart c " +
                          "JOIN products p ON c.product_id = p.product_id " +
                          "WHERE c.user_id = ?";
            PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
            pstmt.setInt(1, ShopCartGUI.getCurrentUserId());
            ResultSet rs = pstmt.executeQuery();
            
            int total = 0;
            while (rs.next()) {
                String name = rs.getString("name");
                int price = rs.getInt("price");
                int quantity = rs.getInt("quantity");
                int subtotal = price * quantity;
                total += subtotal;
                
                Object[] row = {name, price, quantity, subtotal};
                tableModel.addRow(row);
            }
            
            totalLabel.setText("Total: Rs. " + total);
            
            // Update budget progress bar
            String budgetQuery = "SELECT budget FROM users WHERE user_id = ?";
            PreparedStatement budgetStmt = ShopCartGUI.getConnection().prepareStatement(budgetQuery);
            budgetStmt.setInt(1, ShopCartGUI.getCurrentUserId());
            ResultSet budgetRs = budgetStmt.executeQuery();
            
            if (budgetRs.next()) {
                int budget = budgetRs.getInt("budget");
                int percentage = budget > 0 ? (total * 100) / budget : 0;
                budgetBar.setValue(percentage);
                budgetBar.setString(percentage + "%");
                
                if (percentage >= 100) {
                    budgetBar.setForeground(Color.RED);
                } else if (percentage >= 80) {
                    budgetBar.setForeground(Color.ORANGE);
                } else {
                    budgetBar.setForeground(Color.GREEN);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cart: " + e.getMessage());
        }
    }
    
    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove!");
            return;
        }
        
        try {
            String productName = (String) tableModel.getValueAt(selectedRow, 0);
            String deleteQuery = "DELETE FROM cart WHERE user_id = ? AND product_id = " +
                               "(SELECT product_id FROM products WHERE name = ?)";
            PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(deleteQuery);
            pstmt.setInt(1, ShopCartGUI.getCurrentUserId());
            pstmt.setString(2, productName);
            pstmt.executeUpdate();
            
            loadCart();
            JOptionPane.showMessageDialog(this, "Item removed from cart!");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error removing item: " + e.getMessage());
        }
    }
    
    private void checkout() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }
        
        // Calculate total amount
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (Integer) tableModel.getValueAt(i, 3);
        }
        
        // Show payment panel with total amount
        PaymentPanel paymentPanel = (PaymentPanel) mainFrame.getPanel("PAYMENT");
        paymentPanel.setTotalAmount(total);
        mainFrame.showPanel("PAYMENT");
    }
}
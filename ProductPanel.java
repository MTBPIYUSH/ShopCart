import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductPanel extends JPanel {
    private ShopCartGUI mainFrame;
    private final JTable productTable;
    private final DefaultTableModel tableModel;
    private final JSpinner quantitySpinner;
    
    public ProductPanel(ShopCartGUI frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JLabel titleLabel = new JLabel("Available Products");
        titleLabel.setFont(StyleUtils.TITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Create table
        String[] columns = {"ID", "Name", "Description", "Price (Rs.)", "Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Style the table using StyleUtils
        productTable.setFont(StyleUtils.NORMAL_FONT);
        productTable.setRowHeight(30);
        productTable.setShowGrid(true);
        productTable.setGridColor(StyleUtils.PRIMARY_COLOR.brighter());
        productTable.getTableHeader().setFont(StyleUtils.HEADING_FONT);
        productTable.getTableHeader().setBackground(StyleUtils.PRIMARY_COLOR);
        productTable.getTableHeader().setForeground(Color.BLACK); // Changed to black
        productTable.setSelectionBackground(StyleUtils.PRIMARY_COLOR.brighter());
        productTable.setSelectionForeground(StyleUtils.SECONDARY_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.getViewport().setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Create control panel with styling
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(StyleUtils.NORMAL_FONT);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JButton addToCartButton = StyleUtils.createStyledButton("Add to Cart");
        JButton backButton = StyleUtils.createStyledButton("Back to Menu");
        
        controlPanel.add(quantityLabel);
        controlPanel.add(quantitySpinner);
        controlPanel.add(addToCartButton);
        controlPanel.add(backButton);
        
        // Add components to panel
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addToCartButton.addActionListener(_ -> addToCart());
        backButton.addActionListener(_ -> mainFrame.showPanel("MAIN_MENU"));
        
        // Load products when panel is shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadProducts();
            }
        });
    }
    
    private void loadProducts() {
        try {
            tableModel.setRowCount(0);
            String query = "SELECT * FROM products WHERE stock > 0";
            Statement stmt = ShopCartGUI.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("price"),
                    rs.getInt("stock")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }
    
    private void addToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product!");
            return;
        }
        
        int productId = (Integer) tableModel.getValueAt(selectedRow, 0);
        int quantity = (Integer) quantitySpinner.getValue();
        int availableStock = (Integer) tableModel.getValueAt(selectedRow, 4);
        
        if (quantity > availableStock) {
            JOptionPane.showMessageDialog(this, "Requested quantity exceeds available stock!");
            return;
        }
        
        try {
            // Calculate potential new total
            String priceQuery = "SELECT price FROM products WHERE product_id = ?";
            PreparedStatement priceStmt = ShopCartGUI.getConnection().prepareStatement(priceQuery);
            priceStmt.setInt(1, productId);
            ResultSet priceRs = priceStmt.executeQuery();
            priceRs.next();
            int price = priceRs.getInt("price");
            
            // Get current cart total
            String totalQuery = "SELECT SUM(p.price * c.quantity) as total " +
                              "FROM cart c JOIN products p ON c.product_id = p.product_id " +
                              "WHERE c.user_id = ?";
            PreparedStatement totalStmt = ShopCartGUI.getConnection().prepareStatement(totalQuery);
            totalStmt.setInt(1, ShopCartGUI.getCurrentUserId());
            ResultSet totalRs = totalStmt.executeQuery();
            totalRs.next();
            int currentTotal = totalRs.getInt("total");
            
            // Get user's budget
            String budgetQuery = "SELECT budget FROM users WHERE user_id = ?";
            PreparedStatement budgetStmt = ShopCartGUI.getConnection().prepareStatement(budgetQuery);
            budgetStmt.setInt(1, ShopCartGUI.getCurrentUserId());
            ResultSet budgetRs = budgetStmt.executeQuery();
            budgetRs.next();
            int budget = budgetRs.getInt("budget");
            
            // Check if budget is 0 or if new total exceeds budget
            if (budget == 0) {
                JPasswordField passwordField = new JPasswordField();
                int option = JOptionPane.showConfirmDialog(this,
                    new Object[]{"Your budget is 0. Enter your password to add items to cart:", passwordField},
                    "Budget Warning",
                    JOptionPane.OK_CANCEL_OPTION);
                    
                if (option == JOptionPane.OK_OPTION) {
                    String password = new String(passwordField.getPassword());
                    try {
                        // Verify password
                        String verifyQuery = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
                        PreparedStatement verifyStmt = ShopCartGUI.getConnection().prepareStatement(verifyQuery);
                        verifyStmt.setInt(1, ShopCartGUI.getCurrentUserId());
                        verifyStmt.setString(2, password);
                        ResultSet verifyRs = verifyStmt.executeQuery();
                        
                        if (!verifyRs.next()) {
                            JOptionPane.showMessageDialog(this,
                                "Incorrect password!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this,
                            "Error verifying password: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    return;
                }
            } else if (budget > 0 && (currentTotal + (price * quantity)) > budget) {
                JPasswordField passwordField = new JPasswordField();
                int option = JOptionPane.showConfirmDialog(this,
                    new Object[]{"This item exceeds your budget. Enter your password to continue:", passwordField},
                    "Budget Warning",
                    JOptionPane.OK_CANCEL_OPTION);
                    
                if (option == JOptionPane.OK_OPTION) {
                    String password = new String(passwordField.getPassword());
                    try {
                        // Verify password
                        String verifyQuery = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
                        PreparedStatement verifyStmt = ShopCartGUI.getConnection().prepareStatement(verifyQuery);
                        verifyStmt.setInt(1, ShopCartGUI.getCurrentUserId());
                        verifyStmt.setString(2, password);
                        ResultSet verifyRs = verifyStmt.executeQuery();
                        
                        if (!verifyRs.next()) {
                            JOptionPane.showMessageDialog(this,
                                "Incorrect password!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this,
                            "Error verifying password: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    return;
                }
            }
            
            // Add to cart
            String checkQuery = "SELECT quantity FROM cart WHERE user_id = ? AND product_id = ?";
            PreparedStatement checkStmt = ShopCartGUI.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, ShopCartGUI.getCurrentUserId());
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                String updateQuery = "UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";
                PreparedStatement updateStmt = ShopCartGUI.getConnection().prepareStatement(updateQuery);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, ShopCartGUI.getCurrentUserId());
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();
            } else {
                String insertQuery = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = ShopCartGUI.getConnection().prepareStatement(insertQuery);
                insertStmt.setInt(1, ShopCartGUI.getCurrentUserId());
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Product added to cart successfully!");
            loadProducts(); // Refresh the product list
            quantitySpinner.setValue(1); // Reset quantity
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding to cart: " + e.getMessage());
        }
    }
}

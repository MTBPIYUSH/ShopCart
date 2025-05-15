import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class PaymentPanel extends JPanel {
    private ShopCartGUI mainFrame;
    private JTextField cardNumberField = new JTextField();
    private JTextField cardHolderField = new JTextField();
    private JTextField expiryField = new JTextField();
    private JPasswordField cvvField = new JPasswordField(); // Changed to JPasswordField
    private JLabel totalAmountLabel = new JLabel();
    private double totalAmount;
    private final JComboBox<String> savedCardsComboBox;
    
    public PaymentPanel(ShopCartGUI frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JLabel titleLabel = new JLabel("Payment Details");
        titleLabel.setFont(StyleUtils.TITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Amount to pay
        totalAmountLabel = new JLabel("Total Amount: Rs. 0.00");
        totalAmountLabel.setFont(StyleUtils.HEADING_FONT);
        totalAmountLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        
        // Card number
        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberLabel.setFont(StyleUtils.NORMAL_FONT);
        cardNumberField = StyleUtils.createStyledTextField();
        cardNumberField.setColumns(16);
        
        // Card holder
        JLabel cardHolderLabel = new JLabel("Card Holder Name:");
        cardHolderLabel.setFont(StyleUtils.NORMAL_FONT);
        cardHolderField = StyleUtils.createStyledTextField();
        
        // Expiry date
        JLabel expiryLabel = new JLabel("Expiry Date (MM/YY):");
        expiryLabel.setFont(StyleUtils.NORMAL_FONT);
        expiryField = StyleUtils.createStyledTextField();
        expiryField.setColumns(5);
        
        // CVV
        JLabel cvvLabel = new JLabel("CVV:");
        cvvLabel.setFont(StyleUtils.NORMAL_FONT);
        cvvField = StyleUtils.createStyledPasswordField(); // Use password field
        cvvField.setColumns(3);
        
        // Add saved cards dropdown
        JLabel savedCardsLabel = new JLabel("Saved Cards:");
        savedCardsLabel.setFont(StyleUtils.NORMAL_FONT);
        savedCardsComboBox = new JComboBox<>();
        savedCardsComboBox.addItem("-- Select a saved card --");
        
        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(totalAmountLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(savedCardsLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(savedCardsComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(cardNumberLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(cardNumberField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(cardHolderLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(cardHolderField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(expiryLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(expiryField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(cvvLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(cvvField, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JButton payButton = StyleUtils.createStyledButton("Pay Now");
        JButton cancelButton = StyleUtils.createStyledButton("Cancel");
        
        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        payButton.addActionListener(_ -> processPayment());
        cancelButton.addActionListener(_ -> mainFrame.showPanel("CART"));
        
        // Initialize saved cards functionality
        savedCardsComboBox.addActionListener(_ -> {
            if (savedCardsComboBox.getSelectedIndex() > 0) {
                loadCardDetails((String) savedCardsComboBox.getSelectedItem());
            }
        });
        
        // Add component listener to load saved cards when panel is shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadSavedCards();
            }
        });
    }
    
    public void setTotalAmount(double amount) {
        this.totalAmount = amount;
        totalAmountLabel.setText(String.format("Total Amount: Rs. %.2f", amount));
    }
    
    private void loadSavedCards() {
        try {
            savedCardsComboBox.removeAllItems();
            savedCardsComboBox.addItem("-- Select a saved card --");
            
            String query = "SELECT card_number FROM saved_cards WHERE user_id = ?";
            PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
            pstmt.setInt(1, ShopCartGUI.getCurrentUserId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String cardNumber = rs.getString("card_number");
                String maskedNumber = "**** **** **** " + cardNumber.substring(12);
                savedCardsComboBox.addItem(maskedNumber);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading saved cards: " + e.getMessage());
        }
    }
    
    private void loadCardDetails(String maskedNumber) {
        try {
            String lastFour = maskedNumber.substring(maskedNumber.length() - 4);
            String query = "SELECT * FROM saved_cards WHERE user_id = ? AND card_number LIKE ?";
            PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
            pstmt.setInt(1, ShopCartGUI.getCurrentUserId());
            pstmt.setString(2, "%" + lastFour);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                cardNumberField.setText(rs.getString("card_number"));
                cardHolderField.setText(rs.getString("card_holder"));
                expiryField.setText(rs.getString("expiry_date"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading card details: " + e.getMessage());
        }
    }
    
    private void processPayment() {
        // Basic validation
        if (!validateFields()) {
            return;
        }
        
        try {
            // Start transaction
            Connection conn = ShopCartGUI.getConnection();
            conn.setAutoCommit(false);
            
            try {
                // Save card if it's new
                String cardNumber = cardNumberField.getText().trim();
                if (!isCardSaved(cardNumber)) {
                    String saveQuery = "INSERT INTO saved_cards (user_id, card_number, card_holder, expiry_date) VALUES (?, ?, ?, ?)";
                    PreparedStatement saveStmt = conn.prepareStatement(saveQuery);
                    saveStmt.setInt(1, ShopCartGUI.getCurrentUserId());
                    saveStmt.setString(2, cardNumber);
                    saveStmt.setString(3, cardHolderField.getText().trim());
                    saveStmt.setString(4, expiryField.getText().trim());
                    saveStmt.executeUpdate();
                }
                
                // Create order
                String orderQuery = "INSERT INTO orders (user_id, total_amount, order_date) VALUES (?, ?, NOW())";
                PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, ShopCartGUI.getCurrentUserId());
                orderStmt.setDouble(2, totalAmount);
                orderStmt.executeUpdate();
                
                // Get order ID
                ResultSet rs = orderStmt.getGeneratedKeys();
                rs.next();
                int orderId = rs.getInt(1);
                
                // Move items from cart to order_details
                String cartItemsQuery = "INSERT INTO order_details (order_id, product_id, quantity, price_at_time) " +
                                      "SELECT ?, c.product_id, c.quantity, p.price " +
                                      "FROM cart c JOIN products p ON c.product_id = p.product_id " +
                                      "WHERE c.user_id = ?";
                PreparedStatement cartItemsStmt = conn.prepareStatement(cartItemsQuery);
                cartItemsStmt.setInt(1, orderId);
                cartItemsStmt.setInt(2, ShopCartGUI.getCurrentUserId());
                cartItemsStmt.executeUpdate();
                
                // Update product stock
                String updateStockQuery = "UPDATE products p " +
                                        "JOIN cart c ON p.product_id = c.product_id " +
                                        "SET p.stock = p.stock - c.quantity " +
                                        "WHERE c.user_id = ?";
                PreparedStatement updateStockStmt = conn.prepareStatement(updateStockQuery);
                updateStockStmt.setInt(1, ShopCartGUI.getCurrentUserId());
                updateStockStmt.executeUpdate();
                
                // Update user's budget
                String updateBudgetQuery = "UPDATE users SET budget = GREATEST(0, budget - ?) WHERE user_id = ?";
                PreparedStatement updateBudgetStmt = conn.prepareStatement(updateBudgetQuery);
                updateBudgetStmt.setDouble(1, totalAmount);
                updateBudgetStmt.setInt(2, ShopCartGUI.getCurrentUserId());
                updateBudgetStmt.executeUpdate();
                
                // Clear cart
                String clearCartQuery = "DELETE FROM cart WHERE user_id = ?";
                PreparedStatement clearCartStmt = conn.prepareStatement(clearCartQuery);
                clearCartStmt.setInt(1, ShopCartGUI.getCurrentUserId());
                clearCartStmt.executeUpdate();
                
                // Commit transaction
                conn.commit();
                
                JOptionPane.showMessageDialog(this,
                    "Payment successful! Your order has been placed.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                clearFields();
                mainFrame.showPanel("MAIN_MENU");
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error processing payment: " + e.getMessage(),
                "Payment Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isCardSaved(String cardNumber) throws SQLException {
        String query = "SELECT card_id FROM saved_cards WHERE user_id = ? AND card_number = ?";
        PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
        pstmt.setInt(1, ShopCartGUI.getCurrentUserId());
        pstmt.setString(2, cardNumber);
        ResultSet rs = pstmt.executeQuery();
        return rs.next();
    }
    
    private boolean validateFields() {
        String cardNumber = cardNumberField.getText().trim();
        String cardHolder = cardHolderField.getText().trim();
        String expiry = expiryField.getText().trim();
        String cvv = new String(cvvField.getPassword()).trim(); // Get password from JPasswordField
        
        if (cardNumber.isEmpty() || cardHolder.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return false;
        }
        
        // Validate card number (16 digits)
        if (!cardNumber.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this, "Invalid card number! Must be 16 digits.");
            return false;
        }
        
        // Validate expiry date (MM/YY format)
        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Invalid expiry date! Use MM/YY format.");
            return false;
        }
        
        // Validate CVV (3 digits)
        if (!cvv.matches("\\d{3}")) {
            JOptionPane.showMessageDialog(this, "Invalid CVV! Must be 3 digits.");
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        cardNumberField.setText("");
        cardHolderField.setText("");
        expiryField.setText("");
        cvvField.setText("");
        totalAmountLabel.setText("Total Amount: Rs. 0.00");
    }
    
    // Add action listener for saved cards
    public void initializeCardComboBox() {
        savedCardsComboBox.addActionListener(_ -> {
        if (savedCardsComboBox.getSelectedIndex() > 0) {
            loadCardDetails((String) savedCardsComboBox.getSelectedItem());
        }
    });
    
    // Add component listener to load saved cards when panel is shown
    addComponentListener(new java.awt.event.ComponentAdapter() {
        @Override
        public void componentShown(java.awt.event.ComponentEvent e) {
            loadSavedCards();
        }
    });
}
}
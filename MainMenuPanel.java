import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class MainMenuPanel extends JPanel {
    private ShopCartGUI mainFrame;
    private final JLabel userInfoLabel;
    private final JLabel budgetLabel;
    
    public MainMenuPanel(ShopCartGUI frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
                
        // Create center panel with background image
        JPanel centerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgImage = new ImageIcon("background.jpg"); // Add your background image file
                g.drawImage(bgImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Create user info panel
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        userInfoPanel.setOpaque(false);
        userInfoLabel = new JLabel("");
        budgetLabel = new JLabel("");
        userInfoLabel.setFont(StyleUtils.HEADING_FONT);
        userInfoLabel.setForeground(StyleUtils.SECONDARY_COLOR);
        budgetLabel.setFont(StyleUtils.HEADING_FONT);
        budgetLabel.setForeground(StyleUtils.SECONDARY_COLOR);
        
        JButton browseButton = StyleUtils.createStyledButton("Browse Products");
        JButton cartButton = StyleUtils.createStyledButton("View Cart");
        JButton historyButton = StyleUtils.createStyledButton("Order History");
        JButton resetBudgetButton = StyleUtils.createStyledButton("Reset Budget");
        JButton logoutButton = StyleUtils.createStyledButton("Logout");
        JButton feedbackButton = StyleUtils.createStyledButton("Feedback");
        JButton contactButton = StyleUtils.createStyledButton("Contact Us");
        
        // Add to your button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(feedbackButton);
        buttonPanel.add(contactButton);
        
        // Add action listeners
        feedbackButton.addActionListener(_ -> frame.showPanel("FEEDBACK"));
        contactButton.addActionListener(_ -> frame.showPanel("CONTACT_US"));
        
        // Add components to center panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 400, 10, 10);
        userInfoPanel.add(userInfoLabel);
        userInfoPanel.add(budgetLabel);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 400, 20, 10);
        centerPanel.add(userInfoPanel, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 400, 5, 10);
        centerPanel.add(browseButton, gbc);
        
        gbc.gridy = 3;
        centerPanel.add(cartButton, gbc);
        
        gbc.gridy = 4;
        centerPanel.add(historyButton, gbc);
        
        gbc.gridy = 5;
        centerPanel.add(resetBudgetButton, gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 400, 10, 10);
        centerPanel.add(logoutButton, gbc);
        
        // Add the button panel with feedback and contact buttons
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 400, 5, 10);
        centerPanel.add(buttonPanel, gbc);
        
        // Add panels to main panel
        add(centerPanel, BorderLayout.CENTER);
        
        // Add component listener to update user info when panel is shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                updateUserInfo();
            }
        });
        
        // Add action listeners
        browseButton.addActionListener(_ -> mainFrame.showPanel("PRODUCTS"));
        cartButton.addActionListener(_ -> mainFrame.showPanel("CART"));
        historyButton.addActionListener(_ -> mainFrame.showPanel("ORDER_HISTORY"));
        resetBudgetButton.addActionListener(_ -> resetBudget());
        logoutButton.addActionListener(_ -> {
            ShopCartGUI.setCurrentUserId(-1);
            mainFrame.showPanel("LOGIN");
        });
    }
    
    @SuppressWarnings("unused")
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private void resetBudget() {
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(this,
            new Object[]{"Enter your password to reset budget:", passwordField},
            "Reset Budget",
            JOptionPane.OK_CANCEL_OPTION);
            
        if (option == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            try {
                // Verify password
                String query = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
                PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
                pstmt.setInt(1, ShopCartGUI.getCurrentUserId());
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    // Password correct, show budget input dialog
                    String newBudgetStr = JOptionPane.showInputDialog(this,
                        "Enter new budget amount (Rs.):",
                        "Reset Budget",
                        JOptionPane.PLAIN_MESSAGE);
                        
                    if (newBudgetStr != null && !newBudgetStr.trim().isEmpty()) {
                        try {
                            int newBudget = Integer.parseInt(newBudgetStr);
                            if (newBudget <= 0) {
                                JOptionPane.showMessageDialog(this,
                                    "Budget must be greater than 0!",
                                    "Invalid Budget",
                                    JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            
                            // Update budget
                            String updateQuery = "UPDATE users SET budget = ? WHERE user_id = ?";
                            PreparedStatement updateStmt = ShopCartGUI.getConnection().prepareStatement(updateQuery);
                            updateStmt.setInt(1, newBudget);
                            updateStmt.setInt(2, ShopCartGUI.getCurrentUserId());
                            updateStmt.executeUpdate();
                            
                            JOptionPane.showMessageDialog(this,
                                "Budget updated successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(this,
                                "Invalid budget amount!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Incorrect password!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error resetting budget: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void updateUserInfo() {
        try {
            String query = "SELECT username, budget FROM users WHERE user_id = ?";
            PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
            pstmt.setInt(1, ShopCartGUI.getCurrentUserId());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String username = rs.getString("username");
                int budget = rs.getInt("budget");
                userInfoLabel.setText("Welcome, " + username);
                budgetLabel.setText("Current Budget: Rs. " + budget);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user info: " + e.getMessage());
        }
    }
}
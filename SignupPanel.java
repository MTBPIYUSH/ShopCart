import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class SignupPanel extends JPanel {
    private ShopCartGUI mainFrame;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;
    private final JTextField emailField;
    private final JTextField budgetField;
    
    public SignupPanel(ShopCartGUI frame) {
        this.mainFrame = frame;
        setLayout(new GridBagLayout());
        setBackground(StyleUtils.SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Create components
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(StyleUtils.TITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(StyleUtils.NORMAL_FONT);
        usernameField = StyleUtils.createStyledTextField();
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(StyleUtils.NORMAL_FONT);
        passwordField = StyleUtils.createStyledPasswordField();
        
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(StyleUtils.NORMAL_FONT);
        confirmPasswordField = StyleUtils.createStyledPasswordField();
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(StyleUtils.NORMAL_FONT);
        emailField = StyleUtils.createStyledTextField();

        JLabel budgetLabel = new JLabel("Monthly Budget (Rs.):");
        budgetLabel.setFont(StyleUtils.NORMAL_FONT);
        budgetField = StyleUtils.createStyledTextField();
        
        JButton signupButton = StyleUtils.createStyledButton("Sign Up");
        JButton backButton = StyleUtils.createStyledButton("Back to Login");
        
        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 30, 10);
        add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(confirmLabel, gbc);
        
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(emailLabel, gbc);

        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(budgetLabel, gbc);
        
        gbc.gridx = 1;
        add(budgetField, gbc);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(buttonPanel, gbc);
        
        // Add action listeners
        signupButton.addActionListener(_ -> signup());
        backButton.addActionListener(_ -> {
            clearFields();
            mainFrame.showPanel("LOGIN");
        });
    }
    
    private void signup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText();
        String budgetStr = budgetField.getText();
        
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || budgetStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }
        
        try {
            int budget = Integer.parseInt(budgetStr);
            if (budget <= 0) {
                JOptionPane.showMessageDialog(this, "Budget must be greater than 0!");
                return;
            }
            
            // Check if username exists
            String checkQuery = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement checkStmt = ShopCartGUI.getConnection().prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
                return;
            }
            
            // Insert new user
            String insertQuery = "INSERT INTO users (username, password, email, budget) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = ShopCartGUI.getConnection().prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.setInt(4, budget);
            insertStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Account created successfully!");
            clearFields();
            mainFrame.showPanel("LOGIN");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid budget amount!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating account: " + e.getMessage());
        }
    }
    
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        budgetField.setText("");
    }
}
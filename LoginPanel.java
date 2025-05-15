import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private ShopCartGUI mainFrame;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    
    public LoginPanel(ShopCartGUI frame) {
        this.mainFrame = frame;
        setLayout(new GridBagLayout());
        setBackground(StyleUtils.SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Create components
        JLabel titleLabel = new JLabel("Welcome to ShopCart");
        titleLabel.setFont(StyleUtils.TITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(StyleUtils.NORMAL_FONT);
        usernameField = StyleUtils.createStyledTextField();
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(StyleUtils.NORMAL_FONT);
        passwordField = StyleUtils.createStyledPasswordField();
        
        JButton loginButton = StyleUtils.createStyledButton("Login");
        JButton signupButton = StyleUtils.createStyledButton("Sign Up");
        
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
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(buttonPanel, gbc);
        
        // Add action listeners
        loginButton.addActionListener(_ -> login());
        signupButton.addActionListener(_ -> mainFrame.showPanel("SIGNUP"));
    }
    
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        try {
            String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                ShopCartGUI.setCurrentUserId(rs.getInt("user_id"));
                mainFrame.showPanel("MAIN_MENU");
                usernameField.setText("");
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error during login: " + e.getMessage());
        }
    }
}
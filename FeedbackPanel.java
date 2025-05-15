import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class FeedbackPanel extends JPanel {
    private ShopCartGUI mainFrame;
    private final JTextArea feedbackArea;
    private final JTextField emailField;
    
    public FeedbackPanel(ShopCartGUI frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JLabel titleLabel = new JLabel("Feedback");
        titleLabel.setFont(StyleUtils.TITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel emailLabel = new JLabel("Your Email:");
        emailLabel.setFont(StyleUtils.NORMAL_FONT);
        emailField = StyleUtils.createStyledTextField();
        
        JLabel feedbackLabel = new JLabel("Your Feedback:");
        feedbackLabel.setFont(StyleUtils.NORMAL_FONT);
        feedbackArea = new JTextArea(10, 40);
        feedbackArea.setFont(StyleUtils.NORMAL_FONT);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 5, 10);
        formPanel.add(feedbackLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 5, 10);
        formPanel.add(scrollPane, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JButton submitButton = StyleUtils.createStyledButton("Submit Feedback");
        JButton backButton = StyleUtils.createStyledButton("Back to Menu");
        
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);
        
        // Add components to panel
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        submitButton.addActionListener(_ -> submitFeedback());
        backButton.addActionListener(_ -> mainFrame.showPanel("MAIN_MENU"));
    }
    
    private void submitFeedback() {
        String email = emailField.getText().trim();
        String feedback = feedbackArea.getText().trim();
        
        if (email.isEmpty() || feedback.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }
        
        try {
            String query = "INSERT INTO feedback (user_id, email, feedback, feedback_date) VALUES (?, ?, ?, NOW())";
            PreparedStatement pstmt = ShopCartGUI.getConnection().prepareStatement(query);
            pstmt.setInt(1, ShopCartGUI.getCurrentUserId());
            pstmt.setString(2, email);
            pstmt.setString(3, feedback);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Thank you for your feedback!");
            emailField.setText("");
            feedbackArea.setText("");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error submitting feedback: " + e.getMessage());
        }
    }
}
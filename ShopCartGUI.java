import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class ShopCartGUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/shopcartdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345678";
    private static Connection connection;
    private static int currentUserId = -1;
    
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final LoginPanel loginPanel;
    private final SignupPanel signupPanel;
    private final MainMenuPanel mainMenuPanel;
    private final ProductPanel productPanel;
    private final CartPanel cartPanel;
    private final OrderHistoryPanel orderHistoryPanel;
    private final FeedbackPanel feedbackPanel;
    private final ContactUsPanel contactUsPanel;
    private final PaymentPanel paymentPanel;
    
    public ShopCartGUI() {
        // Set a modern look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                System.err.println("Failed to set Look and Feel");
            }
        }
        
        setTitle("ShopCart - Your Shopping Companion");
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set window icon
        ImageIcon icon = new ImageIcon("icon.png");
        setIconImage(icon.getImage());
        
        // Set window background color and border
        getContentPane().setBackground(StyleUtils.SECONDARY_COLOR);
        getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, StyleUtils.PRIMARY_COLOR));
        
        // Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Database connection error: " + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Initialize layout with styling
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Initialize panels
        loginPanel = new LoginPanel(this);
        signupPanel = new SignupPanel(this);
        mainMenuPanel = new MainMenuPanel(this);
        productPanel = new ProductPanel(this);
        cartPanel = new CartPanel(this);
        orderHistoryPanel = new OrderHistoryPanel(this);
        feedbackPanel = new FeedbackPanel(this);
        contactUsPanel = new ContactUsPanel(this);
        paymentPanel = new PaymentPanel(this);
        
        // Add panels to main panel
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(signupPanel, "SIGNUP");
        mainPanel.add(mainMenuPanel, "MAIN_MENU");
        mainPanel.add(productPanel, "PRODUCTS");
        mainPanel.add(cartPanel, "CART");
        mainPanel.add(orderHistoryPanel, "ORDER_HISTORY");
        mainPanel.add(feedbackPanel, "FEEDBACK");
        mainPanel.add(contactUsPanel, "CONTACT_US");
        mainPanel.add(paymentPanel, "PAYMENT");
        
        add(mainPanel);
        
        // Show login panel initially
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
    
    public static Connection getConnection() {
        return connection;
    }
    
    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }
    
    public static int getCurrentUserId() {
        return currentUserId;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ShopCartGUI().setVisible(true);
        });
    }
    
    public JPanel getPanel(String panelName) {
        return switch (panelName) {
            case "PAYMENT" -> paymentPanel;
            default -> null;
        }; // Add other cases as needed
    }
}
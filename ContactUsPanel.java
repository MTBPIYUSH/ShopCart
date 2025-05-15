import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ContactUsPanel extends JPanel {
    private ShopCartGUI mainFrame;
    
    public ContactUsPanel(ShopCartGUI frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleUtils.SECONDARY_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JLabel titleLabel = new JLabel("Contact Us");
        titleLabel.setFont(StyleUtils.TITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Create main content panel with split layout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Section A: Team Members (75% width)
        JPanel teamSection = new JPanel(new BorderLayout(0, 20));
        teamSection.setBackground(StyleUtils.SECONDARY_COLOR);
        teamSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 2, StyleUtils.PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Team section heading panel
        JPanel teamHeadingPanel = new JPanel();
        teamHeadingPanel.setBackground(StyleUtils.PRIMARY_COLOR);
        teamHeadingPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel teamHeading = new JLabel("Designed and Developed by");
        teamHeading.setFont(StyleUtils.HEADING_FONT.deriveFont(Font.BOLD, 22));
        teamHeading.setForeground(StyleUtils.SECONDARY_COLOR);
        teamHeadingPanel.add(teamHeading);
        
        // Team members panel
        JPanel devsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        devsPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        devsPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        devsPanel.add(createDevPanel("T. Jagadish", "Lead Developer", 
            "jagadish.thumde@aurora.edu.in", "+91 6304812493"));
        devsPanel.add(createDevPanel("L. Manasa", "UI/UX Developer", 
            "manasa.lingala@aurora.edu.in", "+91 8639651349"));
        devsPanel.add(createDevPanel("Piyush Kumar Puria", "Backend Developer", 
            "piyushkumar.puria@aurora.edu.in", "+91 9301973841"));
        
        teamSection.add(teamHeadingPanel, BorderLayout.NORTH);
        teamSection.add(devsPanel, BorderLayout.CENTER);
        
        // Section B: Mentor (25% width)
        JPanel mentorSection = new JPanel(new BorderLayout(0, 20));
        mentorSection.setBackground(StyleUtils.SECONDARY_COLOR);
        mentorSection.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Mentor section heading panel
        JPanel mentorHeadingPanel = new JPanel();
        mentorHeadingPanel.setBackground(StyleUtils.PRIMARY_COLOR);
        mentorHeadingPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel mentorHeading = new JLabel("Guided and Mentored by");
        mentorHeading.setFont(StyleUtils.HEADING_FONT.deriveFont(Font.BOLD, 22));
        mentorHeading.setForeground(StyleUtils.SECONDARY_COLOR);
        mentorHeadingPanel.add(mentorHeading);
        
        // Mentor details panel
        JPanel mentorPanel = createMentorPanel();
        
        mentorSection.add(mentorHeadingPanel, BorderLayout.NORTH);
        mentorSection.add(mentorPanel, BorderLayout.CENTER);
        
        // Add sections to content panel with weights
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.75;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(teamSection, gbc);
        
        gbc.weightx = 0.25;
        gbc.gridx = 1;
        contentPanel.add(mentorSection, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(StyleUtils.SECONDARY_COLOR);
        JButton backButton = StyleUtils.createStyledButton("Back to Menu");
        buttonPanel.add(backButton);
        
        // Add components to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listener
        backButton.addActionListener(_ -> mainFrame.showPanel("MAIN_MENU"));
    }
    
    private JPanel createMentorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(StyleUtils.PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(StyleUtils.PRIMARY_COLOR.darker(), 2),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        
        // Mentor image first
        ImageIcon originalIcon = new ImageIcon("team_mentor.jpg");
        Image scaledImage = originalIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        
        JLabel imageLabel = new JLabel(scaledIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D g2) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Shape shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                    g2.setClip(shape);
                }
                super.paintComponent(g);
            }
        };
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(180, 180));
        imageLabel.setMaximumSize(new Dimension(180, 180));
        
        // Mentor details with better formatting and proper width
        String mentorDetails = "<html><div style='text-align: center; width: 100%;'>" +
            "<span style='font-size: 16px; font-weight: bold;'>Mr. K. RaviKanth</span><br><br>" +
            "<span style='font-size: 12px;'>" +
            "B.Tech, M.Tech, (Ph.D.),<br>" +
            "FI2OR, UACEE, PMIFERP,<br>" +
            "LMIAENG, CSTA, IAO<br><br>" +
            "<b>Assistant Professor</b><br>" +
            "Department of CSE, SoE<br><br>" +
            "<i>Coordinator/SPOC</i><br>" +
            "Centre for Future Skills/<br>" +
            "SIH/ Algorand Blockchain Club/<br>" +
            "ICT Academy<br><br>" +
            "<b>Aurora University</b>" +
            "</span></div></html>";
            
        JLabel detailsLabel = new JLabel(mentorDetails);
        detailsLabel.setFont(StyleUtils.NORMAL_FONT);
        detailsLabel.setForeground(StyleUtils.SECONDARY_COLOR);
        detailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailsLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        panel.add(imageLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(detailsLabel);
        
        return panel;
    }
    
    private JPanel createDevPanel(String name, String role, String email, String phone) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(StyleUtils.PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(StyleUtils.PRIMARY_COLOR.darker(), 2),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        panel.setPreferredSize(new Dimension(300, 400));
        
        // Create image label with rounded corners first
        String imagePath = name.contains("Jagadish") ? "team_member_1.jpg" :
                      name.contains("Manasa") ? "team_member_2.jpg" : "team_member_3.jpg";
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(240, 240, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        
        JLabel imageLabel = new JLabel(scaledIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D g2) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Shape shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                    g2.setClip(shape);
                }
                super.paintComponent(g);
            }
        };
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(240, 240));
        imageLabel.setMaximumSize(new Dimension(240, 240));
        
        // Developer details with consistent formatting
        String devDetails = "<html><div style='text-align: center; width: 100%;'>" +
            "<span style='font-size: 16px; font-weight: bold;'>" + name + "</span><br><br>" +
            "<span style='font-size: 14px;'>" +
            "<b>" + role + "</b><br><br>" +
            "<u>" + email + "</u><br>" +
            phone + "<br>" +
            "</span></div></html>";
            
        JLabel detailsLabel = new JLabel(devDetails);
        detailsLabel.setFont(StyleUtils.NORMAL_FONT);
        detailsLabel.setForeground(StyleUtils.SECONDARY_COLOR);
        detailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailsLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        detailsLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panel.add(imageLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(detailsLabel);
        
        return panel;
    }
}
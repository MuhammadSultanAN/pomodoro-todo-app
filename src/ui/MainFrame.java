package ui;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout rootLayout = new CardLayout();
    private final JPanel root = new JPanel(rootLayout);
    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pages = new JPanel(pageLayout);
    private final StreakManager streakManager = new StreakManager();
    private StreakPanel streakPanel;

    public MainFrame() {
        super("PomodoroTimer | A Journal of Productivity");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 680));
        setSize(1100, 780);
        setLocationRelativeTo(null);

        root.setBackground(Theme.CREAM);
        root.add(buildWelcome(), "welcome");
        root.add(buildApplication(), "app");
        setContentPane(root);
        rootLayout.show(root, "welcome");
    }

    private JPanel buildWelcome() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Theme.CREAM);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.LIGHT);
        card.setBorder(new CompoundBorder(new LineBorder(Theme.BORDER, 1), new EmptyBorder(65, 70, 65, 70)));

        JLabel eyebrow = new JLabel("✦  A JOURNAL OF PRODUCTIVITY  ✦");
        eyebrow.setFont(Theme.sans(11, Font.BOLD));
        eyebrow.setForeground(Theme.GOLD);
        JLabel title = Theme.label("PomodoroTimer", 50, Theme.BROWN, true);
        JLabel desc1 = new JLabel("Manage your tasks, focus on your goals,", SwingConstants.CENTER);
        JLabel desc2 = new JLabel("and build better habits every day.", SwingConstants.CENTER);
        for (JLabel d : new JLabel[]{desc1, desc2}) {
            d.setFont(Theme.sans(14, Font.PLAIN));
            d.setForeground(Theme.MUTED);
        }
        JButton start = Theme.button("Get Started", Theme.SAGE, Color.WHITE);
        start.addActionListener(e -> { rootLayout.show(root, "app"); pageLayout.show(pages, "dashboard"); });

        card.add(eyebrow);
        card.add(Box.createVerticalStrut(24));
        card.add(title);
        card.add(Box.createVerticalStrut(18));
        card.add(desc1);
        card.add(Box.createVerticalStrut(2));
        card.add(desc2);
        card.add(Box.createVerticalStrut(30));
        card.add(start);
        Theme.align(card, Component.CENTER_ALIGNMENT, false);
        outer.add(card);
        return outer;
    }

    private JPanel buildApplication() {
        JPanel app = new JPanel(new BorderLayout(0, 18));
        app.setBackground(Theme.CREAM);
        app.setBorder(new EmptyBorder(18, 30, 15, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        nav.setOpaque(false);
        String[][] items = {{"Home", "dashboard"}, {"Pomodoro Timer", "timer"}, {"My Tasks", "tasks"}, {"My Streak", "streak"}};
        for (String[] item : items) {
            JButton b = Theme.button(item[0], Theme.CREAM, Theme.BROWN);
            b.setBorder(new EmptyBorder(9, 12, 9, 12));
            b.addActionListener(e -> showPage(item[1]));
            nav.add(b);
        }
        header.add(Theme.label("PomodoroTimer", 28, Theme.BROWN, true), BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        header.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
        app.add(header, BorderLayout.NORTH);

        pages.setOpaque(false);
        pages.add(new HomePanel(this::showPage), "dashboard");
        pages.add(new TimerPanel(streakManager), "timer");
        pages.add(new TaskPanel(), "tasks");
        streakPanel = new StreakPanel(streakManager);
        pages.add(streakPanel, "streak");
        app.add(pages, BorderLayout.CENTER);

        JLabel footer = new JLabel("Stay Focused, Stay Productive.", SwingConstants.CENTER);
        footer.setForeground(Theme.MUTED);
        footer.setFont(Theme.sans(11, Font.PLAIN));
        footer.setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));
        app.add(footer, BorderLayout.SOUTH);
        return app;
    }

    private void showPage(String name) {
        pageLayout.show(pages, name);
        
        if (name.equals("streak") && streakPanel != null) {
            streakPanel.refreshData();
        }
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        try {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName()
            );
        } catch (Exception ignored) {}

        new MainFrame().setVisible(true);
    });
    }
}

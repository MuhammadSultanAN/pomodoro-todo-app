package ui;
import javax.swing.*;
import java.awt.*;

public class StreakPanel extends JPanel {
    public StreakPanel() {
        Theme.pageHeader(this, "Daily Streak 🔥", "Build consistency, one productive day at a time.");

        JPanel main = Theme.cardPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        JLabel caption = new JLabel("Current Streak");
        caption.setForeground(Theme.MUTED);
        JLabel message = new JLabel("Start your first productive day!");
        message.setForeground(Theme.MUTED);
        main.add(Theme.label("🔥", 55, Theme.ROSE, true));
        main.add(Theme.label("0 Days", 45, Theme.ROSE, true));
        main.add(caption);
        main.add(Box.createVerticalStrut(8));
        main.add(message);
        Theme.align(main, Component.CENTER_ALIGNMENT, false);
        add(main);
        add(Box.createVerticalStrut(16));

        JPanel stats = new JPanel(new GridLayout(1, 2, 15, 0));
        stats.setOpaque(false);
        Theme.addStat(stats, "0", "Longest Streak");
        Theme.addStat(stats, "0", "Total Active Days");
        add(stats);
        add(Box.createVerticalStrut(16));

        JPanel history = Theme.cardPanel();
        history.setLayout(new BorderLayout());
        history.add(Theme.label("Activity History", 23, Theme.DARK_BROWN, true), BorderLayout.NORTH);
        JTextArea calendar = new JTextArea("Riwayat aktivitas akan tampil di sini.");
        calendar.setEditable(false);
        calendar.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        calendar.setBackground(Theme.LIGHT);
        calendar.setForeground(Theme.BROWN);
        history.add(new JScrollPane(calendar), BorderLayout.CENTER);
        add(history);
        add(Box.createVerticalStrut(12));

        JPanel info = Theme.cardPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(Theme.label("How to maintain your streak?", 20, Theme.DARK_BROWN, true));
        JLabel rule = new JLabel("<html>Complete at least one Pomodoro Session each day to maintain your daily streak.<br>Stay consistent and keep improving every day!</html>");
        rule.setForeground(Theme.MUTED);
        info.add(rule);
        Theme.align(info, Component.LEFT_ALIGNMENT, false);
        add(info);

        Theme.align(this, Component.LEFT_ALIGNMENT, true);
    }
}

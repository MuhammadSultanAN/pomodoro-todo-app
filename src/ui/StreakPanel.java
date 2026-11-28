package ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class StreakPanel extends JPanel {

    private final StreakManager streakManager;

    private JLabel streakLabel;
    private JLabel message;
    private JLabel longestLabel;
    private JLabel totalLabel;
    private JTextArea calendar;

    public StreakPanel(StreakManager streakManager) {

        this.streakManager = streakManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        Theme.pageHeader(this, "Daily Streak 🔥",
                "Build consistency, one productive day at a time.");

        // Main Streak Card
        JPanel main = Theme.cardPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JLabel caption = new JLabel("Current Streak");
        caption.setForeground(Theme.MUTED);

        message = new JLabel("Start your first productive day!");
        message.setForeground(Theme.MUTED);

        JLabel fire = Theme.label("🔥", 55, Theme.ROSE, true);

        streakLabel = Theme.label("0 Days", 45,
                Theme.ROSE, true);

        main.add(fire);
        main.add(streakLabel);
        main.add(caption);
        main.add(Box.createVerticalStrut(8));
        main.add(message);

        Theme.align(main, Component.CENTER_ALIGNMENT, false);

        add(main);
        add(Box.createVerticalStrut(16));

        // Statistics
        JPanel stats = new JPanel(new GridLayout(1, 2, 15, 0));
        stats.setOpaque(false);

        JPanel longestCard = createStatCard("0", "Longest Streak");
        JPanel totalCard = createStatCard("0", "Total Active Days");

        longestLabel = (JLabel) longestCard.getClientProperty("valueLabel");
        totalLabel = (JLabel) totalCard.getClientProperty("valueLabel");

        stats.add(longestCard);
        stats.add(totalCard);

        add(stats);
        add(Box.createVerticalStrut(16));

        // Activity History
        JPanel history = Theme.cardPanel();
        history.setLayout(new BorderLayout(0, 10));

        history.add(
                Theme.label("Activity History", 23,
                        Theme.DARK_BROWN, true),
                BorderLayout.NORTH
        );

        calendar = new JTextArea();
        calendar.setEditable(false);
        calendar.setLineWrap(true);
        calendar.setWrapStyleWord(true);
        calendar.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        calendar.setBackground(Theme.LIGHT);
        calendar.setForeground(Theme.BROWN);

        JScrollPane historyScroll = new JScrollPane(calendar);
        historyScroll.setPreferredSize(new Dimension(500, 180));
        historyScroll.setBorder(BorderFactory.createEmptyBorder());

        history.add(historyScroll, BorderLayout.CENTER);

        add(history);
        add(Box.createVerticalStrut(12));

        // Information
        JPanel info = Theme.cardPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        info.add(Theme.label("How to maintain your streak?",
                20, Theme.DARK_BROWN, true));

        JLabel rule = new JLabel(
                "<html>Complete at least one Pomodoro Session each day "
                + "to maintain your daily streak.<br>"
                + "Stay consistent and keep improving every day!</html>"
        );

        rule.setForeground(Theme.MUTED);
        info.add(rule);

        Theme.align(info, Component.LEFT_ALIGNMENT, false);

        add(info);

        Theme.align(this, Component.LEFT_ALIGNMENT, true);

        // Load data
        refreshData();
    }

    // Membuat card statistik
    private JPanel createStatCard(String value, String title) {

        JPanel card = Theme.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel valueLabel = Theme.label(value, 30,
                Theme.ROSE, true);

        JLabel titleLabel = Theme.label(title, 14,
                Theme.MUTED, false);

        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);

        Theme.align(card, Component.CENTER_ALIGNMENT, false);

        card.putClientProperty("valueLabel", valueLabel);

        return card;
    }

    // Memperbarui seluruh data streak
    public void refreshData() {

        int current = streakManager.getCurrentStreak();
        int longest = streakManager.getLongestStreak();
        int total = streakManager.getTotalActiveDays();

        streakLabel.setText(current + " Days");
        longestLabel.setText(String.valueOf(longest));
        totalLabel.setText(String.valueOf(total));

        if (current == 0) {
            message.setText("Start your first productive day!");
        } else if (current == 1) {
            message.setText("Great start! Keep it going!");
        } else {
            message.setText("Amazing! Keep your " + current
                    + " day streak alive!");
        }

        updateHistory();

        revalidate();
        repaint();
    }

    // Menampilkan riwayat tanggal aktivitas
    private void updateHistory() {

        Set<LocalDate> activeDays = streakManager.getActiveDays();

        if (activeDays.isEmpty()) {
            calendar.setText("Riwayat aktivitas akan tampil di sini.");
            return;
        }

        StringBuilder history = new StringBuilder();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy",
                        java.util.Locale.ENGLISH);

        for (LocalDate date : activeDays) {
            history.append("🔥  ")
                    .append(date.format(formatter))
                    .append("\n");
        }

        calendar.setText(history.toString());
        calendar.setCaretPosition(0);
    }
}

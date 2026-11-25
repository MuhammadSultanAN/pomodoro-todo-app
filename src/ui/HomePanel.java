package ui;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class HomePanel extends JPanel {
    public HomePanel(Consumer<String> navigate) {
        Theme.pageHeader(this, "Welcome to PomodoroTimer!", "Make today productive and achieve your goals.");

        JPanel streak = Theme.cardPanel();
        streak.setLayout(new FlowLayout(FlowLayout.LEFT, 18, 12));
        streak.add(Theme.label("🔥", 38, Theme.ROSE, true));
        streak.add(Theme.label("0 Days Streak", 27, Theme.DARK_BROWN, true));
        add(streak);
        add(Box.createVerticalStrut(18));

        JPanel stats = new JPanel(new GridLayout(1, 3, 15, 0));
        stats.setOpaque(false);
        Theme.addStat(stats, "0", "Total Tasks");
        Theme.addStat(stats, "0", "Completed Tasks");
        Theme.addStat(stats, "0", "Focus Sessions");
        add(stats);
        add(Box.createVerticalStrut(22));

        JButton focus = Theme.button("Start Focusing", Theme.SAGE, Color.WHITE);
        focus.addActionListener(e -> navigate.accept("timer"));
        add(focus);

        Theme.align(this, Component.LEFT_ALIGNMENT, true);
    } 
}

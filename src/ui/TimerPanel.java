package ui;
import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {
    public TimerPanel() {
        Theme.pageHeader(this, "Pomodoro Timer", "Stay focused and make every minute count.");

        JPanel timerCard = Theme.cardPanel();
        timerCard.setLayout(new BoxLayout(timerCard, BoxLayout.Y_AXIS));

        JPanel modes = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        modes.setOpaque(false);

        JLabel display = Theme.label("25:00", 82, Theme.BROWN, true);
        JLabel status = new JLabel("Time to focus!", SwingConstants.CENTER);
        status.setFont(Theme.sans(14, Font.PLAIN));
        status.setForeground(Theme.MUTED);

        String[][] modeData = {
            {"Pomodoro Timer", "25:00", "Time to focus!"},
            {"Short Break",    "05:00", "Take a short break!"},
            {"Long Break",     "15:00", "Enjoy your long break!"}
        };
        for (String[] m : modeData) {
            JButton b = Theme.button(m[0], Theme.PAPER, Theme.BROWN);
            b.addActionListener(e -> { display.setText(m[1]); status.setText(m[2]); });
            modes.add(b);
        }

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        controls.setOpaque(false);
        controls.add(Theme.button("Start", Theme.SAGE, Color.WHITE));
        controls.add(Theme.button("Pause", Theme.ROSE, Color.WHITE));
        controls.add(Theme.button("Reset", Theme.PAPER, Theme.BROWN));

        JLabel loop = new JLabel("Session 1 of 4", SwingConstants.CENTER);
        loop.setForeground(Theme.MUTED);
        loop.setFont(Theme.sans(13, Font.PLAIN));

        timerCard.add(modes);
        timerCard.add(Box.createVerticalStrut(25));
        timerCard.add(display);
        timerCard.add(Box.createVerticalStrut(8));
        timerCard.add(status);
        timerCard.add(Box.createVerticalStrut(22));
        timerCard.add(controls);
        timerCard.add(Box.createVerticalStrut(20));
        timerCard.add(new JSeparator());
        timerCard.add(Box.createVerticalStrut(14));
        timerCard.add(loop);
        Theme.align(timerCard, Component.CENTER_ALIGNMENT, true);
        add(timerCard);
        add(Box.createVerticalStrut(18));

        JPanel settings = Theme.cardPanel();
        settings.setLayout(new BorderLayout(0, 12));
        settings.add(Theme.label("Timer Settings", 25, Theme.DARK_BROWN, true), BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(2, 4, 12, 6));
        fields.setOpaque(false);
        settingField(fields, "Pomodoro (minutes)", "25");
        settingField(fields, "Short Break (minutes)", "5");
        settingField(fields, "Long Break (minutes)", "15");
        settingField(fields, "Number of Loops (sessions)", "4");
        settings.add(fields, BorderLayout.CENTER);
        settings.add(Theme.button("Save Settings", Theme.SAGE, Color.WHITE), BorderLayout.SOUTH);
        add(settings);

        Theme.align(this, Component.LEFT_ALIGNMENT, true);
    }

    private void settingField(JPanel parent, String caption, String value) {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(caption);
        l.setFont(Theme.sans(12, Font.BOLD));
        l.setForeground(Theme.BROWN);
        JTextField f = new JTextField(value);
        Theme.styleInput(f);
        box.add(l);
        box.add(Box.createVerticalStrut(6));
        box.add(f);
        Theme.align(box, Component.LEFT_ALIGNMENT, false);
        parent.add(box);
    }
}
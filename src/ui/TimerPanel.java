package ui;

import controller.TimerLogic;
import controller.TimerLogic.SessionType;
import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {
    private TimerLogic logic = new TimerLogic();
    private int sessionsBeforeLongBreak = 4;

    private JLabel display, status, loop;
    private JTextField workField, shortField, longField, loopField;
    private final StreakManager streakManager;

    public TimerPanel(StreakManager streakManager) {
        this.streakManager = streakManager;
        Theme.pageHeader(this, "Pomodoro Timer", "Stay focused and make every minute count.");

        JPanel timerCard = Theme.cardPanel();
        timerCard.setLayout(new BoxLayout(timerCard, BoxLayout.Y_AXIS));

        display = Theme.label("25:00", 82, Theme.BROWN, true);
        status = new JLabel("Time to focus!", SwingConstants.CENTER);
        status.setFont(Theme.sans(14, Font.PLAIN));
        status.setForeground(Theme.MUTED);

        // Tombol mode
        JPanel modes = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        modes.setOpaque(false);
        addModeButton(modes, "Pomodoro Timer", SessionType.WORK);
        addModeButton(modes, "Short Break", SessionType.SHORT_BREAK);
        addModeButton(modes, "Long Break", SessionType.LONG_BREAK);

        // Tombol kontrol
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        controls.setOpaque(false);
        JButton startBtn = Theme.button("Start", Theme.SAGE, Color.WHITE);
        JButton pauseBtn = Theme.button("Pause", Theme.ROSE, Color.WHITE);
        JButton resetBtn = Theme.button("Reset", Theme.PAPER, Theme.BROWN);
        startBtn.addActionListener(e -> logic.start());
        pauseBtn.addActionListener(e -> logic.pause());
        resetBtn.addActionListener(e -> logic.reset());
        controls.add(startBtn);
        controls.add(pauseBtn);
        controls.add(resetBtn);

        loop = new JLabel("Session 1 of 4", SwingConstants.CENTER);
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

        // Pengaturan
        JPanel settings = Theme.cardPanel();
        settings.setLayout(new BorderLayout(0, 12));
        settings.add(Theme.label("Timer Settings", 25, Theme.DARK_BROWN, true), BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(2, 4, 12, 6));
        fields.setOpaque(false);
        workField  = settingField(fields, "Pomodoro (minutes)", "25");
        shortField = settingField(fields, "Short Break (minutes)", "5");
        longField  = settingField(fields, "Long Break (minutes)", "15");
        loopField  = settingField(fields, "Number of Loops (sessions)", "4");
        settings.add(fields, BorderLayout.CENTER);
        JButton saveBtn = Theme.button("Save Settings", Theme.SAGE, Color.WHITE);
        saveBtn.addActionListener(e -> saveSettings());
        settings.add(saveBtn, BorderLayout.SOUTH);
        add(settings);

        Theme.align(this, Component.LEFT_ALIGNMENT, true);

        attachLogic(); // dipanggil setelah semua komponen dibuat
    }

    private void addModeButton(JPanel parent, String text, SessionType type) {
        JButton b = Theme.button(text, Theme.PAPER, Theme.BROWN);
        b.addActionListener(e -> logic.setSession(type));
        parent.add(b);
    }

    // Menghubungkan TimerLogic ke label di layar
    private void attachLogic() {
        logic.setListener(new TimerLogic.TimerListener() {
            @Override
            public void onTick(int remainingSeconds, SessionType type) {
                display.setText(logic.getFormattedTime());
                status.setText(statusText(type));
                int current = (logic.getCompletedWorkSessions() % sessionsBeforeLongBreak) + 1;
                loop.setText("Session " + current + " of " + sessionsBeforeLongBreak);
            }

            @Override
            public void onSessionCompleted(SessionType completed, SessionType next) {
                Toolkit.getDefaultToolkit().beep();
                if (completed == SessionType.WORK) {
                    streakManager.recordSession();
                }
            }
        });
    }

    private String statusText(SessionType type) {
        switch (type) {
            case SHORT_BREAK: return "Take a short break!";
            case LONG_BREAK:  return "Enjoy your long break!";
            default:          return "Time to focus!";
        }
    }

    private void saveSettings() {
        try {
            int w = Integer.parseInt(workField.getText().trim());
            int s = Integer.parseInt(shortField.getText().trim());
            int l = Integer.parseInt(longField.getText().trim());
            int n = Integer.parseInt(loopField.getText().trim());

            logic.pause();
            logic = new TimerLogic(w, s, l, n); // melempar exception jika <= 0
            sessionsBeforeLongBreak = n;
            attachLogic();
            JOptionPane.showMessageDialog(this, "Settings saved.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Isi semua kolom dengan angka bulat.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Semua angka harus lebih dari 0.");
        }
    }

    private JTextField settingField(JPanel parent, String caption, String value) {
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
        return f;
    }
}
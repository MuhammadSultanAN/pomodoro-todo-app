package ui;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class TaskPanel extends JPanel {
    private final JPanel formPanel;

    public TaskPanel() {
        Theme.pageHeader(this, "My Tasks", "Organize your daily activities.");

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JButton addBtn = Theme.button("+ Add Task", Theme.SAGE, Color.WHITE);
        addBtn.addActionListener(e -> toggleForm(true));
        top.add(addBtn, BorderLayout.EAST);
        add(top);
        add(Box.createVerticalStrut(15));

        JPanel sort = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        sort.setOpaque(false);
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setForeground(Theme.BROWN);
        sort.add(sortLabel);
        sort.add(new JComboBox<>(new String[]{"Nearest Deadline", "Farthest Deadline", "Recently Added"}));
        add(sort);
        add(Box.createVerticalStrut(12));

        formPanel = buildForm();
        formPanel.setVisible(false);
        add(formPanel);
        add(Box.createVerticalStrut(12));

        JPanel cards = new JPanel();
        cards.setOpaque(false);
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.add(sampleCard());
        JScrollPane scroll = new JScrollPane(cards);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setPreferredSize(new Dimension(800, 280));
        add(scroll);
        add(Box.createVerticalStrut(15));

        JPanel legend = Theme.cardPanel();
        legend.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));
        legend.add(Theme.label("Deadline Status:", 16, Theme.DARK_BROWN, true));
        legend.add(new JLabel("🟢 Safe"));
        legend.add(new JLabel("🟡 Approaching"));
        legend.add(new JLabel("🟠 Due Today"));
        legend.add(new JLabel("🔴 Overdue"));
        add(legend);

        Theme.align(this, Component.LEFT_ALIGNMENT, true);
    }

    private void toggleForm(boolean show) {
        formPanel.setVisible(show);
        formPanel.revalidate();
        formPanel.repaint();
    }

    private JPanel buildForm() {
        JPanel form = Theme.cardPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        form.add(Theme.label("Add New Task", 22, Theme.DARK_BROWN, true), g);
        g.gridwidth = 1; g.gridy++;
        form.add(new JLabel("Task Name"), g);
        JTextField name = new JTextField();
        Theme.styleInput(name);
        g.gridx = 1;
        form.add(name, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Description"), g);
        JTextArea desc = new JTextArea(3, 20);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setFont(Theme.sans(13, Font.PLAIN));
        desc.setBackground(Theme.LIGHT);
        desc.setBorder(new LineBorder(Theme.BORDER));
        g.gridx = 1;
        form.add(new JScrollPane(desc), g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Deadline (yyyy-MM-dd HH:mm)"), g);
        JTextField deadline = new JTextField();
        Theme.styleInput(deadline);
        g.gridx = 1;
        form.add(deadline, g);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton save = Theme.button("Save Task", Theme.SAGE, Color.WHITE);
        JButton cancel = Theme.button("Cancel", Theme.ROSE, Color.WHITE);
        save.addActionListener(e -> toggleForm(false));
        cancel.addActionListener(e -> toggleForm(false));
        actions.add(save);
        actions.add(cancel);
        g.gridx = 1; g.gridy++;
        form.add(actions, g);
        return form;
    }

    /** Kartu tugas contoh (statis) supaya tampilannya terlihat. */
    private JPanel sampleCard() {
        Color accent = Theme.SAGE;
        JPanel card = Theme.cardPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(new CompoundBorder(new MatteBorder(0, 5, 0, 0, accent),
                new CompoundBorder(new LineBorder(Theme.BORDER), new EmptyBorder(14, 16, 14, 16))));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel desc = new JLabel("Contoh deskripsi tugas");
        desc.setForeground(Theme.MUTED);
        JLabel due = new JLabel("Deadline: 01 Januari 2027, 12:00");
        due.setForeground(Theme.BROWN);
        JLabel stat = new JLabel("Safe");
        stat.setForeground(accent);
        text.add(Theme.label("Contoh Tugas", 21, Theme.DARK_BROWN, true));
        text.add(Box.createVerticalStrut(4));
        text.add(desc);
        text.add(Box.createVerticalStrut(4));
        text.add(due);
        text.add(stat);
        Theme.align(text, Component.LEFT_ALIGNMENT, false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actions.setOpaque(false);
        actions.add(Theme.button("Done", Theme.SAGE, Color.WHITE));
        actions.add(Theme.button("Edit", Theme.PAPER, Theme.BROWN));
        actions.add(Theme.button("Delete", Theme.ROSE, Color.WHITE));

        card.add(text, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        return card;
    }
}
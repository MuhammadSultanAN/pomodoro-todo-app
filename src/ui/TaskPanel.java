package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.util.Comparator;
import java.util.Collections;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TaskPanel extends JPanel {
    private static class Item {
    String name, description;
    LocalDateTime deadline;
    boolean done = false;
    Item(String name, String description, LocalDateTime deadline) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }
}

    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter SHOW_FMT = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");

    private final List<Item> items = new ArrayList<>();
    private JPanel formPanel, cardsPanel;
    private JTextField nameField, deadlineField;
    private JTextArea descriptionArea;
    private JComboBox<String> sortBox;
    private Item editingItem = null;   // null = mode tambah, isi = mode edit
    private JLabel formTitle;

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
        sortBox = new JComboBox<>(new String[] {
            "Nearest Deadline",
            "Farthest Deadline",
             "Recently Added"
            });
            
        sortBox.addActionListener(e -> render());
        sort.add(sortBox);
        add(sort);
        add(Box.createVerticalStrut(12));

        formPanel = buildForm();
        formPanel.setVisible(false);
        add(formPanel);
        add(Box.createVerticalStrut(12));

        cardsPanel = new JPanel();
        cardsPanel.setOpaque(false);
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setPreferredSize(new Dimension(800, 280));
        add(scroll);
        add(Box.createVerticalStrut(15));

        JPanel legend = Theme.cardPanel();
        legend.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));
        legend.add(Theme.label("Deadline Status:", 16, Theme.DARK_BROWN, true));
        legend.add(legendItem("Safe", new Color(76, 145, 95)));
        legend.add(legendItem("Approaching", new Color(198, 167, 93)));
        legend.add(legendItem("Due Today", new Color(193, 124, 83)));
        legend.add(legendItem("Overdue", new Color(173, 105, 105)));
        add(legend);

        Theme.align(this, Component.LEFT_ALIGNMENT, true);
        render();
    }

    private void toggleForm(boolean show) {
        formPanel.setVisible(show);
        formPanel.revalidate();
        formPanel.repaint();
    }

    private void closeForm() {
    editingItem = null;
    formTitle.setText("Add New Task");
    nameField.setText("");
    descriptionArea.setText("");
    deadlineField.setText("");
    toggleForm(false);
    }

    private JPanel buildForm() {
        JPanel form = Theme.cardPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        formTitle = Theme.label("Add New Task", 22, Theme.DARK_BROWN, true);
        form.add(formTitle, g);
        g.gridwidth = 1; g.gridy++;
        form.add(new JLabel("Task Name"), g);
        nameField = new JTextField();
        Theme.styleInput(nameField);
        g.gridx = 1;
        form.add(nameField, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Description"), g);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(Theme.sans(13, Font.PLAIN));
        descriptionArea.setBackground(Theme.LIGHT);
        descriptionArea.setBorder(new LineBorder(Theme.BORDER));
        g.gridx = 1;
        form.add(new JScrollPane(descriptionArea), g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Deadline (yyyy-MM-dd HH:mm)"), g);
        deadlineField = new JTextField();
        Theme.styleInput(deadlineField);
        g.gridx = 1;
        form.add(deadlineField, g);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton save = Theme.button("Save Task", Theme.SAGE, Color.WHITE);
        JButton cancel = Theme.button("Cancel", Theme.ROSE, Color.WHITE);
        save.addActionListener(e -> saveTask());
        cancel.addActionListener(e -> closeForm());
        actions.add(save);
        actions.add(cancel);
        g.gridx = 1; g.gridy++;
        form.add(actions, g);
        return form;
    }

    private void saveTask() {
        String name = nameField.getText().trim();
        String deadlineText = deadlineField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tugas tidak boleh kosong.");
            return;
        }
        if (deadlineText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Deadline harus diisi.");
            return;
        }

        LocalDateTime deadline;
        try {
            deadline = LocalDateTime.parse(deadlineText, INPUT_FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format deadline harus yyyy-MM-dd HH:mm\nContoh: 2026-12-31 18:00");
            return;
        }
        if (editingItem != null) {
            editingItem.name = name;
            editingItem.description = descriptionArea.getText().trim();
            editingItem.deadline = deadline;
        } else {
            items.add(new Item(name, descriptionArea.getText().trim(), deadline));
        }
        closeForm();
        render();
    }

    private void render() {
        cardsPanel.removeAll();
        List<Item> sortedItems = new ArrayList<>(items);

        String selectedSort = (String) sortBox.getSelectedItem();

        if ("Nearest Deadline".equals(selectedSort)) {
            sortedItems.sort(Comparator.comparing(t -> t.deadline));
        } else if ("Farthest Deadline".equals(selectedSort)) {
        sortedItems.sort(Comparator.comparing((Item t) -> t.deadline).reversed());
        } else if ("Recently Added".equals(selectedSort)) {
            Collections.reverse(sortedItems);
        }
        
        if (sortedItems.isEmpty()) {
        JPanel empty = Theme.cardPanel();
        empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));

        JLabel a = new JLabel("No tasks yet.");
        a.setForeground(Theme.MUTED);

        JLabel b = new JLabel("Add a task to get started!");
        b.setForeground(Theme.MUTED);

        empty.add(a);
        empty.add(b);

        Theme.align(empty, Component.LEFT_ALIGNMENT, false);
        empty.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            empty.getPreferredSize().height
        ));

        cardsPanel.add(empty);
       }
       
       for (Item t : sortedItems) {
        cardsPanel.add(buildCard(t));
        cardsPanel.add(Box.createVerticalStrut(10));
       }
       
       cardsPanel.revalidate();
       cardsPanel.repaint();
    }

    private JPanel buildCard(Item t) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), t.deadline.toLocalDate());
        boolean lewat = t.deadline.isBefore(LocalDateTime.now());

        Color accent;
        String status;
        if (t.done)            { accent = Color.GRAY;               status = "Completed"; }
        else if (lewat || days < 0) { accent = new Color(173, 105, 105); status = "Overdue"; }
        else if (days == 0)    { accent = new Color(193, 124, 83);  status = "Due Today"; }
        else if (days <= 3)    { accent = new Color(198, 167, 93);  status = "Approaching Deadline"; }
        else                   { accent = Theme.SAGE;               status = "Safe"; }

        JPanel card = Theme.cardPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(new CompoundBorder(new MatteBorder(0, 5, 0, 0, accent),
                new CompoundBorder(new LineBorder(Theme.BORDER), new EmptyBorder(14, 16, 14, 16))));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel desc = new JLabel(t.description.isEmpty() ? "No description" : t.description);
        desc.setForeground(Theme.MUTED);
        JLabel due = new JLabel("Deadline: " + t.deadline.format(SHOW_FMT));
        due.setForeground(Theme.BROWN);
        JLabel stat = new JLabel(status);
        stat.setForeground(accent);
        text.add(Theme.label(t.name, 21, Theme.DARK_BROWN, true));
        text.add(Box.createVerticalStrut(4));
        text.add(desc);
        text.add(Box.createVerticalStrut(4));
        text.add(due);
        text.add(stat);
        Theme.align(text, Component.LEFT_ALIGNMENT, false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actions.setOpaque(false);
        JButton doneBtn = Theme.button(t.done ? "Undo" : "Done", Theme.SAGE, Color.WHITE);
        JButton editBtn = Theme.button("Edit", Theme.PAPER, Theme.BROWN);
        JButton deleteBtn = Theme.button("Delete", Theme.ROSE, Color.WHITE);

        doneBtn.addActionListener(e -> {
            t.done = !t.done;
            render();
        });

        editBtn.addActionListener(e -> {
            editingItem = t;
            nameField.setText(t.name);
            descriptionArea.setText(t.description);
            deadlineField.setText(t.deadline.format(INPUT_FMT));
            formTitle.setText("Edit Task");
            toggleForm(true);
        });

        deleteBtn.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Hapus tugas ini?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                items.remove(t);
                render();
            }
        });

        actions.add(doneBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);

        card.add(text, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        return card;
    }

    private JLabel legendItem(String text, Color color) {
    JLabel label = new JLabel(text);
    label.setIcon(new DotIcon(color));
    label.setIconTextGap(6);
    return label;
    }
    
    private static class DotIcon implements Icon {
        private final Color color;
        
        public DotIcon(Color color) {
            this.color = color;
        } 
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillOval(x, y + 3, 10, 10);
        }
        
        @Override
        public int getIconWidth() {
            return 10;
        }
        
        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}

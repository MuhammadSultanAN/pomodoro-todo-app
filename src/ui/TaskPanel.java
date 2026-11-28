package ui;

import controller.TaskManager;
import model.Task;

import javax.swing.*;
import javax.swing.border.*;
import java.util.Collections;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TaskPanel extends JPanel {

    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter SHOW_FMT = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");

    // Semua data tugas disimpan dan dibaca lewat TaskManager (otomatis ke data/tasks.dat)
    private final TaskManager taskManager;

    private JPanel formPanel, cardsPanel;
    private JTextField nameField, deadlineField;
    private JTextArea descriptionArea;
    private JComboBox<String> sortBox;
    private Task editingTask = null;   // null = mode tambah, isi = mode edit
    private JLabel formTitle;

    public TaskPanel(TaskManager taskManager) {
        this.taskManager = taskManager;
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
        scroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        scroll.setPreferredSize(new Dimension(800, 450));
        scroll.setMinimumSize(new Dimension(0, 200));

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

        Component scrollComponent = getComponent(6);
        scrollComponent.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)
        );

        render();
    }

    private void toggleForm(boolean show) {
        formPanel.setVisible(show);
        formPanel.revalidate();
        formPanel.repaint();
    }

    private void closeForm() {
    editingTask = null;
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

        try {
            if (editingTask != null) {
                String id = editingTask.getId();
                taskManager.renameTask(id, name);
                taskManager.changeDescription(id, descriptionArea.getText());
                taskManager.changeDeadline(id, deadline);
            } else {
                taskManager.addTask(name, descriptionArea.getText(), deadline);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        closeForm();
        render();
    }

    private void render() {
        cardsPanel.removeAll();

        List<Task> sortedTasks;
        String selectedSort = (String) sortBox.getSelectedItem();

        if ("Nearest Deadline".equals(selectedSort)) {
            sortedTasks = taskManager.getTasksSortedByDeadline(true);
        } else if ("Farthest Deadline".equals(selectedSort)) {
            sortedTasks = taskManager.getTasksSortedByDeadline(false);
        } else {
            // "Recently Added": yang terbaru di atas
            sortedTasks = taskManager.getAllTasks();
            Collections.reverse(sortedTasks);
        }
        
        if (sortedTasks.isEmpty()) {
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
       
       for (Task t : sortedTasks) {
        cardsPanel.add(buildCard(t));
        cardsPanel.add(Box.createVerticalStrut(10));
       }
       
       cardsPanel.revalidate();
       cardsPanel.repaint();
    }

    private JPanel buildCard(Task t) {
        LocalDateTime deadline = t.getDeadline();   // bisa null (tugas tanpa deadline)

        Color accent;
        String status;
        if (t.getIsDone()) {
            accent = Color.GRAY;
            status = "Completed";
        } else if (deadline == null) {
            accent = Theme.MUTED;
            status = "No Deadline";
        } else {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), deadline.toLocalDate());
            boolean lewat = deadline.isBefore(LocalDateTime.now());
            if (lewat || days < 0)  { accent = new Color(173, 105, 105); status = "Overdue"; }
            else if (days == 0)     { accent = new Color(193, 124, 83);  status = "Due Today"; }
            else if (days <= 3)     { accent = new Color(198, 167, 93);  status = "Approaching Deadline"; }
            else                    { accent = Theme.SAGE;               status = "Safe"; }
        }

        JPanel card = Theme.cardPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(new CompoundBorder(new MatteBorder(0, 5, 0, 0, accent),
                new CompoundBorder(new LineBorder(Theme.BORDER), new EmptyBorder(14, 16, 14, 16))));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel desc = new JLabel(t.getDescription().isEmpty() ? "No description" : t.getDescription());
        desc.setForeground(Theme.MUTED);
        JLabel due = new JLabel(deadline == null ? "No deadline" : "Deadline: " + deadline.format(SHOW_FMT));
        due.setForeground(Theme.BROWN);
        JLabel stat = new JLabel(status);
        stat.setForeground(accent);
        text.add(Theme.label(t.getTitle(), 21, Theme.DARK_BROWN, true));
        text.add(Box.createVerticalStrut(4));
        text.add(desc);
        text.add(Box.createVerticalStrut(4));
        text.add(due);
        text.add(stat);
        Theme.align(text, Component.LEFT_ALIGNMENT, false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actions.setOpaque(false);
        JButton doneBtn = Theme.button(t.getIsDone() ? "Undo" : "Done", Theme.SAGE, Color.WHITE);
        JButton editBtn = Theme.button("Edit", Theme.PAPER, Theme.BROWN);
        JButton deleteBtn = Theme.button("Delete", Theme.ROSE, Color.WHITE);

        doneBtn.addActionListener(e -> {
            taskManager.setTaskDone(t.getId(), !t.getIsDone());
            render();
        });

        editBtn.addActionListener(e -> {
            editingTask = t;
            nameField.setText(t.getTitle());
            descriptionArea.setText(t.getDescription());
            deadlineField.setText(deadline == null ? "" : deadline.format(INPUT_FMT));
            formTitle.setText("Edit Task");
            toggleForm(true);
        });

        deleteBtn.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Hapus tugas ini?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                taskManager.removeTask(t.getId());
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

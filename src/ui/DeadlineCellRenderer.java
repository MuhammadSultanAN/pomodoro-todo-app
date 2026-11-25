package ui;

import controller.TaskManager;
import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class DeadlineCellRenderer extends DefaultTableCellRenderer {
    private final TaskManager taskManager;

    public DeadlineCellRenderer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        // Jaga-jaga kalau tabel di-sort lewat header
        int modelRow = table.convertRowIndexToModel(row);

        if (isSelected) {
            return c; // pakai warna seleksi bawaan
        }

        if (modelRow < taskManager.getAllTasks().size()) {
            Task task = taskManager.getAllTasks().get(modelRow);
            long jamTersisa = Duration.between(LocalDateTime.now(), task.getDeadline()).toHours();

            if (jamTersisa < 0) {
                c.setBackground(new Color(255, 160, 160));      // lewat deadline: merah
            } else if (jamTersisa <= 24) {
                c.setBackground(new Color(255, 200, 120));      // < 24 jam: oranye
            } else if (jamTersisa <= 72) {
                c.setBackground(new Color(255, 245, 157));      // < 3 hari: kuning
            } else {
                c.setBackground(Color.WHITE);
            }
            c.setForeground(Color.BLACK);
        }
        return c;
    }
}
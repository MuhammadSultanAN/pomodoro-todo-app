package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import model.Task;
import storage.FileStorage;

public class TaskManager {
  private ArrayList<Task> taskList;
  private final FileStorage storage;

  // Otomatis memuat data dari data/tasks.dat
  public TaskManager() {
    this(new FileStorage());
  }

  public TaskManager(FileStorage storage) {
    this.storage = storage;
    this.taskList = storage.load();
  }

  // ---------- READ ----------

  // Mengembalikan salinan, jadi mengubah hasilnya tidak merusak daftar asli.
  public ArrayList<Task> getAllTasks() {
    return new ArrayList<Task>(taskList);
  }

  public Task findTask(String id) {
    for (Task task : taskList) {
      if (task.getId().equals(id)) {
        return task;
      }
    }
    return null;
  }

  public int getTotalTasks() {
    return taskList.size();
  }

  public int getCompletedCount() {
    int count = 0;
    for (Task task : taskList) {
      if (task.getIsDone()) {
        count++;
      }
    }
    return count;
  }

  // Salinan terurut, urutan asli (urutan ditambahkan) tidak berubah.
  // Tugas tanpa deadline selalu di paling bawah.
  public ArrayList<Task> getTasksSortedByDeadline(boolean nearestFirst) {
    ArrayList<Task> sorted = new ArrayList<Task>(taskList);
    sorted.sort(Comparator.comparing(Task::getDeadline,
        Comparator.nullsLast(nearestFirst
            ? Comparator.<LocalDateTime>naturalOrder()
            : Comparator.<LocalDateTime>reverseOrder())));
    return sorted;
  }

  // ---------- CREATE ----------

  public Task addTask(String title, LocalDateTime deadline) {
    return addTask(title, "", deadline);
  }

  // Judul wajib diisi. Deadline boleh null (tugas tanpa deadline).
  public Task addTask(String title, String description, LocalDateTime deadline) {
    validateTitle(title);
    String desc = description == null ? "" : description.trim();
    Task task = new Task(title.trim(), desc, deadline);
    taskList.add(task);
    save();
    return task;
  }

  // ---------- UPDATE ----------
  // Semua method update mengembalikan true kalau berhasil,
  // false kalau tugas dengan id tersebut tidak ditemukan.

  public boolean taskDone(String id) {
    return setTaskDone(id, true);
  }

  // Dipakai checkbox/tombol: bisa menandai selesai maupun membatalkannya.
  public boolean setTaskDone(String id, boolean isDone) {
    Task targetTask = findTask(id);
    if (targetTask == null) {
      return false;
    }
    targetTask.setIsDone(isDone);
    save();
    return true;
  }

  public boolean renameTask(String id, String title) {
    validateTitle(title);
    Task targetTask = findTask(id);
    if (targetTask == null) {
      return false;
    }
    targetTask.setTitle(title.trim());
    save();
    return true;
  }

  public boolean changeDescription(String id, String description) {
    Task targetTask = findTask(id);
    if (targetTask == null) {
      return false;
    }
    targetTask.setDescription(description == null ? "" : description.trim());
    save();
    return true;
  }

  // deadline = null artinya menghapus deadline.
  public boolean changeDeadline(String id, LocalDateTime deadline) {
    Task targetTask = findTask(id);
    if (targetTask == null) {
      return false;
    }
    targetTask.setDeadline(deadline);
    save();
    return true;
  }

  // ---------- DELETE ----------

  public boolean removeTask(String id) {
    Task targetTask = findTask(id);
    if (targetTask == null) {
      return false;
    }
    taskList.remove(targetTask);
    save();
    return true;
  }

  // ---------- SORT (versi lama, mengubah urutan daftar asli) ----------

  // Mengurutkan tugas dari deadline paling cepat (Insertion Sort).
  // Sudah aman untuk tugas tanpa deadline (ditaruh paling bawah).
  public void sortByDeadline() {
    for (int i = 1; i < taskList.size(); i++) {
      Task key = taskList.get(i);
      int j = i - 1;

      while (j >= 0 && isLater(taskList.get(j), key)) {
        taskList.set(j + 1, taskList.get(j));
        j--;
      }
      taskList.set(j + 1, key);
    }
    save();
  }

  // true kalau a harus berada setelah b
  private boolean isLater(Task a, Task b) {
    if (a.getDeadline() == null) {
      return b.getDeadline() != null;
    }
    if (b.getDeadline() == null) {
      return false;
    }
    return a.getDeadline().isAfter(b.getDeadline());
  }

  // ---------- Helper ----------

  private void validateTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("Judul tugas tidak boleh kosong");
    }
  }

  private void save() {
    try {
      storage.save(taskList);
    } catch (IOException e) {
      System.err.println("Gagal menyimpan tugas: " + e.getMessage());
    }
  }
}

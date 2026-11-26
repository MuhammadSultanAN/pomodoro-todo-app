package storage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import model.Task;

// Menyimpan dan membaca data dari file di folder data/ (serialisasi Java).
public class FileStorage {
  private final Path filePath;

  public FileStorage() {
    this("data/tasks.dat");
  }

  public FileStorage(String path) {
    this.filePath = Paths.get(path);
  }

  // ---------- Daftar tugas ----------

  public void save(ArrayList<Task> tasks) throws IOException {
    writeObject(filePath, tasks);
  }

  // File belum ada atau rusak -> daftar kosong.
  @SuppressWarnings("unchecked")
  public ArrayList<Task> load() {
    Object data = readObject(filePath);
    if (data instanceof ArrayList) {
      return (ArrayList<Task>) data;
    }
    return new ArrayList<Task>();
  }

  // ---------- Umum (dipakai juga oleh StreakManager) ----------

  // Simpan ke file sementara dulu, baru diganti ke file asli.
  // Jadi kalau program mati saat menyimpan, file lama tidak rusak.
  public static void writeObject(Path path, Serializable data) throws IOException {
    Path parent = path.toAbsolutePath().getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    Path temp = Paths.get(path.toString() + ".tmp");
    try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temp))) {
      out.writeObject(data);
    }
    Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
  }

  // Kembalikan null kalau file belum ada.
  // Kalau file rusak: dipindah jadi .rusak (cadangan) dan null dikembalikan.
  public static Object readObject(Path path) {
    if (!Files.exists(path)) {
      return null;
    }
    try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
      return in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Gagal membaca " + path + ": " + e);
      try {
        Files.move(path, Paths.get(path.toString() + ".rusak"),
            StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException ex) {
        System.err.println("Gagal mencadangkan file rusak: " + ex.getMessage());
      }
      return null;
    }
  }
}

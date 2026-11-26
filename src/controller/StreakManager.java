package controller;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeSet;
import storage.FileStorage;

// Mengelola streak harian: hari dianggap aktif kalau minimal satu sesi fokus selesai.
// Yang disimpan hanya daftar tanggal aktif dan jumlah sesi fokus.
// Streak saat ini, streak terpanjang, dan total hari aktif dihitung dari daftar itu,
// jadi angkanya tidak bisa tidak sinkron.
public class StreakManager {

  private static class Data implements Serializable {
    private static final long serialVersionUID = 1L;
    TreeSet<LocalDate> activeDates = new TreeSet<LocalDate>();
    int focusSessions = 0;
  }

  private final Path filePath;
  private Data data;

  public StreakManager() {
    this("data/streak.dat");
  }

  public StreakManager(String path) {
    this.filePath = Paths.get(path);
    Object loaded = FileStorage.readObject(filePath);
    this.data = (loaded instanceof Data) ? (Data) loaded : new Data();
  }

  // ---------- Mencatat aktivitas ----------

  // Panggil saat satu sesi fokus (WORK) selesai, misalnya dari
  // TimerListener.onSessionCompleted. Jangan dipanggil kalau sesi hanya di-skip.
  public void recordFocusSession() {
    recordFocusSession(LocalDate.now());
  }

  public void recordFocusSession(LocalDate day) {
    data.activeDates.add(day);
    data.focusSessions++;
    save();
  }

  // ---------- Statistik (untuk StreakPanel dan HomePanel) ----------

  public int getCurrentStreak() {
    return getCurrentStreak(LocalDate.now());
  }

  // Hari ini belum aktif tidak memutus streak, selama kemarin aktif.
  public int getCurrentStreak(LocalDate today) {
    LocalDate day = data.activeDates.contains(today) ? today : today.minusDays(1);
    int streak = 0;
    while (data.activeDates.contains(day)) {
      streak++;
      day = day.minusDays(1);
    }
    return streak;
  }

  public int getLongestStreak() {
    int longest = 0;
    int run = 0;
    LocalDate previous = null;
    for (LocalDate day : data.activeDates) {
      run = (previous != null && previous.plusDays(1).equals(day)) ? run + 1 : 1;
      longest = Math.max(longest, run);
      previous = day;
    }
    return longest;
  }

  public int getTotalActiveDays() {
    return data.activeDates.size();
  }

  public int getTotalFocusSessions() {
    return data.focusSessions;
  }

  public boolean isActiveOn(LocalDate day) {
    return data.activeDates.contains(day);
  }

  // Tanggal aktif, urut dari yang paling lama (untuk Activity History)
  public ArrayList<LocalDate> getActiveDates() {
    return new ArrayList<LocalDate>(data.activeDates);
  }

  private void save() {
    try {
      FileStorage.writeObject(filePath, data);
    } catch (IOException e) {
      System.err.println("Gagal menyimpan streak: " + e.getMessage());
    }
  }
}

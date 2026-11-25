package controller;

import javax.swing.Timer;

/**
 * Mengatur logika hitung mundur Pomodoro.
 * Kelas ini tidak mengatur tampilan GUI.
 *
 * Catatan: gunakan dari Event Dispatch Thread (EDT), karena
 * javax.swing.Timer memanggil tick() di EDT. Jangan lakukan
 * pekerjaan berat di dalam TimerListener.
 */
public class TimerLogic {
 
    public enum SessionType {
        WORK,
        SHORT_BREAK,
        LONG_BREAK
    }
 
    public interface TimerListener {
        // Dipanggil setiap detik dan setiap kali sesi berganti
        void onTick(int remainingSeconds, SessionType sessionType);
 
        // Dipanggil saat sesi selesai (alami atau lewat skip)
        void onSessionCompleted(
            SessionType completedSession,
            SessionType nextSession
        );
    }
 
    private final int workDuration;
    private final int shortBreakDuration;
    private final int longBreakDuration;
    private final int sessionsBeforeLongBreak;
 
    private final Timer swingTimer;
    private TimerListener listener;
 
    private SessionType currentSession;
    private int remainingSeconds;
    private int completedWorkSessions;
    private boolean autoStartNextSession;
 
    // Constructor default: 25 / 5 / 15 menit, long break setiap 4 sesi
    public TimerLogic() {
        this(25, 5, 15, 4);
    }
 
    // Constructor dengan durasi yang bisa diatur
    public TimerLogic(
        int workMinutes,
        int shortBreakMinutes,
        int longBreakMinutes,
        int sessionsBeforeLongBreak
    ) {
        if (workMinutes <= 0 || shortBreakMinutes <= 0
                || longBreakMinutes <= 0
                || sessionsBeforeLongBreak <= 0) {
            throw new IllegalArgumentException(
                "Durasi dan jumlah sesi harus lebih dari 0."
            );
        }
 
        this.workDuration = workMinutes * 60;
        this.shortBreakDuration = shortBreakMinutes * 60;
        this.longBreakDuration = longBreakMinutes * 60;
        this.sessionsBeforeLongBreak = sessionsBeforeLongBreak;
 
        this.currentSession = SessionType.WORK;
        this.remainingSeconds = this.workDuration;
        this.completedWorkSessions = 0;
        this.autoStartNextSession = false;
 
        // Timer berjalan setiap 1 detik
        this.swingTimer = new Timer(1000, event -> tick());
        this.swingTimer.setRepeats(true);
    }
 
    // ===== Kontrol timer =====
 
    // Menghubungkan timer dengan GUI
    public void setListener(TimerListener listener) {
        this.listener = listener;
        notifyTick();
    }
 
    // Memulai timer
    public void start() {
        if (!swingTimer.isRunning()) {
            swingTimer.start();
        }
    }
 
    // Menghentikan sementara timer
    public void pause() {
        swingTimer.stop();
    }
 
    // Melanjutkan timer
    public void resume() {
        start();
    }
 
    // Mengembalikan timer ke kondisi awal (sesi WORK, hitungan sesi 0)
    public void reset() {
        swingTimer.stop();
 
        currentSession = SessionType.WORK;
        remainingSeconds = workDuration;
        completedWorkSessions = 0;
 
        notifyTick();
    }
 
    // Mengulang sesi yang sedang berjalan tanpa mengubah jenis sesi
    // maupun hitungan sesi fokus
    public void restartCurrentSession() {
        swingTimer.stop();
        remainingSeconds = getDurationInSeconds(currentSession);
        notifyTick();
    }
 
    // Melompat ke sesi berikutnya tanpa menunggu waktu habis.
    // Melewati sesi WORK tidak dihitung sebagai sesi fokus selesai.
    public void skip() {
        boolean wasRunning = swingTimer.isRunning();
 
        moveToNextSession(false);
 
        if (wasRunning) {
            swingTimer.start();
        }
    }
 
    // Mengatur apakah sesi berikutnya otomatis dimulai
    public void setAutoStartNextSession(boolean autoStart) {
        this.autoStartNextSession = autoStart;
    }
 
    // ===== Getter =====
 
    // Mengecek apakah timer sedang berjalan
    public boolean isRunning() {
        return swingTimer.isRunning();
    }
 
    // Mengambil jenis sesi saat ini
    public SessionType getCurrentSession() {
        return currentSession;
    }
 
    // Mengambil sisa waktu dalam detik
    public int getRemainingSeconds() {
        return remainingSeconds;
    }
 
    // Mengambil durasi total sesi saat ini (untuk progress bar/lingkaran)
    public int getTotalSeconds() {
        return getDurationInSeconds(currentSession);
    }
 
    // Mengambil jumlah sesi fokus yang selesai
    public int getCompletedWorkSessions() {
        return completedWorkSessions;
    }
 
    // Mengubah detik menjadi format MM:SS
    public String getFormattedTime() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
 
        return String.format("%02d:%02d", minutes, seconds);
    }
 
    // ===== Logika internal =====
 
    // Proses hitung mundur
    private void tick() {
        remainingSeconds--;
        notifyTick();
 
        if (remainingSeconds <= 0) {
            moveToNextSession(true);
 
            // Opsional: langsung memulai sesi berikutnya
            if (autoStartNextSession) {
                swingTimer.start();
            }
        }
    }
 
    // Mengatur perpindahan antar sesi.
    // countAsCompleted = true jika sesi selesai secara alami,
    // false jika dilewati lewat skip().
    private void moveToNextSession(boolean countAsCompleted) {
        SessionType completedSession = currentSession;
 
        swingTimer.stop();
 
        if (currentSession == SessionType.WORK) {
            boolean longBreakDue = false;
 
            if (countAsCompleted) {
                completedWorkSessions++;
                longBreakDue =
                    completedWorkSessions % sessionsBeforeLongBreak == 0;
            }
 
            currentSession = longBreakDue
                ? SessionType.LONG_BREAK
                : SessionType.SHORT_BREAK;
        } else {
            currentSession = SessionType.WORK;
        }
 
        remainingSeconds = getDurationInSeconds(currentSession);
 
        if (listener != null) {
            listener.onSessionCompleted(completedSession, currentSession);
        }
 
        notifyTick();
    }
 
    // Mengambil durasi berdasarkan jenis sesi
    private int getDurationInSeconds(SessionType sessionType) {
        switch (sessionType) {
            case WORK:
                return workDuration;
 
            case SHORT_BREAK:
                return shortBreakDuration;
 
            case LONG_BREAK:
                return longBreakDuration;
 
            default:
                throw new IllegalStateException(
                    "Jenis sesi tidak dikenal."
                );
        }
    }
 
    // Mengirim pembaruan waktu ke GUI
    private void notifyTick() {
        if (listener != null) {
            listener.onTick(remainingSeconds, currentSession);
        }
    }
}
 

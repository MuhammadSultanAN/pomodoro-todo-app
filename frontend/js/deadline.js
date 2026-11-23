// Pemilik: Integrasi.
// Aturan lengkap ada di docs/data-format.md (bagian 3 dan 4).
// File ini dipakai oleh tasks.js. Selama belum diisi, semua tugas tampil normal.

const DeadlineUtils = {
  /**
   * Kembalikan status deadline sebuah tugas:
   * 'done' | 'overdue' | 'urgent' | 'soon' | 'normal'
   *
   * Aturan:
   * - task.done === true                  -> 'done'
   * - deadline null                       -> 'normal'
   * - sudah lewat & belum selesai         -> 'overdue'
   * - kurang dari 24 jam                  -> 'urgent'
   * - kurang dari atau sama dengan 2 hari -> 'soon'
   * - selain itu                          -> 'normal'
   */
  getStatus(task) {
    // TODO (Integrasi): hitung selisih new Date(task.deadline) - Date.now()
    return 'normal';
  },

  /**
   * Kembalikan ARRAY BARU yang sudah diurutkan (jangan ubah array asli):
   * 1. belum selesai di atas, selesai di bawah
   * 2. deadline terdekat paling atas
   * 3. tanpa deadline (null) paling bawah di grupnya
   */
  sortTasks(tasks) {
    // TODO (Integrasi): pakai [...tasks].sort(...)
    return [...tasks];
  },
};

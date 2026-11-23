// Pemilik: Integrasi. Menghubungkan timer, daftar tugas, dan elemen HTML.

// ----- Timer -> tampilan -----
const displayEl = document.getElementById('timer-display');
const phaseEl = document.getElementById('timer-phase');

function pad(n) {
  return String(n).padStart(2, '0');
}

Pomodoro.onTick((s) => {
  displayEl.textContent = `${pad(Math.floor(s.remaining / 60))}:${pad(s.remaining % 60)}`;
  const phaseName = s.phase === 'work' ? 'Fokus' : 'Istirahat';
  phaseEl.textContent = `${phaseName}, putaran ${s.currentLoop} dari ${s.loops}`;
});

// ----- Tombol dan pengaturan timer -----
function applySettings() {
  Pomodoro.configure({
    work: Number(document.getElementById('set-work').value),
    break: Number(document.getElementById('set-break').value),
    loops: Number(document.getElementById('set-loop').value),
  });
}

document.getElementById('btn-start').addEventListener('click', Pomodoro.start);
document.getElementById('btn-pause').addEventListener('click', Pomodoro.pause);
document.getElementById('btn-reset').addEventListener('click', applySettings);
['set-work', 'set-break', 'set-loop'].forEach((id) =>
  document.getElementById(id).addEventListener('change', applySettings)
);

// ----- Daftar tugas -----
Tasks.load();
// Gambar ulang tiap menit supaya warna deadline ikut berubah.
setInterval(Tasks.render, 60 * 1000);

// ----- Contoh pemakaian event timer (untuk gamifikasi nanti) -----
document.addEventListener('pomodoro:work-complete', () => {
  console.log('Satu sesi fokus selesai');
});

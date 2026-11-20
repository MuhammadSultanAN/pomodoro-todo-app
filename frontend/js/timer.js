// Pemilik: Timer.
// File ini hanya berisi logika, TIDAK menyentuh elemen HTML.
// Tampilan diatur lewat callback onTick (dipasang di main.js).
//
// Event yang dikirim ke document (untuk gamifikasi nanti):
//   'pomodoro:work-complete'   satu sesi kerja selesai
//   'pomodoro:break-complete'  satu sesi istirahat selesai
//   'pomodoro:all-complete'    semua putaran selesai

const Pomodoro = (() => {
  const state = {
    workMin: 25,
    breakMin: 5,
    loops: 2,
    currentLoop: 1,
    phase: 'work', // 'work' | 'break'
    remaining: 25 * 60, // detik
    running: false,
  };
  let intervalId = null;
  let tickCallback = () => {};

  function emit(name) {
    document.dispatchEvent(new CustomEvent(name, { detail: { ...state } }));
  }

  function notify() {
    tickCallback({ ...state });
  }

  function phaseSeconds() {
    return (state.phase === 'work' ? state.workMin : state.breakMin) * 60;
  }

  // Dipanggil tiap detik. Tangani pergantian work -> break -> putaran berikutnya.
  function tick() {
    state.remaining -= 1;

    if (state.remaining <= 0) {
      if (state.phase === 'work') {
        emit('pomodoro:work-complete');
        state.phase = 'break';
      } else {
        emit('pomodoro:break-complete');
        if (state.currentLoop >= state.loops) {
          pause();
          state.currentLoop = 1;
          state.phase = 'work';
          state.remaining = phaseSeconds();
          emit('pomodoro:all-complete');
          notify();
          return;
        }
        state.currentLoop += 1;
        state.phase = 'work';
      }
      state.remaining = phaseSeconds();
    }
    notify();
  }

  function start() {
    if (state.running) return;
    state.running = true;
    intervalId = setInterval(tick, 1000);
    notify();
  }

  function pause() {
    state.running = false;
    clearInterval(intervalId);
    intervalId = null;
    notify();
  }

  function reset() {
    pause();
    state.currentLoop = 1;
    state.phase = 'work';
    state.remaining = phaseSeconds();
    notify();
  }

  // Ubah pengaturan (menit dan jumlah putaran), lalu atur ulang timer.
  function configure({ work, break: breakMin, loops }) {
    if (work > 0) state.workMin = work;
    if (breakMin > 0) state.breakMin = breakMin;
    if (loops > 0) state.loops = loops;
    reset();
  }

  function onTick(callback) {
    tickCallback = callback;
    notify();
  }

  return { start, pause, reset, configure, onTick };
})();

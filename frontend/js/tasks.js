// Pemilik: bersama (Backend menyiapkan API, Integrasi mengatur tampilan daftar).
// Mengambil data dari API, menampilkan daftar, tambah, centang selesai, dan hapus.

const Tasks = (() => {
  const listEl = document.getElementById('task-list');
  const countEl = document.getElementById('task-count');
  const errorEl = document.getElementById('task-error');
  const formEl = document.getElementById('task-form');
  const titleEl = document.getElementById('task-title');
  const deadlineEl = document.getElementById('task-deadline');

  let tasks = [];

  async function request(url, options = {}) {
    const res = await fetch(url, {
      headers: { 'Content-Type': 'application/json' },
      ...options,
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.error || 'Terjadi kesalahan pada server');
    return data;
  }

  function showError(message) {
    errorEl.textContent = message || '';
    errorEl.hidden = !message;
  }

  function formatDeadline(deadline) {
    if (!deadline) return 'Tanpa deadline';
    return new Date(deadline).toLocaleString('id-ID', {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  }

  function createItem(task) {
    const li = document.createElement('li');
    li.className = `task-item deadline-${DeadlineUtils.getStatus(task)}`;

    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.checked = task.done;
    checkbox.setAttribute('aria-label', `Tandai selesai: ${task.title}`);
    checkbox.addEventListener('change', () => update(task.id, { done: checkbox.checked }));

    const info = document.createElement('div');
    info.className = 'task-info';
    const title = document.createElement('span');
    title.className = 'task-title';
    title.textContent = task.title;
    const deadline = document.createElement('span');
    deadline.className = 'task-deadline';
    deadline.textContent = formatDeadline(task.deadline);
    info.append(title, deadline);

    const del = document.createElement('button');
    del.type = 'button';
    del.className = 'task-delete';
    del.textContent = 'Hapus';
    del.addEventListener('click', () => remove(task.id));

    li.append(checkbox, info, del);
    return li;
  }

  function render() {
    const sorted = DeadlineUtils.sortTasks(tasks);
    listEl.replaceChildren(...sorted.map(createItem));
    const doneCount = tasks.filter((t) => t.done).length;
    countEl.textContent = `${doneCount}/${tasks.length} selesai`;
  }

  async function load() {
    try {
      tasks = await request('/api/tasks');
      showError('');
    } catch (err) {
      showError(`Gagal memuat tugas: ${err.message}`);
    }
    render();
  }

  async function add(title, deadline) {
    try {
      const task = await request('/api/tasks', {
        method: 'POST',
        body: JSON.stringify({ title, deadline: deadline || null }),
      });
      tasks.push(task);
      showError('');
      render();
    } catch (err) {
      showError(err.message);
    }
  }

  async function update(id, changes) {
    try {
      const updated = await request(`/api/tasks/${id}`, {
        method: 'PUT',
        body: JSON.stringify(changes),
      });
      tasks = tasks.map((t) => (t.id === id ? updated : t));
      showError('');
    } catch (err) {
      showError(err.message);
    }
    render();
  }

  async function remove(id) {
    try {
      await request(`/api/tasks/${id}`, { method: 'DELETE' });
      tasks = tasks.filter((t) => t.id !== id);
      showError('');
    } catch (err) {
      showError(err.message);
    }
    render();
  }

  formEl.addEventListener('submit', (event) => {
    event.preventDefault();
    add(titleEl.value, deadlineEl.value);
    formEl.reset();
  });

  return { load, render };
})();

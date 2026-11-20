// Server Express: menyajikan folder frontend/ dan API CRUD tugas.
// Kontrak API ada di docs/data-format.md. Jangan ubah tanpa diskusi.
const express = require('express');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;
const DATA_DIR = path.join(__dirname, 'data');
const DATA_FILE = path.join(DATA_DIR, 'tasks.json');

app.use(express.json());
app.use(express.static(path.join(__dirname, '..', 'frontend')));

// ---------- Penyimpanan (file JSON) ----------
function readTasks() {
  if (!fs.existsSync(DATA_FILE)) return [];
  try {
    return JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'));
  } catch {
    return [];
  }
}

function writeTasks(tasks) {
  fs.mkdirSync(DATA_DIR, { recursive: true });
  fs.writeFileSync(DATA_FILE, JSON.stringify(tasks, null, 2));
}

function isValidDeadline(value) {
  return value === null || (typeof value === 'string' && !Number.isNaN(Date.parse(value)));
}

// ---------- Endpoint ----------
app.get('/api/tasks', (req, res) => {
  res.json(readTasks());
});

app.get('/api/tasks/:id', (req, res) => {
  const task = readTasks().find((t) => t.id === Number(req.params.id));
  if (!task) return res.status(404).json({ error: 'Tugas tidak ditemukan' });
  res.json(task);
});

app.post('/api/tasks', (req, res) => {
  const { title, deadline = null } = req.body;
  if (typeof title !== 'string' || !title.trim()) {
    return res.status(400).json({ error: 'Judul tugas wajib diisi' });
  }
  if (!isValidDeadline(deadline)) {
    return res.status(400).json({ error: 'Format deadline tidak valid' });
  }
  const tasks = readTasks();
  const task = {
    id: tasks.reduce((max, t) => Math.max(max, t.id), 0) + 1,
    title: title.trim(),
    deadline,
    done: false,
    createdAt: new Date().toISOString(),
  };
  tasks.push(task);
  writeTasks(tasks);
  res.status(201).json(task);
});

app.put('/api/tasks/:id', (req, res) => {
  const tasks = readTasks();
  const task = tasks.find((t) => t.id === Number(req.params.id));
  if (!task) return res.status(404).json({ error: 'Tugas tidak ditemukan' });

  const { title, deadline, done } = req.body;
  if (title !== undefined) {
    if (typeof title !== 'string' || !title.trim()) {
      return res.status(400).json({ error: 'Judul tugas tidak boleh kosong' });
    }
    task.title = title.trim();
  }
  if (deadline !== undefined) {
    if (!isValidDeadline(deadline)) {
      return res.status(400).json({ error: 'Format deadline tidak valid' });
    }
    task.deadline = deadline;
  }
  if (done !== undefined) task.done = Boolean(done);

  writeTasks(tasks);
  res.json(task);
});

app.delete('/api/tasks/:id', (req, res) => {
  const tasks = readTasks();
  const index = tasks.findIndex((t) => t.id === Number(req.params.id));
  if (index === -1) return res.status(404).json({ error: 'Tugas tidak ditemukan' });
  const [removed] = tasks.splice(index, 1);
  writeTasks(tasks);
  res.json(removed);
});

app.listen(PORT, () => {
  console.log(`Server jalan di http://localhost:${PORT}`);
});

# pomodoro-todo-app

Aplikasi web To-Do List, Notes, dan Pomodoro Timer dengan sistem gamifikasi.

## Fitur
- To-Do List (tambah, lihat, ubah, hapus tugas)
- Pomodoro Timer (durasi work, break, dan loop bisa diatur)
- Warna tugas berubah saat deadline mendekat
- Tugas bisa diurutkan berdasarkan deadline terdekat
- Notes
- Gamifikasi (level)

## Tech Stack
| Bagian | Teknologi |
|---|---|
| Frontend | HTML, CSS, JavaScript |
| Backend | Node.js + Express |
| Penyimpanan | File JSON (`backend/data/tasks.json`) |
| Version control | Git + GitHub |

## Struktur Folder
```
pomodoro-todo-app/
├── frontend/        # Tampilan (HTML, CSS) dan logika di browser
│   ├── index.html
│   ├── css/
│   └── js/          # termasuk timer.js (logika Pomodoro)
├── backend/         # Server Express + API CRUD tugas
│   └── data/        # Penyimpanan data (tidak di-commit)
├── docs/            # Kesepakatan format data dan API
└── .github/         # Template Pull Request
```

## Cara Menjalankan
Pastikan [Node.js](https://nodejs.org) (versi LTS) sudah terinstall.

```bash
git clone https://github.com/MuhammadSultanAN/pomodoro-todo-app.git
cd pomodoro-todo-app/backend
npm install
npm start
```
Buka `http://localhost:3000` di browser.

## Pembagian Tugas
| Anggota | Peran | Tugas | Branch |
|---|---|---|---|
| (isi nama) | UI/UX | Layout, desain, tampilan semua komponen | `feature/ui-layout` |
| (isi nama) | Backend | API CRUD tugas | `feature/backend-crud` |
| (isi nama) | Timer | Logika Pomodoro (work, break, loop) | `feature/pomodoro-timer` |
| (isi nama) | Integrasi | Gabungkan timer + UI, warna deadline, sorting | `feature/deadline-sorting` |

## Alur Kontribusi
1. Ambil kode terbaru dan buat branch dari `develop`:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/nama-fitur
   ```
2. Kerjakan fitur, lalu commit:
   ```bash
   git add .
   git commit -m "feat: deskripsi singkat"
   git push -u origin feature/nama-fitur
   ```
3. Buka **Pull Request** ke `develop`, minta minimal 1 teman untuk review.
4. Setelah di-approve, merge. Jangan push langsung ke `main` atau `develop`.

### Format commit
| Prefix | Dipakai untuk |
|---|---|
| `feat:` | Fitur baru |
| `fix:` | Perbaikan bug |
| `style:` | Perubahan tampilan / CSS |
| `docs:` | Dokumentasi |
| `refactor:` | Merapikan kode tanpa ubah fungsi |

## Dokumentasi
- [Format data dan kontrak API](docs/data-format.md)

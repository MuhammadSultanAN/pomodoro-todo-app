# pomodoro-todo-app

Aplikasi web To-Do List, Notes, dan Pomodoro Timer dengan sistem gamifikasi.

## Fitur
- To-Do List (tambah, lihat, ubah, hapus tugas)
- Pomodoro Timer (durasi work, break, dan loop bisa diatur)
- Warna tugas berubah saat deadline mendekat
- Tugas bisa diurutkan berdasarkan deadline terdekat
- Notes (pending approval)
- Gamifikasi (level) (pending approval)

## Pembagian Tugas
| Bagian | Anggota |
|---|---|
| Frontend | Kharisma |
| Backend | Andrie |
| Timer | Sultan |
| Integration (gabungin front+back)? | Azzam |

## Struktur Folder (hasil Gemini, bisa restrukturisasi lagi bila perlu)
```
pomodoro_todo_app/
├── data/                      # Auto-generated folder where your save files go
│   └── tasks.dat              # The file FileStorage.java will read/write
├── src/
│   ├── model/                 # ➔ Andrie (CRUD Backend)
│   │   └── Task.java          # The blueprint of a task (id, title, deadline, pomodoros)
│   │
│   ├── storage/               # ➔ Andrie (CRUD Backend)
│   │   └── FileStorage.java   # Handles saving and loading the ArrayList to tasks.dat
│   │
│   ├── controller/            # ➔ Andrie & Sultan
│   │   ├── TaskManager.java   # Andrie: ArrayList logic (add, edit, delete, sort)
│   │   └── TimerLogic.java    # Sultan: The countdown math and threading
│   │
│   ├── ui/                    # ➔ UI/UX & INTEGRATION PERSON
│   │   ├── MainFrame.java     # Kharisma: The main application window
│   │   ├── TaskPanel.java     # Kharisma: The UI design for a single task row
│   │   └── TimerPanel.java    # Kharisma & Sultan: The visual clock and start/stop buttons
│   │
│   └── Main.java              # ➔ INTEGRATION
│                              # Azzam: The entry point that boots up the UI 
│                              # and connects the TaskManager to the MainFrame
└── README.md
```

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

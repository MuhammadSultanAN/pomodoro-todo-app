# Format Data dan Kontrak API

Semua anggota wajib mengikuti dokumen ini supaya UI, backend, timer, dan fitur deadline bisa digabung tanpa masalah. Kalau mau mengubah sesuatu, diskusikan dulu di grup, lalu update file ini.

## 1. Format data tugas

```json
{
  "id": 1,
  "title": "Tugas MSIlit",
  "deadline": "2026-11-25T23:59:00",
  "done": false,
  "createdAt": "2026-11-20T10:00:00"
}
```

| Field | Tipe | Wajib | Keterangan |
|---|---|---|---|
| `id` | number | otomatis | Dibuat backend, unik |
| `title` | string | ya | Judul tugas |
| `deadline` | string (ISO 8601) atau `null` | tidak | Tanggal dan jam batas waktu |
| `done` | boolean | otomatis | Default `false` |
| `createdAt` | string (ISO 8601) | otomatis | Dibuat backend |

## 2. Endpoint API (dikerjakan oleh Backend)

| Method | URL | Fungsi | Body |
|---|---|---|---|
| GET | `/api/tasks` | Ambil semua tugas | - |
| GET | `/api/tasks/:id` | Ambil satu tugas | - |
| POST | `/api/tasks` | Tambah tugas | `{ "title": "...", "deadline": "..." }` |
| PUT | `/api/tasks/:id` | Ubah tugas | field yang mau diubah |
| DELETE | `/api/tasks/:id` | Hapus tugas | - |

Respon sukses berupa JSON (objek tugas atau array tugas). Respon error:
```json
{ "error": "Pesan error" }
```

## 3. Aturan warna deadline (dikerjakan oleh Integrasi)

Hitung selisih `deadline - waktu sekarang`:

| Kondisi | Warna |
|---|---|
| Lebih dari 2 hari | Normal |
| Kurang dari atau sama dengan 2 hari | Kuning |
| Kurang dari 24 jam | Merah |
| Sudah lewat dan belum selesai | Merah tua |
| `done: true` | Abu-abu / dicoret |

## 4. Aturan sorting

1. Tugas yang belum selesai di atas, yang sudah selesai di bawah.
2. Di antara yang belum selesai: deadline terdekat paling atas.
3. Tugas tanpa deadline (`null`) ditaruh paling bawah di grupnya.

## 5. Antarmuka Timer (dikerjakan oleh Timer)

Supaya mudah digabung dengan UI, file `frontend/js/timer.js` sebaiknya menyediakan:

- Pengaturan: durasi `work` (menit), `break` (menit), dan jumlah `loop`
- Fungsi: `start()`, `pause()`, `reset()`
- Event saat satu sesi selesai, misalnya `pomodoro:work-complete` dan `pomodoro:break-complete` (bisa dipakai nanti untuk gamifikasi)
- Menampilkan sisa waktu lewat fungsi/callback, bukan langsung mengubah elemen HTML, supaya UI bebas diatur oleh bagian UI/UX

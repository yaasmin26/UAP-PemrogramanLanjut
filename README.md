# Aplikasi Tiket Bioskop

Aplikasi desktop berbasis **Java Swing** ini dirancang untuk mengelola tiket bioskop.  
Aplikasi menyediakan fitur login, manajemen data tiket (CRUD), dan integrasi dengan API eksternal untuk penyimpanan data.

---

## Fitur Utama

1. **Login Pengguna**
   - Username: `viya`
   - Password: `keceee`
   - Hanya pengguna yang terverifikasi yang dapat mengakses aplikasi.

2. **Dashboard**
   - Menyediakan antarmuka awal yang intuitif.
   - Memungkinkan navigasi cepat ke daftar tiket, input tiket baru, atau keluar dari aplikasi.

3. **Manajemen Tiket**
   - Menampilkan seluruh tiket yang tersimpan.
   - Memungkinkan pengguna untuk memilih tiket tertentu untuk diedit atau dihapus.

4. **Input dan Pembaruan Tiket**
   - Menambahkan tiket baru atau memperbarui tiket yang sudah ada.
   - Validasi input memastikan semua field terisi dan harga berupa angka.
   - Data tiket dikirim ke API eksternal (`http://localhost:8080/api/tiket`) secara otomatis.

5. **Penyimpanan Data Lokal**
   - Data tiket disimpan dalam file `tiket.csv`.
   - Data akan dimuat kembali saat aplikasi dijalankan kembali.

6. **Desain Antarmuka**
   - Menggunakan kombinasi warna soft pink dan hitam.
   - Font modern dengan tampilan panel yang rapi dan responsif.

## Struktur Proyek
- **src/** : Folder ini berisi seluruh kode sumber aplikasi.
  - **TiketBioskopApp.java** : 
    - Mengatur tampilan antarmuka pengguna (UI) menggunakan Java Swing.
    - Menangani logika CRUD (Create, Read, Update, Delete) tiket.
    - Melakukan validasi input, menyimpan data ke file lokal, dan menampilkan data pada tabel.
    - Mengelola navigasi antar panel: Dashboard, Daftar Tiket, dan Input Tiket.
    - Menyediakan fungsionalitas login untuk pengguna.
  - **TiketApiClient.java** :
    - Berfungsi untuk mengirim data tiket ke API eksternal (`http://localhost:8080/api/tiket`) menggunakan HTTP POST.
    - Mendukung integrasi antara aplikasi desktop dan server eksternal.

- **tiket.csv** :
  - File ini digunakan untuk menyimpan data tiket secara lokal.
  - Format penyimpanan: CSV, setiap baris mewakili satu tiket dengan kolom: Judul Film, Studio, Jam, Harga.
  - Dibuat otomatis saat aplikasi dijalankan jika file belum ada.

- **README.md** :
  - Dokumen ini menjelaskan tujuan, fitur, cara penggunaan, dan struktur proyek.
  - Berfungsi sebagai panduan bagi pengguna dan pengembang.

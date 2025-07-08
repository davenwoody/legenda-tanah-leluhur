# 🎮 **AksaraGames Studio – Android App**

<p align="center">
  <img src="https://via.placeholder.com/250x250.png?text=AksaraGames+Logo" alt="Aksara Games Studio Logo" width="200"/>
</p>

<p align="center">
  <a href="https://github.com/your-github-username/your-repo/actions/workflows/gradle-publish.yml">
    <img src="https://github.com/your-github-username/your-repo/actions/workflows/gradle-publish.yml/badge.svg" alt="Android CI Status"/>
  </a>
</p>

---

Selamat datang di _AksaraGames Studio Android App_!  
Proyek ini adalah pondasi aplikasi Android yang dibangun dengan **Gradle** & **Android SDK 29**.  
Workflow **GitHub Actions** sudah siap pakai untuk membangun APK secara otomatis setiap kali ada perubahan di cabang `main`.

## ✨ Fitur Utama

• Build otomatis di GitHub Actions (lihat badge di atas).  
• Konfigurasi Gradle sederhana & kompatibel dengan **JDK 11**.  
• Struktur proyek standar (`app` module).  
• Contoh dependensi **AndroidX** & **Material**.

## 🚀 Memulai

### Prasyarat

1. **JDK 11** atau lebih baru.  
2. **Android SDK** (API 29 & Build-Tools 29.0.3).  
3. **Gradle 5.6.4** (atau gunakan *wrapper* yang dibuat otomatis oleh IDE).

### Clone & Build Lokal

```bash
# Clone repositori
$ git clone https://github.com/your-github-username/your-repo.git
$ cd your-repo

# Bila Anda memiliki wrapper, cukup jalankan:
$ ./gradlew assembleDebug

# Atau jika wrapper belum ada:
$ gradle assembleDebug
```

APK hasil build dapat ditemukan di:  
`app/build/outputs/apk/debug/app-debug.apk`

### Build Otomatis di GitHub Actions

Setiap _push_ atau _pull request_ ke cabang `main` akan memicu workflow `Android CI` yang akan:

1. Menyiapkan JDK 11.  
2. Menginstal Android SDK & Build-Tools.  
3. Menjalankan perintah `gradle assembleDebug`.  
4. Meng-upload APK sebagai artifact.

Artifact dapat di-download langsung dari tab **Actions → run → Summary → Artifacts**.

## 🛠️ Struktur Direktori Singkat

```
.
├── app/                 # Modul aplikasi Android
│   ├── build.gradle     # Konfigurasi modul
│   └── src/             # Kode sumber
├── build.gradle         # Konfigurasi tingkat proyek
├── settings.gradle      # Deklarasi modul
└── .github/
    └── workflows/       # Definisi GitHub Actions
```

## 🤝 Kontribusi

Kontribusi sangat terbuka! Silakan _fork_, buat _branch_ fitur, dan ajukan **Pull Request**.

Pastikan kode tetap rapih & lulus _lint_ sebelum mengajukan PR.

## 📄 Lisensi

Proyek ini berada di bawah lisensi **MIT** – silakan lihat berkas [`LICENSE`](LICENSE) untuk info lebih lanjut.

---

> Made with ❤️ by **AksaraGames Studio**
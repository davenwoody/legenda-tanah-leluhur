// Pastikan nama package ini sama dengan proyek AIDE Anda
package com.aksaragames.studio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

    // Durasi splash screen dalam milidetik
    private static final int SPLASH_TIMEOUT = 3000; // 3 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// Buat Intent untuk memulai MainActivity
					Intent i = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(i);

					// Tutup activity ini agar tidak bisa kembali ke splash screen
					finish();
				}
			}, SPLASH_TIMEOUT);
    }
}

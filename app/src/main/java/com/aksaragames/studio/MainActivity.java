// Pastikan nama package ini sama dengan proyek AIDE Anda
package com.aksaragames.studio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {

    // Komponen UI
    RelativeLayout rootLayout;
    ImageView playerImage, enemyImage;
    ProgressBar playerHpBar, playerMpBar, enemyHpBar;
    TextView storyText, playerStatsText;
    LinearLayout mainMenuLayout, playOptionsLayout, choicesLayout, combatLayout;
    Button playButton, settingsButton, exitButton, newGameButton, continueButton, choiceButton1, choiceButton2, attackButton, defendButton, skillButton, itemButton;

    // Status Game
    int currentScene = 0;
    Player player;
    Enemy currentEnemy;
    boolean isDefending = false;
    Random random = new Random();

    // Animasi
    Animation fadeIn, fadeOut;
    long animationDuration = 500;

    // Audio
    private MediaPlayer bgmPlayer;
    private SoundPool sfxPool;
    private HashMap<String, Integer> sfxMap;
    private boolean isBgmOn = true;
    private boolean isSfxOn = true;

    // Konstanta File Simpanan
    public static final String SAVE_FILE = "SaveGameData";
    public static final String SETTINGS_FILE = "SettingsData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        initializeAudio();
        setupClickListeners();
        loadSettings();
    }

    void initializeUI() {
        rootLayout = findViewById(R.id.root_layout);
        playerImage = findViewById(R.id.player_image);
        enemyImage = findViewById(R.id.enemy_image);
        playerHpBar = findViewById(R.id.player_hp_bar);
        playerMpBar = findViewById(R.id.player_mp_bar);
        enemyHpBar = findViewById(R.id.enemy_hp_bar);
        storyText = findViewById(R.id.story_text);
        playerStatsText = findViewById(R.id.player_stats_text);
        mainMenuLayout = findViewById(R.id.main_menu_layout);
        playOptionsLayout = findViewById(R.id.play_options_layout);
        choicesLayout = findViewById(R.id.choices_layout);
        combatLayout = findViewById(R.id.combat_layout);
        playButton = findViewById(R.id.play_button);
        settingsButton = findViewById(R.id.settings_button);
        exitButton = findViewById(R.id.exit_button);
        newGameButton = findViewById(R.id.new_game_button);
        continueButton = findViewById(R.id.continue_button);
        choiceButton1 = findViewById(R.id.choice_button1);
        choiceButton2 = findViewById(R.id.choice_button2);
        attackButton = findViewById(R.id.attack_button);
        defendButton = findViewById(R.id.defend_button);
        skillButton = findViewById(R.id.skill_button);
        itemButton = findViewById(R.id.item_button);

        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
    }

    void initializeAudio() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();
        sfxPool = new SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build();

        sfxMap = new HashMap<>();
        sfxMap.put("attack", sfxPool.load(this, R.raw.sfx_attack, 1));
        sfxMap.put("skill", sfxPool.load(this, R.raw.sfx_skill, 1));
        sfxMap.put("win", sfxPool.load(this, R.raw.sfx_win, 1));
        sfxMap.put("lose", sfxPool.load(this, R.raw.sfx_lose, 1));
        sfxMap.put("select", sfxPool.load(this, R.raw.sfx_select, 1));

        playMusic(R.raw.bgm_main_menu);
    }

    void setupClickListeners() {
        playButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
        newGameButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        choiceButton1.setOnClickListener(this);
        choiceButton2.setOnClickListener(this);
        attackButton.setOnClickListener(this);
        defendButton.setOnClickListener(this);
        skillButton.setOnClickListener(this);
        itemButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        playSound("select");
        switch(v.getId()) {
            case R.id.play_button:
                mainMenuLayout.setVisibility(View.GONE);
                playOptionsLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.settings_button:
                showSettingsDialog();
                break;
            case R.id.exit_button:
                finish();
                break;
            case R.id.new_game_button:
                startNewGame();
                break;
            case R.id.continue_button:
                loadGame();
                break;
            case R.id.choice_button1: handleChoice(1); break;
            case R.id.choice_button2: handleChoice(2); break;
            case R.id.attack_button: playerAttack(); break;
            case R.id.skill_button: playerSkill(); break;
            case R.id.defend_button: playerDefend(); break;
            case R.id.item_button: showInventory(); break;
        }
    }

    void updateStoryText(final String text) {
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
				@Override public void onAnimationStart(Animation animation) {}
				@Override public void onAnimationEnd(Animation animation) {
					storyText.setText(text);
					storyText.startAnimation(fadeIn);
				}
				@Override public void onAnimationRepeat(Animation animation) {}
			});
        storyText.startAnimation(fadeOut);
    }

    void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pengaturan");
        String[] options = {"Kecepatan Teks", "Suara & Musik", "Hapus Data Simpanan"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showTextSpeedDialog();
                } else if (which == 1) {
                    showAudioSettingsDialog();
                } else if (which == 2) {
                    showDeleteDataConfirmation();
                }
            }
        });
        builder.setNegativeButton("Tutup", null);
        builder.create().show();
    }

    void showTextSpeedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Kecepatan Teks");
        String[] speeds = {"Lambat", "Normal", "Cepat"};
        builder.setItems(speeds, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    animationDuration = 800;
                } else if (which == 1) {
                    animationDuration = 500;
                } else if (which == 2) {
                    animationDuration = 200;
                }
                fadeIn.setDuration(animationDuration);
                fadeOut.setDuration(animationDuration);
                saveSettings();
                Toast.makeText(MainActivity.this, "Kecepatan teks diubah!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    void showAudioSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pengaturan Suara");
        String[] audioOptions = {
            isBgmOn ? "Matikan Musik (BGM)" : "Nyalakan Musik (BGM)",
            isSfxOn ? "Matikan Efek Suara (SFX)" : "Nyalakan Efek Suara (SFX)"
        };
        builder.setItems(audioOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    isBgmOn = !isBgmOn;
                    if (isBgmOn && player != null) {
                        showScene(currentScene);
                    } else if (bgmPlayer != null && bgmPlayer.isPlaying()) {
                        bgmPlayer.pause();
                    }
                } else if (which == 1) {
                    isSfxOn = !isSfxOn;
                }
                saveSettings();
                showAudioSettingsDialog();
            }
        });
        builder.setNegativeButton("Kembali", null);
        builder.create().show();
    }

    void showDeleteDataConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin menghapus semua data game yang tersimpan? Aksi ini tidak bisa dibatalkan.")
            .setPositiveButton("Ya, Hapus", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences prefs = getSharedPreferences(SAVE_FILE, Context.MODE_PRIVATE);
                    prefs.edit().clear().apply();
                    Toast.makeText(MainActivity.this, "Data telah dihapus.", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Tidak", null)
            .show();
    }

    void saveSettings() {
        SharedPreferences prefs = getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("animationDuration", animationDuration);
        editor.putBoolean("isBgmOn", isBgmOn);
        editor.putBoolean("isSfxOn", isSfxOn);
        editor.apply();
    }

    void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
        animationDuration = prefs.getLong("animationDuration", 500);
        isBgmOn = prefs.getBoolean("isBgmOn", true);
        isSfxOn = prefs.getBoolean("isSfxOn", true);
        fadeIn.setDuration(animationDuration);
        fadeOut.setDuration(animationDuration);
    }

    void playMusic(int resourceId) {
        if (!isBgmOn) return;
        if (bgmPlayer != null) {
            if (bgmPlayer.isPlaying()) { bgmPlayer.stop(); }
            bgmPlayer.release();
        }
        bgmPlayer = MediaPlayer.create(this, resourceId);
        bgmPlayer.setLooping(true);
        bgmPlayer.start();
    }

    void playSound(String soundKey) {
        if (!isSfxOn) return;
        if (sfxMap.containsKey(soundKey)) {
            sfxPool.play(sfxMap.get(soundKey), 1, 1, 1, 0, 1.0f);
        }
    }

    void startNewGame() {
        player = new Player(100, 20, 15, 1);
        player.addItem(new Item("Ramuan Penyembuh", "HEAL_HP", 50));
        playOptionsLayout.setVisibility(View.GONE);
        showScene(0);
        saveGame();
    }

    void showScene(int sceneId) {
        currentScene = sceneId;
        updateUI(false); 
        switch (sceneId) {
            case 0:
                rootLayout.setBackgroundResource(R.drawable.village_background);
                playMusic(R.raw.bgm_main_menu);
                updateStoryText("Kamu berada di desa Astina. Kepala desa memintamu mencari 3 Artefak Suci untuk menyegel kembali kekuatan jahat yang mengancam tanah leluhur.");
                choiceButton1.setText("Terima tugas mulia ini");
                choiceButton2.setText("Tanya lebih lanjut soal artefak");
                break;
            case 1:
                rootLayout.setBackgroundResource(R.drawable.forest_background);
                playMusic(R.raw.bgm_forest);
                updateStoryText("Perjalananmu dimulai ke Hutan Keramat. Suasananya gelap dan sunyi, hanya suara langkah kakimu yang terdengar.");
                choiceButton1.setText("Masuk lebih dalam");
                choiceButton2.setText("Periksa sekitar dengan waspada");
                break;
            case 2:
                updateStoryText("Kamu masuk lebih dalam. Tiba-tiba di depanmu muncul sesosok Pocong! Ia sepertinya penjaga pertama hutan ini.");
                currentEnemy = new Enemy("Pocong", 60, 10, 25);
                enemyImage.setImageResource(R.drawable.pocong);
                startCombat();
                break;
            case 3:
                playMusic(R.raw.bgm_forest);
                updateStoryText("Pocong telah lenyap. Di tempatnya berdiri, kamu menemukan sebuah batu bercahaya, Artefak Bumi. Kamu merasa kekuatanmu sedikit bertambah.");
                player.maxHp += 20; player.hp = player.maxHp;
                choiceButton1.setText("Lanjutkan ke Gua Pertapa");
                choiceButton2.setVisibility(View.GONE);
                break;
            case 4:
                rootLayout.setBackgroundResource(R.drawable.cave_background);
                playMusic(R.raw.bgm_cave);
                playerImage.setImageResource(R.drawable.player_avatar);
                enemyImage.setImageResource(R.drawable.hermit);
                enemyImage.setVisibility(View.VISIBLE);
                updateStoryText("Kamu tiba di sebuah gua. Di dalamnya duduk seorang pertapa tua. 'Aku Eyang Pertapa,' katanya. 'Untuk artefak kedua, pergilah ke Gunung Tandus.'");
                choiceButton1.setText("Tanya soal Gunung Tandus");
                choiceButton2.setText("Minta petunjuk lain");
                break;
            case 5:
                updateStoryText("'Di Gunung Tandus bersemayam Siluman Ular penjaga Artefak Angin. Hati-hati, sisiknya keras bagai baja,' pesan Eyang Pertapa.");
                player.addItem(new Item("Ramuan Kuat", "BOOST_ATK", 5));
                Toast.makeText(this, "Mendapatkan Ramuan Kuat!", Toast.LENGTH_SHORT).show();
                choiceButton1.setText("Berangkat ke Gunung Tandus");
                choiceButton2.setVisibility(View.GONE);
                break;
            case 6:
                rootLayout.setBackgroundResource(R.drawable.mountain_background);
                playMusic(R.raw.bgm_forest);
                updateStoryText("Setelah perjalanan berat, kamu tiba di Gunung Tandus. Udara kering dan bebatuan tajam menyambutmu. Di puncak, seekor Siluman Ular raksasa telah menantimu.");
                currentEnemy = new Enemy("Siluman Ular", 120, 20, 75);
                enemyImage.setImageResource(R.drawable.siluman_ular);
                startCombat();
                break;
            case 7:
				playMusic(R.raw.bgm_main_menu);
				updateStoryText("Siluman Ular musnah menjadi debu. Artefak Angin muncul di hadapanmu. Kamu kini hanya perlu mencari Artefak Air yang terakhir.");
				player.attack += 5;
				choiceButton1.setText("Ikuti petunjuk ke Rawa Misterius");
				choiceButton2.setVisibility(View.GONE);
				break;
            case 8:
				rootLayout.setBackgroundResource(R.drawable.forest_background);
				playMusic(R.raw.bgm_forest);
				updateStoryText("Rawa Misterius diselimuti kabut tebal. Di tengah rawa, sesosok Genderuwo besar penjaga Artefak Air menghadangmu!");
				currentEnemy = new Enemy("Genderuwo", 200, 30, 150);
				enemyImage.setImageResource(R.drawable.genderuwo);
				startCombat();
				break;
            case 9:
				playMusic(R.raw.bgm_main_menu);
				updateStoryText("Genderuwo telah dikalahkan! Dengan ketiga Artefak Suci di tangan, kamu kembali ke desa sebagai pahlawan.");
				choiceButton1.setText("Tamat");
				choiceButton2.setVisibility(View.GONE);
				break;
        }
        updateAllStatsUI();
        saveGame();
    }

    void handleChoice(int choice) {
        if (currentScene == 0) {
            if (choice == 1) showScene(1);
            else updateStoryText("Kepala desa: 'Tiga artefak itu adalah Artefak Bumi, Angin, dan Air. Temukan ketiganya, Nak!'");
        } else if (currentScene == 1) { showScene(2);
        } else if (currentScene == 3) { showScene(4);
        } else if (currentScene == 4) { showScene(5);
        } else if (currentScene == 5) { showScene(6);
        } else if (currentScene == 7) { showScene(8);
        } else if (currentScene == 9) { finish(); }
    }

    // --- LOGIKA PERTARUNGAN DENGAN ANIMASI ---

    void animatePlayerAttack() {
        TranslateAnimation moveForward = new TranslateAnimation(0, 50, 0, 0);
        moveForward.setDuration(150);
        moveForward.setAnimationListener(new Animation.AnimationListener() {
				@Override public void onAnimationStart(Animation animation) {}
				@Override public void onAnimationRepeat(Animation animation) {}
				@Override public void onAnimationEnd(Animation animation) {
					TranslateAnimation moveBack = new TranslateAnimation(50, 0, 0, 0);
					moveBack.setDuration(150);
					playerImage.startAnimation(moveBack);
				}
			});
        playerImage.startAnimation(moveForward);
    }

    void animateEnemyHit() {
        enemyImage.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enemyImage.clearColorFilter();
            }
        }, 200);
    }

    void startCombat(){
        updateUI(true);
        updateStoryText("Pertarungan dimulai melawan " + currentEnemy.name + "!");
    }

    void playerAttack(){
        isDefending = false;
        playSound("attack");
        animatePlayerAttack(); 

        final int damage = player.attack + random.nextInt(5);
        currentEnemy.takeDamage(damage);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateEnemyHit();
                updateStoryText("Kamu menyerang " + currentEnemy.name + ", menyebabkan " + damage + " kerusakan!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkCombatStatus();
                    }
                }, 1500);
            }
        }, 300);
    }

    void playerSkill(){
        isDefending = false;
        int skillCost = 10;
        if(player.mp >= skillCost) {
            playSound("skill");
            player.mp -= skillCost;
            final int damage = player.attack * 2;
            currentEnemy.takeDamage(damage);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateEnemyHit();
                    updateStoryText("Kamu menggunakan 'Pukulan Batin', menyebabkan " + damage + " kerusakan!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkCombatStatus();
                        }
                    }, 1500);
                }
            }, 300);
        } else {
            updateStoryText("MP tidak cukup untuk menggunakan skill!");
        }
        updateAllStatsUI();
    }

    void playerDefend(){
        isDefending = true;
        updateStoryText("Kamu bersiap untuk bertahan.");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enemyTurn();
            }
        }, 1500);
    }

    void enemyTurn() {
        int damage = currentEnemy.attack + random.nextInt(5);
        String log;
        if (isDefending) {
            damage /= 2;
            log = "\nKamu bertahan! Kerusakan berkurang menjadi " + damage + ".";
        } else {
            log = "\n" + currentEnemy.name + " menyerangmu sebesar " + damage + " kerusakan.";
        }
        player.takeDamage(damage);
        updateStoryText(storyText.getText() + log);

        if (!player.isAlive()) {
            gameOver();
        }
        updateAllStatsUI();
        isDefending = false;
    }

    void checkCombatStatus() {
        if (!currentEnemy.isAlive()) {
            winCombat();
        } else {
            enemyTurn();
        }
    }

    void winCombat() {
        playSound("win");
        updateStoryText("Kamu berhasil mengalahkan " + currentEnemy.name + "!");
        player.addExp(currentEnemy.expGiven);
        updateUI(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentEnemy.name.equals("Pocong")) showScene(3);
                else if (currentEnemy.name.equals("Siluman Ular")) showScene(7);
                else if (currentEnemy.name.equals("Genderuwo")) showScene(9);
            }
        }, 2500);
    }

    void gameOver() {
        playSound("lose");
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.stop();
        }
        updateStoryText("Kamu telah kalah... Permainan berakhir.");
        combatLayout.setVisibility(View.GONE);
        choicesLayout.setVisibility(View.GONE);
    }

    void showInventory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Inventaris");
        final List<Item> inventory = player.getInventory();
        if (inventory.isEmpty()) {
            builder.setMessage("Inventaris kosong.");
        } else {
            String[] itemNames = new String[inventory.size()];
            for (int i = 0; i < inventory.size(); i++) { itemNames[i] = inventory.get(i).name; }
            builder.setItems(itemNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Item selectedItem = inventory.get(which);
                    player.useItem(selectedItem, which);
                    updateStoryText("Kamu menggunakan " + selectedItem.name + ".");
                    updateAllStatsUI();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enemyTurn();
                        }
                    }, 1500);
                }
            });
        }
        builder.setNegativeButton("Tutup", null);
        builder.create().show();
    }

    void updateUI(boolean inCombat) {
        mainMenuLayout.setVisibility(View.GONE);
        playOptionsLayout.setVisibility(View.GONE);
        combatLayout.setVisibility(inCombat ? View.VISIBLE : View.GONE);
        choicesLayout.setVisibility(inCombat ? View.GONE : View.VISIBLE);
        if(inCombat) {
            enemyImage.setVisibility(View.VISIBLE);
            enemyHpBar.setVisibility(View.VISIBLE);
        } else if (currentScene != 4) { 
			enemyImage.setVisibility(View.GONE);
			enemyHpBar.setVisibility(View.GONE);
        }
        playerImage.setVisibility(View.VISIBLE);
        playerHpBar.setVisibility(View.VISIBLE);
        playerMpBar.setVisibility(View.VISIBLE);
        playerStatsText.setVisibility(View.VISIBLE);
        choiceButton1.setVisibility(View.VISIBLE);
        choiceButton2.setVisibility(View.VISIBLE);
    }

    void updateAllStatsUI() {
        playerHpBar.setMax(player.maxHp);
        playerHpBar.setProgress(player.hp);
        playerMpBar.setMax(player.maxMp);
        playerMpBar.setProgress(player.mp);
        playerStatsText.setText("Level " + player.level + " (EXP: " + player.exp + "/" + player.expToNextLevel + ")");
        if (currentEnemy != null && currentEnemy.isAlive()) {
            enemyHpBar.setMax(currentEnemy.maxHp);
            enemyHpBar.setProgress(currentEnemy.hp);
        }
    }

    void saveGame() { 
        SharedPreferences prefs = getSharedPreferences(SAVE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("currentScene", currentScene);
        editor.putInt("player_level", player.level);
        editor.putInt("player_hp", player.hp);
        editor.putInt("player_mp", player.mp);
        editor.putInt("player_attack", player.attack);
        editor.putInt("player_exp", player.exp);
        editor.putInt("player_max_hp", player.maxHp);
        int potionCount = 0, boostCount = 0;
        for(Item item : player.getInventory()){
            if(item.name.equals("Ramuan Penyembuh")) potionCount++;
            if(item.name.equals("Ramuan Kuat")) boostCount++;
        }
        editor.putInt("item_potion_count", potionCount);
        editor.putInt("item_boost_count", boostCount);
        editor.apply();
    }

    void loadGame() { 
        SharedPreferences prefs = getSharedPreferences(SAVE_FILE, Context.MODE_PRIVATE);
        if(!prefs.contains("currentScene")) {
            Toast.makeText(this, "Tidak ada data game tersimpan.", Toast.LENGTH_SHORT).show();
            return;
        }
        int level = prefs.getInt("player_level", 1);
        int attack = prefs.getInt("player_attack", 15);
        int exp = prefs.getInt("player_exp", 0);
        int maxHp = prefs.getInt("player_max_hp", 100);
        player = new Player(maxHp, 20 + (level-1)*5, attack, level);
        player.exp = exp;
        player.hp = prefs.getInt("player_hp", player.maxHp);
        player.mp = prefs.getInt("player_mp", player.maxMp);
        player.getInventory().clear();
        int potionCount = prefs.getInt("item_potion_count", 0);
        for(int i = 0; i < potionCount; i++) { player.addItem(new Item("Ramuan Penyembuh", "HEAL_HP", 50)); }
        int boostCount = prefs.getInt("item_boost_count", 0);
        for(int i = 0; i < boostCount; i++) { player.addItem(new Item("Ramuan Kuat", "BOOST_ATK", 5)); }
        playOptionsLayout.setVisibility(View.GONE);
        showScene(prefs.getInt("currentScene", 0));
        Toast.makeText(this, "Game dimuat!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bgmPlayer != null && !bgmPlayer.isPlaying() && isBgmOn) {
            bgmPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (sfxPool != null) {
            sfxPool.release();
            sfxPool = null;
        }
    }

    class Player {
        int hp, maxHp, mp, maxMp, attack, level, exp, expToNextLevel;
        private List<Item> inventory = new ArrayList<>();
        Player(int maxHp, int maxMp, int attack, int level) { this.maxHp = maxHp; this.hp = maxHp; this.maxMp = maxMp; this.mp = maxMp; this.attack = attack; this.level = level; this.exp = 0; calculateExpToNextLevel(); }
        void takeDamage(int damage) { hp -= damage; if (hp < 0) hp = 0; }
        boolean isAlive() { return hp > 0; }
        void addExp(int amount) {
            exp += amount;
            Toast.makeText(MainActivity.this, "Mendapatkan " + amount + " EXP!", Toast.LENGTH_SHORT).show();
            if(exp >= expToNextLevel) { levelUp(); }
        }
        void levelUp() {
            level++;
            exp = 0; 
            maxHp += 20;
            maxMp += 5;
            attack += 3;
            hp = maxHp;
            mp = maxMp;
            calculateExpToNextLevel();
            Toast.makeText(MainActivity.this, "LEVEL UP! Kamu mencapai Level " + level, Toast.LENGTH_LONG).show();
        }
        void calculateExpToNextLevel(){ expToNextLevel = level * 100; }
        void addItem(Item item) { inventory.add(item); }
        void useItem(Item item, int position) {
            if(item.type.equals("HEAL_HP")) {
                hp += item.value;
                if(hp > maxHp) hp = maxHp;
                Toast.makeText(MainActivity.this, "HP pulih sebanyak "+item.value+"!", Toast.LENGTH_SHORT).show();
            } else if (item.type.equals("BOOST_ATK")) {
                attack += item.value;
				Toast.makeText(MainActivity.this, "Serangan permanen bertambah "+item.value+"!", Toast.LENGTH_SHORT).show();
            }
            inventory.remove(position);
        }
        public List<Item> getInventory() { return inventory; }
    }

    class Enemy {
        String name; int hp, maxHp, attack, expGiven;
        Enemy(String name, int maxHp, int attack, int expGiven) { this.name = name; this.maxHp = maxHp; this.hp = maxHp; this.attack = attack; this.expGiven = expGiven; }
        void takeDamage(int damage) { hp -= damage; if (hp < 0) hp = 0; }
        boolean isAlive() { return hp > 0; }
    }

    class Item {
        String name; String type; int value;
        Item(String name, String type, int value) { this.name = name; this.type = type; this.value = value; }
    }
}

package com.hpjadul.editfoto;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageView fotoView;
    TextToSpeech tts;
    Uri audioUri = null;
    MediaPlayer player;
    ActivityResultLauncher<Intent> pickFoto, pickAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fotoView = findViewById(R.id.fotoView);
        EditText input = findViewById(R.id.inputTeks);

        // Launcher Pilih Foto
        pickFoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), res -> {
            if(res.getResultCode()==RESULT_OK && res.getData()!=null){
                fotoView.setImageURI(res.getData().getData());
            }
        });
        // Launcher Pilih Audio
        pickAudio = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), res -> {
            if(res.getResultCode()==RESULT_OK && res.getData()!=null){
                audioUri = res.getData().getData();
                Toast.makeText(this,"Audio dipilih: "+audioUri.getLastPathSegment(),Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnPilihFoto).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickFoto.launch(i);
        });
        findViewById(R.id.btnPilihAudio).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("audio/*");
            pickAudio.launch(i);
        });

        // SET SUARA COWOK
        tts = new TextToSpeech(this, s -> {
            if(s==TextToSpeech.SUCCESS){
                tts.setLanguage(new Locale("id","ID"));
                tts.setPitch(0.7f); // Suara cowok berat
                tts.setSpeechRate(0.9f);
            }
        });

        findViewById(R.id.btnBicara).setOnClickListener(v -> {
            if(audioUri != null){
                // MODE AUDIO MP3
                if(player!=null) player.release();
                player = MediaPlayer.create(this, audioUri);
                player.start();
                animasiBicara(player.getDuration());
                Toast.makeText(this,"Memutar Audio + Foto Bicara",Toast.LENGTH_SHORT).show();
            } else {
                // MODE TEKS JADI SUARA COWOK
                String teks = input.getText().toString();
                tts.speak(teks, TextToSpeech.QUEUE_FLUSH, null, "id");
                animasiBicara(4000);
            }
        });
    }

    void animasiBicara(int durasi){
        // Animasi mulut buka tutup simpel
        Runnable goyang = new Runnable() {
            int count = 0;
            @Override public void run() {
                if(count < durasi/100){
                    fotoView.animate().scaleY(count%2==0?1.1f:1f).setDuration(100).withEndAction(this).start();
                    count++;
                } else {
                    fotoView.animate().scaleY(1f).setDuration(100).start();
                }
            }
        };
        goyang.run();
    }
}

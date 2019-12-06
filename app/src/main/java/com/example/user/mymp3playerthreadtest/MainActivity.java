package com.example.user.mymp3playerthreadtest;

import android.Manifest;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listViewMP3;
    private Button btnPlay, btnStop;
    private TextView tvPlay, tvTime;
    private SeekBar pbMP3;

    private MediaPlayer mediaPlayer;
    private ArrayList<String> list = new ArrayList<String>();
    private String selectedMP3;
    static final String MP3_PATH =  "/sdcard/musicc/";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewMP3 = findViewById(R.id.listViewMP3);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        tvPlay = findViewById(R.id.tvPlay);
        tvTime = findViewById(R.id.tvTime);
        pbMP3 = findViewById(R.id.pbMP3);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        File[] files = new File(MP3_PATH).listFiles();
        for (File file : files) {
            String fileName = file.getName();
            String extendName = fileName.substring(fileName.length() - 3);
            if (extendName.equals("mp3")) {
                list.add(fileName);

            }

        }//end of for

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, list);
        listViewMP3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewMP3.setAdapter(adapter);
        listViewMP3.setItemChecked(0, true);
        Log.d(TAG,"onCreate");

        listViewMP3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedMP3 = list.get(position);
            }
        });

        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);

//        btnPlay.setClickable(true);
//        btnStop.setClickable(false);
        btnPlay.setEnabled(true);
        btnStop.setEnabled(false);
        pbMP3.setProgress(0);
        tvTime.setText("진행시간 : 0");
        selectedMP3 = list.get(0);


    }//end of onCreate

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(MP3_PATH + selectedMP3);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

//                    btnPlay.setClickable(false);
//                    btnStop.setClickable(true);
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(true);
                    tvPlay.setText("실행중인 음악명:" + selectedMP3);

                    Thread thread = new Thread() {


                        @Override
                        public void run() {
                            if (mediaPlayer == null) {
                                return;
                            }
                            //1.노래에 총 걸리는 시간                             //재생시간
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTime.setText(selectedMP3 + " 재생시간 " + mediaPlayer.getDuration());
                                    pbMP3.setMax(mediaPlayer.getDuration());
                                }
                            });


                            while (mediaPlayer.isPlaying()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //자기가 진행되는 시간
                                        pbMP3.setProgress(mediaPlayer.getCurrentPosition());
                                        tvTime.setText(selectedMP3 + "진행시간 : " + simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                                    }
                                });//end of runOnUiThread() 스레드안에서 화면위젯변경을 할 수 있는 스레드

                                SystemClock.sleep(200);
                            }//end of while
                        }
                    };

                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btnStop:
                mediaPlayer.stop();
                mediaPlayer.reset();
//                btnPlay.setClickable(true);
//                btnStop.setClickable(false);
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                tvPlay.setText("음악");
                pbMP3.setProgress(0);
                tvTime.setText("진행시간 : 0");

                break;
        }
    }
}

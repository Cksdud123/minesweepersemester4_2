package com.example.minesweepersemester4_2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView minesCountTextView;
    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView 가져오기
        minesCountTextView = findViewById(R.id.minesCountTextView);
        timeTextView = findViewById(R.id.timeTextView);

        GameEngine.getInstance().createGrid(this);
        startGame();

        TextView smileButton = findViewById(R.id.midTextView);
        smileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameEngine.getInstance().resetGame();
                startGame(); // 새로운 게임 시작
            }
        });
    }

    // 게임 시작 메소드
    private void startGame() {
        // GameEngine의 인스턴스를 통해 지뢰 갯수 설정
        int minesCount = GameEngine.getInstance().getMinesCount();
        updateMinesCount(minesCount);

        updateGameTime(0);
    }

    // 지뢰 갯수 업데이트 메소드
    public void updateMinesCount(int count) {
        minesCountTextView.setText("Mines: " + count);
    }

    // 시간 업데이트 메소드
    public void updateGameTime(int time) {
        timeTextView.setText("Time: " + time);
    }
}
package com.example.minesweepersemester4_2;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.minesweepersemester4_2.util.Generator;
import com.example.minesweepersemester4_2.util.PrintGrid;
import com.example.minesweepersemester4_2.views.Cell;

/**
 * Created by Marcell on 2016. 04. 01..
 */
public class GameEngine {
    private static GameEngine instance;
    public static final int BOMB_NUMBER = 10;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    private Context context;
    private int gameTime = 0;
    private Handler handler;
    private boolean isTimerRunning = false;

    public int getGameTime() {
        return gameTime;
    }
    private void increaseGameTime() {
        gameTime++;
        ((MainActivity) context).updateGameTime(gameTime);
    }
    public int getMinesCount() {
        return BOMB_NUMBER;
    }
    private Cell[][] MinesweeperGrid = new Cell[WIDTH][HEIGHT];

    public static GameEngine getInstance() {
        if( instance == null ){
            instance = new GameEngine();
        }
        return instance;
    }

    private GameEngine(){ }
    public void createGrid(Context context){
        Log.e("GameEngine","createGrid is working");
        this.context = context;


        // create the grid and store it
        int[][] GeneratedGrid = Generator.generate(BOMB_NUMBER,WIDTH, HEIGHT);
        PrintGrid.print(GeneratedGrid,WIDTH,HEIGHT);
        setGrid(context,GeneratedGrid);
    }

    private void setGrid( final Context context, final int[][] grid ){
        for( int x = 0 ; x < WIDTH ; x++ ){
            for( int y = 0 ; y < HEIGHT ; y++ ){
                if( MinesweeperGrid[x][y] == null ){
                    MinesweeperGrid[x][y] = new Cell( context , x,y);
                }
                MinesweeperGrid[x][y].setValue(grid[x][y]);
                MinesweeperGrid[x][y].invalidate();
            }
        }
    }

    public Cell getCellAt(int position) {
        int x = position % WIDTH;
        int y = position / WIDTH;

        return MinesweeperGrid[x][y];
    }

    public Cell getCellAt( int x , int y ){
        return MinesweeperGrid[x][y];
    }

    public void click( int x , int y ){
        if (!isTimerRunning) {
            // 최초 클릭 시 게임 시간 증가 시작
            startGameTimeCounter();
            isTimerRunning = true;
        }

        if( x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT && !getCellAt(x,y).isClicked() ){
            getCellAt(x,y).setClicked();

            if( getCellAt(x,y).getValue() == 0 ){
                for( int xt = -1 ; xt <= 1 ; xt++ ){
                    for( int yt = -1 ; yt <= 1 ; yt++){
                        if( xt != yt ){
                            click(x + xt , y + yt);
                        }
                    }
                }
            }

            if( getCellAt(x,y).isBomb() ){
                onGameLost();
            }
        }

        checkEnd();
    }
    private boolean checkEnd(){
        int bombNotFound = BOMB_NUMBER;
        int notRevealed = WIDTH * HEIGHT;
        for ( int x = 0 ; x < WIDTH ; x++ ){
            for( int y = 0 ; y < HEIGHT ; y++ ){
                if( getCellAt(x,y).isRevealed() || getCellAt(x,y).isFlagged() ){
                    notRevealed--;
                }

                if( getCellAt(x,y).isFlagged() && getCellAt(x,y).isBomb() ){
                    bombNotFound--;
                }
            }
        }

        if( bombNotFound == 0 && notRevealed == 0 ){
            Toast.makeText(context,"Game won", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void startGameTimeCounter() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                increaseGameTime();
                handler.postDelayed(this, 1000); // 1초마다 호출
            }
        }, 1000);
    }
    public void flag( int x , int y ){
        boolean isFlagged = getCellAt(x,y).isFlagged();
        getCellAt(x,y).setFlagged(!isFlagged);
        getCellAt(x,y).invalidate();
    }

    public void resetGame() {
        // 열려있는 칸을 닫음
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x, y).setRevealed(false);
            }
        }

        // 새로운 지뢰 배치 및 게임 상태 초기화
        createGrid(context);

        // 게임 시간 초기화
        stopGameTimeCounter();
        gameTime = 0;
        isTimerRunning = false;
        ((MainActivity) context).updateGameTime(gameTime);
    }
    private void stopGameTimeCounter() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
    private void onGameLost(){
        // handle lost game
        Toast.makeText(context,"Game lost", Toast.LENGTH_SHORT).show();

        for ( int x = 0 ; x < WIDTH ; x++ ) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x,y).setRevealed();
            }
        }
    }
}
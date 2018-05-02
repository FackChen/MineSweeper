package com.example.minesweeper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class MainView extends View {
    private Mine mine;
    private  boolean isFirst=true;
    private  Context context;
    private Random randomNum=new Random();
    private final int mineNum=randomNum.nextInt(10)+5;
    private final int greenNum=0;
    private  final int Ver=15;
    private  final int Hor=10;
    private   int TILE_WIDTH;
    private  boolean isFalse=false;
    public  MainView(Context context)
    {
        super(context);
        this.context=context;

        TILE_WIDTH= MainActivity.W/10;
        mine=new Mine((MainActivity.W-Hor*TILE_WIDTH)/2,(MainActivity.H-Ver*TILE_WIDTH)/2,Hor,Ver,mineNum,greenNum,TILE_WIDTH,false);
        try {
            mine.init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void logic()
    {
        int count=0;
        for (int i=0;i<mine.numVer;i++)
        {
            for (int j=0;j<mine.numHor;j++)
            {
                if(!mine.tile[i][j].open)
                {
                    count++;
                }
            }
        }
        if(count==mineNum)
        {
            new AlertDialog.Builder(context)
                    .setMessage("恭喜你，你找出了所有雷")
                    .setCancelable(false)
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mine.init();
                            invalidate();
                            isFirst=true;
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mine.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if(x>=mine.tileWidth*9&&y>=0&&x<=mine.tileWidth*10&&y<=mine.tileWidth)
            {
                if(mine.flagMode){
                    mine.flagMode=false;
                    Toast.makeText(context,"已关闭标记模式",Toast.LENGTH_SHORT).show();
                }else{
                    mine.flagMode=true;
                    Toast.makeText(context,"已开启标记模式",Toast.LENGTH_SHORT).show();
                }
            }
            if (x >= mine.x && y >= mine.y && x <= (mine.mapWidth + mine.x) && y <= (mine.y + mine.mapHeight)) {
                int idxX = (x - mine.x) / mine.tileWidth;
                int idxY = (y - mine.y) / mine.tileWidth;
                if (mine.flagMode) {
//                    Log.d("测试","已开启");
                    if(mine.tile[idxY][idxX].flag){
                        mine.tile[idxY][idxX].flag=false;
                    }else{
                        mine.tile[idxY][idxX].flag=true;
                    }
                    invalidate();
                } else {
                    mine.open(new Mine.Point(idxX, idxY), isFirst);
                    isFirst = false;
                    if (mine.tile[idxY][idxX].value == -1) {
                        mine.isDrawAllMine = true;
                        new AlertDialog.Builder(context)
                                .setCancelable(false)
                                .setMessage("很遗憾，你踩到雷了！")
                                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mine.init();
                                        isFalse = true;
                                        isFirst = true;

                                        invalidate();
                                    }
                                })
                                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.exit(0);
                                    }
                                })
                                .show();
                    }
                }
                if (isFalse) {
                    isFalse = false;
                    invalidate();
                    return true;
                }
                logic();
                invalidate();
            }
        }
        return true;
    }
}  
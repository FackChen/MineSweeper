package com.example.minesweeper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class ExtendMode extends View{
    private Mine mine;
    private  boolean isFirst=true;//是否第一次打开
    private  Context context;
    private Random randomNum=new Random();//随机数
    private final int mineNum=randomNum.nextInt(10)+5;//10—15个地雷
    private final int greenNum=1;//1个绿块
    private  final int Ver=15;//纵向方块数
    private  final int Hor=10;//横向方块数
    private   int TILE_WIDTH;//方块框宽度
    private  boolean isFalse=false;//是否已经失败
    int Gcount=0;//当前绿块数

    /*
    构造函数
     */
    public  ExtendMode(Context context)
    {
        super(context);
        this.context=context;
        TILE_WIDTH= MainActivity.W/10;
        mine=new Mine((MainActivity.W-Hor*TILE_WIDTH)/2,(MainActivity.H-Ver*TILE_WIDTH)/2,Hor,Ver,mineNum,greenNum,TILE_WIDTH,true);
        try {
            mine.init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    输赢的逻辑判断
     */
    public void logic()
    {
        int count=0;
        for (int i=0;i<mine.numVer;i++)
        {
            for (int j=0;j<mine.numHor;j++)
            {
                if(!mine.tile[i][j].open)
                {
                    count++;//看剩下的还有几个方块没打开
                }
            }
        }
//        Log.d("Gcount个数：",String.valueOf(Gcount));
//        Log.d("剩余个数：",String.valueOf(count));
        if(count==(mineNum-Gcount))
        {
            new AlertDialog.Builder(context)
                    .setMessage("恭喜你，你找出了所有雷")
                    .setCancelable(false)
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mine.init();
                            invalidate();
                            Gcount=0;
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

    /*
    绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        mine.draw(canvas);
    }

    /*
    点击屏幕并进行判断
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            int x=(int)event.getX();
            int y=(int)event.getY();
            //如果是点击在标记图案上就toast显示
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
            //判断是否在地图内
            if(x>=mine.x&&y>=mine.y&&x<=(mine.mapWidth+mine.x)&&y<=(mine.y+mine.mapHeight))
            {
                int idxX=(x-mine.x)/mine.tileWidth;
                int idxY=(y-mine.y)/mine.tileWidth;
                //判断是否已经开启标记模式
                if(mine.flagMode){
//                    Log.d("测试","已开启");
                    if(mine.tile[idxY][idxX].flag){
                        mine.tile[idxY][idxX].flag=false;
                    }else{
                        mine.tile[idxY][idxX].flag=true;
                    }
                    invalidate();
                }else{
                mine.open(new Mine.Point(idxX,idxY),isFirst);
                isFirst=false;
                //如果打开的是绿块，拥有数加一
                if(mine.tile[idxY][idxX].value==-2&&mine.tile[idxY][idxX].isFirst){
                    mine.haveGreen++;
                    mine.tile[idxY][idxX].open=true;
                    mine.tile[idxY][idxX].isFirst=false;
                }
                //判断打开的是否是地雷
                if(mine.tile[idxY][idxX].value==-1)
                {
                    //如果拥有绿块，则抵消一次伤害，绿块数量减一，否则绘制出所有地雷，游戏结束
                    if(mine.haveGreen>0) {
                        Gcount++;
                        mine.haveGreen--;
                        mine.tile[idxY][idxX].open=true;
                    }else{
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
                //判断是否结束
                if(isFalse)
                {
                    isFalse=false;
                    invalidate();
                    Gcount=0;
                    return true;
                }
                logic();
                invalidate();
            }
            }
        }
        return true;
    }
}
package com.example.minesweeper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Mine {
    public   int x;//地图的横向起始坐标
    public   int y;//地图的纵向起始坐标
    public    int numHor;//横向方块数量
    public   int numVer;//纵向方块数量
    private  int mineNum ;//雷的数量
    private int greenNum;//绿块数量
    public int haveGreen;//拥有的绿块数
    public   int tileWidth;//方块边长
    public  int mapWidth;//地图宽度
    public int mapHeight;//地图高度
    public static short EMPTY=0;
    public static short MINE=-1;
    public static short GREENPOINT=-2;
    public Tile[][] tile;//方块的方位
    private  Paint textPaint;//绘制文本
    private Paint numberPaint;//绘制数字
    private Paint bmpPaint;//绘制地雷
    private Paint greenPaint;//绘制绿块
    private  Paint tilePaint;//绘制方块
    private  Paint rectPaint;//绘制矩形
//    private  Paint minePaint;
    private Random rd=new Random();//设置随机数
    public boolean isDrawAllMine=false;//是否画出所有的地雷
    public boolean extendMode;//是否是扩展模式
    public boolean flagMode;//是否是标记模式

    /*
    方块周围的8个方向
     */
    private  int[][] dir={
            {-1,1},
            {0,1},
            {1,1},
            {-1,0},
            {1,0},
            {-1,-1},
            {0,-1},
            {1,-1}
    };

    /*
    定义方块
     */
    public   class Tile{
        short value;
        boolean flag;
        boolean open;
        boolean isFirst;
        public Tile()
        {
            this.value=0;
            this.flag=false;
            this.open=false;
            this.isFirst=true;
        }
    }

    /*
    表示位置，并通过哈希函数来判断位置是否相同
     */
    public static class Point{
        private int x;
        private int y;
        public Point(int x,int y)
        {
            this.x=x;
            this.y=y;
        }

        //重写哈希函数
        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return 2*x+y;
        }

        //判断是否相同
        @Override
        public boolean equals(Object obj) {
            // TODO Auto-generated method stub
            return this.hashCode()==((Point)(obj)).hashCode();

        }
    }

    /*
    构造函数
     */
    public Mine(int x, int y, int hor, int ver, int mineNum,int greenNum, int tileWidth,boolean extendMode)
    {
        this.x=x;
        this.y=y;
        this.numHor = hor;
        this.numVer = ver;
        this.mineNum=mineNum;
        this.greenNum=greenNum;
        this.tileWidth=tileWidth;
        mapWidth=hor*tileWidth;
        mapHeight=ver*tileWidth;
        this.extendMode=extendMode;
        this.haveGreen=haveGreen;

        textPaint=new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(MainActivity.W/20);
        textPaint.setColor(Color.RED);

        numberPaint=new Paint();
        numberPaint.setAntiAlias(true);
        numberPaint.setTextSize(MainActivity.W/10);
        numberPaint.setColor(Color.RED);

        bmpPaint=new Paint();
        bmpPaint.setAntiAlias(true);
        bmpPaint.setColor(Color.DKGRAY);

        greenPaint=new Paint();
        greenPaint.setAntiAlias(true);
        greenPaint.setColor(Color.GREEN);

        tilePaint =new Paint();
        tilePaint.setAntiAlias(true);
        tilePaint.setColor(0xff1faeff);

//        minePaint =new Paint();
//        minePaint.setAntiAlias(true);
//        minePaint.setColor(0xffff981d);

        rectPaint =new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setColor(0xff000000);
        rectPaint.setStyle(Paint.Style.STROKE);

        tile=new Tile[ver][hor];
    }

    /*
    初始化地图
     */
    public  void init()
    {
        for (int i = 0; i< numVer; i++)
        {
            for (int j = 0; j< numHor; j++)
            {
                tile[i][j]=new Tile();
                tile[i][j].value=EMPTY;
                tile[i][j].flag=false;
                tile[i][j].open=false;
                isDrawAllMine=false;
            }

        }
    }

    /*
    建立所有方块，地雷和绿块
     */
    public void create(Point exception)
    {
        List<Point> allPoint=new LinkedList<Point>();

        for (int i = 0; i< numVer; i++)//y
        {
            for (int j = 0; j < numHor; j++)//x
            {
                Point point=new Point(j,i);
                if(!point.equals(exception))
                {
                    allPoint.add(point);//除了初始位置外都加入方块列表
                }
            }
        }

        List<Point> minePoint=new LinkedList<Point>();
        for (int i=0; i<mineNum; i++)
        {
            int idx=rd.nextInt(allPoint.size());
            minePoint.add(allPoint.get(idx));//添加进地雷列表
            allPoint.remove(idx);//从方块列表中移除
        }

        List<Point> greenPoint=new LinkedList<Point>();
        if(extendMode) {
            for (int i = 0; i < greenNum; i++) {
                int idx = rd.nextInt(minePoint.size());
                greenPoint.add(minePoint.get(idx));
                minePoint.remove(idx);//从地雷列表中移除
            }
        }

        for(Iterator<Point> it = minePoint.iterator(); it.hasNext();)
        {
            Point p=it.next();
            tile[p.y][p.x].value=MINE;//设定为地雷
        }

        for (Iterator<Point> it = greenPoint.iterator(); it.hasNext(); ) {
            Point g = it.next();
            tile[g.y][g.x].value = GREENPOINT;
        }

        //添加数字
        for (int i = 0; i< numVer; i++)//y
        {
            for (int j = 0; j< numHor; j++)//x
            {
                short t=tile[i][j].value;
                if(t==MINE||t==GREENPOINT)
                {
                    for (int k=0;k<8;k++)
                    {
                        int offsetX=j+dir[k][0],offsetY=i+dir[k][1];
                        if(offsetX>=0&&offsetX< numHor &&offsetY>=0&&offsetY< numVer ) {
                            if (tile[offsetY][offsetX].value != -1)
                                tile[offsetY][offsetX].value += 1;
                        }
                    }
                }
            }
        }

    }

    /*
    判断是否打开
     */
    public void open(Point op,boolean isFirst)
    {
        //判断是否是第一次打开
        if(isFirst)
        {
            create(op);
        }

        //如果是地雷或数字方块直接返回
        tile[op.y][op.x].open=true;
        if( tile[op.y][op.x].value==-1)
            return;
        else if( tile[op.y][op.x].value>0)
        {
            return;
        }
        //如果是空白方块就对8个方向判断并展开
        Queue<Point> qu=new LinkedList<Point>();
        qu.offer(new Point(op.x,op.y));
        for (int i=0;i<8;i++)
        {
            int offsetX=op.x+dir[i][0],offsetY=op.y+dir[i][1];
            boolean isCan=(offsetX>=0&&offsetX< numHor &&offsetY>=0&&offsetY< numVer);//判断周围的8个方块是否在地图内
            if(isCan)
            {
                if(tile[offsetY][offsetX].value==0 &&!tile[offsetY][offsetX].open) {
                    qu.offer(new Point(offsetX, offsetY));
                }
                else if(tile[offsetY][offsetX].value>0)
                {
                    tile[offsetY][offsetX].open=true;
                }
            }

        }
        //通过列表的增减对空白方块进行循环扩展，直到列表为空
        while(qu.size()!=0)
        {
            Point p=qu.poll();
            tile[p.y][p.x].open=true;
            for (int i=0;i<8;i++)
            {
                int offsetX=p.x+dir[i][0],offsetY=p.y+dir[i][1];
                boolean isCan=offsetX>=0&&offsetX< numHor &&offsetY>=0&&offsetY< numVer;
                if(isCan)
                {
                    if( tile[offsetY][offsetX].value==0&&!tile[offsetY][offsetX].open) {
                        qu.offer(new Point(offsetX, offsetY));
                    }
                    else if(tile[offsetY][offsetX].value>0)
                    {
                        tile[offsetY][offsetX].open=true;
                    }
                }

            }
        }

    }

    /*
    绘制各种符号和数字
     */
    public  void draw(Canvas canvas)
    {
        for (int i = 0; i< numVer; i++)
        {
            for (int j = 0; j< numHor; j++)
            {
                Tile t=tile[i][j];
                if(t.open){
                    //绘制数字
                    if(t.value>0)
                    {
                        canvas.drawText(t.value+"",x+j*tileWidth,y+i*tileWidth+tileWidth,numberPaint);
                    }
                    //绘制地雷
                    if(t.value==-2){
                            canvas.drawCircle((x + j * tileWidth) + tileWidth / 2, (y + i * tileWidth) + tileWidth / 2, tileWidth / 2, greenPaint);
                    }
                }else
                {
                    //标记
                    if(t.flag)
                    {
//                        Log.d("测试","已标记");
                        canvas.drawRect((x + j * tileWidth) + tileWidth / 4,(y + i * tileWidth) + tileWidth / 4,(x + j * tileWidth) + tileWidth / 4*3,(y + i * tileWidth) + tileWidth / 3*2, numberPaint);
                        canvas.drawRect((x + j * tileWidth) + tileWidth / 4*3,(y + i * tileWidth) + tileWidth / 4,(x + j * tileWidth) + tileWidth / 8*7,y + (i+1) * tileWidth, bmpPaint);
                    }else
                    {
                        RectF reactF=new RectF(x+j*tileWidth,y+i*tileWidth,x+j*tileWidth+tileWidth,y+i*tileWidth+tileWidth);
                        canvas.drawRoundRect(reactF,0,0, tilePaint);
                    }
                }
                //画出所有地雷
                if( isDrawAllMine&&tile[i][j].value==-1) {
                    canvas.drawCircle((x + j * tileWidth) + tileWidth / 2, (y + i * tileWidth) + tileWidth / 2, tileWidth / 2, bmpPaint);
                }
            }
        }
//        if(extendMode)canvas.drawText("已拥有的绿块 : "+haveGreen,x,y-tileWidth/5,textPaint);
        canvas.drawRect(tileWidth*9,0,tileWidth*10,tileWidth, tilePaint);
        canvas.drawText("F",9*tileWidth+tileWidth/5,tileWidth-tileWidth/6,numberPaint);//绘制标图案
        canvas.drawRect(x,y,x+mapWidth,y+mapHeight, rectPaint);//绘制地图大框架
        //画线
        for (int i = 0; i< numVer; i++) {
            canvas.drawLine(x,y+i*tileWidth,x+mapWidth,y+i*tileWidth, rectPaint);
        }
        for (int i = 0;i < numHor; i++) {
            canvas.drawLine(x+i*tileWidth,y,x+i*tileWidth,y+mapHeight, rectPaint);
        }
    }

}
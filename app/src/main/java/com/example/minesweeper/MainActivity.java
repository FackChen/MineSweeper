package com.example.minesweeper;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;

public class MainActivity extends Activity {
    public  static  int W;
    public  static  int H;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);//获取屏幕尺寸
        W = dm.widthPixels;//获取屏幕宽度
        H = dm.heightPixels ;//获取屏幕高度

        setContentView(new MainView(this));
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("扩展模式")
                .setMessage("是否开启扩展模式，在此模式下可以用绿色方块抵消一次伤害,但会对数字造成干扰")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setContentView(new ExtendMode(MainActivity.this));
                    }
                })
                .setNegativeButton("否",null)
                .show();

    }
}
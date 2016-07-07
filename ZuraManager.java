package com.homepetzura.ljy.zura;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Created by LJY on 16/2/24.
 */

public class ZuraManager {

    final static int ZURA_NORMAL=0;
    final static int ZURA_GUEST=1;
    final static int ZURA_SURPRISE=2;

    //status可选择是normal，还是guest，还是Surprise
    static int zura_status=ZURA_NORMAL;

    static boolean isMoving=false;
    static boolean isRelax=false;
    final static int[] relaxDuration={8000,10000,11500,2900};

    static int guestnumber=0;

    /*
    回到普通状态
     */
    public static void normal()
    {
        ImageView iv = (ImageView) ZuraService.zuraLayout.findViewById(R.id.zuraimg);
        iv.setImageResource(R.drawable.normal);
        zura_status=ZURA_NORMAL;
        isMoving=false;
        isRelax=false;
        guestnumber=0;
    }

    /*
    拖动操作激发move
     */
    public static void move()
    {
        if(!isMoving)
        {
            isMoving=true;
            ImageView iv = (ImageView) ZuraService.zuraLayout.findViewById(R.id.zuraimg);
            iv.setImageResource(R.drawable.move);
        }
    }

    /*
    单击操作触发react
     */
    public static void react()
    {
    }

    /*
    概率高，没事一段时间就会开始发呆卖萌relax
     */
    public static void relax()
    {
        if(zura_status==ZURA_NORMAL&&!isMoving)
        {
            isRelax=true;
            guestnumber=0;
            ImageView iv = (ImageView) ZuraService.zuraLayout.findViewById(R.id.zuraimg);
            int relaxnumber=(int)(Math.random()*relaxDuration.length)+1;
            switch (relaxnumber)
            {
                case 1:
                    iv.setImageResource(R.drawable.anime_relax_01);
                    break;
                case 2:
                    iv.setImageResource(R.drawable.anime_relax_02);
                    break;
                case 3:
                    iv.setImageResource(R.drawable.anime_relax_03);
                    break;
                case 4:
                    iv.setImageResource(R.drawable.anime_relax_04);
                    break;
            }
            AnimationDrawable frameAnimation = (AnimationDrawable) iv.getDrawable();
            frameAnimation.start();
            //播放完一段时间后要停止,然后
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(zura_status==ZURA_NORMAL)
                        normal();
                    isRelax=false;
                }
            }, relaxDuration[relaxnumber-1]+100);//不同的relax时间不同

        }

    }


    /*
    概率低，换装假发出场，或者别的角色客串guest
     */
    public static void guest()
    {
        if(zura_status==ZURA_NORMAL) {
            zura_status = ZURA_GUEST;
            //一堆要修改blabla
            ImageView iv = (ImageView) ZuraService.zuraLayout.findViewById(R.id.zuraimg);
            guestnumber=(int)(Math.random()*4)+1;//guest数为4
            switch (guestnumber)
            {
                case 1:
                    iv.setImageResource(R.drawable.guest_01);
                    break;
                case 2:
                    iv.setImageResource(R.drawable.guest_02);
                    break;
                case 3:
                    iv.setImageResource(R.drawable.guest_03);
                    break;
                case 4:
                    iv.setImageResource(R.drawable.guest_04);
                    break;
            }
            //一段时间guest后要回到normal
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(zura_status==ZURA_GUEST)
                        normal();
                }
            }, 40*1000);
        }
    }


    /*
    快速单击多次触发小惊喜
     */
    public static void surprise()
    {
        zura_status=ZURA_SURPRISE;
        guestnumber=0;
        /*一段时间的surprise状态*/
        //之后要回到normal
        ImageView iv = (ImageView) ZuraService.zuraLayout.findViewById(R.id.zuraimg);
        iv.setImageResource(R.drawable.surprise);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                normal();
            }
        }, 30*1000);
    }

}

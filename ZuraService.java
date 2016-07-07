package com.homepetzura.ljy.zura;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LJY on 16/2/24.
 */

public class ZuraService extends Service {
    WindowManager wm;
    /*
    该zuralayout对象的操作在Manager里进行设置，所以设为了static
     */
    static ZuraLayout zuraLayout;
    boolean isCreated=false;
    private Timer statusTimer;
    private int guestcount=0;
    static boolean isServiceOpen=false;

    final Handler h=new Handler(){
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                /*
                sleep一段时间后，在这里激发事件（guest）和（relax）
                 */
                case 1:
                    if(guestcount>=7) {//取5？或者取一个随机数字？
                        ZuraManager.guest();
                        guestcount=0;
                    }
                    else {
                        ZuraManager.relax();
                        guestcount++;
                    }
                    break;
            }
        }
    };

    public IBinder onBind(Intent i)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        isServiceOpen=true;
        wm=(WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        zuraLayout=new ZuraLayout(this);
        /*
        事件（guest）和（relax）的触发时间
         */
        statusTimer=new Timer();
        final TimerTask statusTimerTask = new TimerTask() {
            public void run() {
                Message m = new Message();
                m.what = 1;
                if(ZuraManager.zura_status==ZuraManager.ZURA_NORMAL)
                    h.sendMessage(m);
            }
        };
        statusTimer.schedule(statusTimerTask, 20 * 1000, 30 * 1000);//从现在起过20秒后，每隔30秒执行一次
    }

    public int onStartCommand(Intent i,int a,int b)
    {
        if(!isCreated)//如果还没有addview过
        {
            isCreated=true;
            wm.addView(zuraLayout, ZuraFloatParams.getWindowManagerLayoutParams
                    (zuraLayout.width/2 - (int) (64 * zuraLayout.contextscale + 0.5f),
                            zuraLayout.height,
                            zuraLayout.contextscale));//初始坐标
        }
        return super.onStartCommand(i,a,b);
    }
    public void onDestroy()
    {
        wm.removeView(zuraLayout);
        statusTimer.cancel();
        isServiceOpen=false;
        super.onDestroy();
    }

}

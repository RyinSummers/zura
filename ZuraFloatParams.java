package com.homepetzura.ljy.zura;

import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

/**
 * Created by LJY on 16/2/24.
 */

/*
该类用于存放悬浮窗的种种参数，全局公用。（反正唯一会改变的就是坐标而已。）
 */
public class ZuraFloatParams {

    public static WindowManager.LayoutParams getWindowManagerLayoutParams(int x,int y,float scale)
    {
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        lp.type =2002;//所有应用图标之上，状态栏之下
        lp.width= (int) (128 * scale + 0.5f);//注意：这个和分辨率有关系，要适配机型
        lp.height=(int) (128 * scale + 0.5f);
        lp.format = PixelFormat.RGBA_8888;//背景透明
        lp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.gravity = Gravity.LEFT | Gravity.TOP;//居左居上，方便计算坐标
            /*
            初始x,y也可以设置。再研究看看这个对象还有没有别的属性。
            blabla
             */
        lp.x=x;
        lp.y=y;
        return lp;
    }
}

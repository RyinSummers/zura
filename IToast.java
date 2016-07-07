package com.homepetzura.ljy.zura;

/**
 * Created by LJY on 16/6/1.
 */
import android.view.View;

/**
 * @author zhitao
 * @since 2016-01-21 14:30
 */
public interface IToast {

    IToast setGravity(int gravity, int xOffset, int yOffset);

    IToast setDuration(long durationMillis);

    IToast setView(View view);

    void show();

}

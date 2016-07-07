package com.homepetzura.ljy.zura;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LJY on 16/6/1.
 */
public class ZuraToast {

    private static Handler mHandler = new Handler();

    /**
     * 维护toast的队列
     */
    private static BlockingQueue<ZuraToast> mQueue = new LinkedBlockingQueue<ZuraToast>();

    /**
     * 原子操作：判断当前是否在读取{@linkplain #mQueue 队列}来显示toast
     */
    protected static AtomicInteger mAtomicInteger = new AtomicInteger(0);

    private WindowManager mWindowManager;

    private long mDurationMillis;

    private View mView;

    private WindowManager.LayoutParams mParams;

    private Context mContext;

    public ZuraToast(Context context) {

        mContext = context;
        mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);


        //设置toast的Params
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.windowAnimations = android.R.style.Animation_Toast;
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        // 默认小米Toast在下方居中
        mParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    }

    /**
     * Set the location at which the notification should appear on the screen.
     *
     * @param gravity
     * @param xOffset
     * @param yOffset
     */

    public void setGravity(int gravity, int xOffset, int yOffset) {

        // We can resolve the Gravity here by using the Locale for getting
        // the layout direction
        mParams.gravity = gravity;
        if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
            mParams.horizontalWeight = 1.0f;
        }
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
            mParams.verticalWeight = 1.0f;
        }
        mParams.y = yOffset;
        mParams.x = xOffset;
    }


    public void setDuration(long durationMillis) {
        if (durationMillis < 0) {
            mDurationMillis = 0;
        }
        if (durationMillis == Toast.LENGTH_SHORT) {
            mDurationMillis = 2000;
        } else if (durationMillis == Toast.LENGTH_LONG) {
            mDurationMillis = 3500;
        } else {
            mDurationMillis = durationMillis;
        }
    }

    public void setView(View view) {
        mView = view;
    }


    public void show() {
        // 1. 将本次需要显示的toast加入到队列中
        mQueue.offer(this);

        // 2. 如果队列还没有激活，就激活队列，依次展示队列中的toast
        if (0 == mAtomicInteger.get()) {
            mAtomicInteger.incrementAndGet();
            mHandler.post(mActivite);
        }
    }


    private void handleShow() {
        if (mView != null) {
            if (mView.getParent() != null) {
                mWindowManager.removeView(mView);
            }
            mWindowManager.addView(mView, mParams);
        }
    }

    private void handleHide() {
        if (mView != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mView.getParent() != null) {
                mWindowManager.removeView(mView);
                // 同时从队列中移除这个toast
                mQueue.poll();
            }
            mView = null;
        }
    }

    private static void activeQueue() {
        ZuraToast miuiToast = mQueue.peek();
        if (miuiToast == null) {
            // 如果不能从队列中获取到toast的话，那么就表示已经暂时完所有的toast了
            // 这个时候需要标记队列状态为：非激活读取中
            mAtomicInteger.decrementAndGet();
        } else {

            // 如果还能从队列中获取到toast的话，那么就表示还有toast没有展示
            // 1. 展示队首的toast
            // 2. 设置一定时间后主动采取toast消失措施
            // 3. 设置展示完毕之后再次执行本逻辑，以展示下一个toast
            mHandler.post(miuiToast.mShow);
            mHandler.postDelayed(miuiToast.mHide, miuiToast.mDurationMillis);
            mHandler.postDelayed(mActivite, miuiToast.mDurationMillis);
        }
    }

    private final Runnable mShow = new Runnable() {
        @Override
        public void run() {
            handleShow();
        }
    };

    private final Runnable mHide = new Runnable() {
        @Override
        public void run() {
            handleHide();
        }
    };

    private final static Runnable mActivite = new Runnable() {
        @Override
        public void run() {
            activeQueue();
        }
    };

}

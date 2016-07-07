package com.homepetzura.ljy.zura;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by LJY on 16/2/24.
 */
public class ZuraLayout extends LinearLayout {

    private WindowManager windowManager;

    private float mTouchStartX,mTouchStartY,DowninScreenX,DowninScreenY;

    ZuraToast reacttoast;

    View toastView;

    private long[] continuousClick=new long[6];
    private int clickcount=0;
    private boolean showtoast=true;
    private SharedPreferences pref;
    static String notestring;
//            {"好想吃甜食啊。","新八，快去开门。","好无聊啊，去买本JUMP看吧。",
//            "房租？……今天天气不错。","戳银桑干什么？","工作好麻烦啊……","  嗯？有委托吗？","快去干活啊混蛋！",
//            "好想偶遇结野主播啊，怎样才能偶遇结野主播呢？","昨晚好像喝多了……话说这里是哪里？","有人敲门吗？"};//共11条
    final static String[] reactSayings=
        {
                "不是假发，是桂！",
                "攘~夷~就~是~J~O~Y~♪",
                "几松殿的荞麦面味道越来越好了。",//桂几
                "至少作为同伴，我能与你分担痛苦。",//桂几
                "不管发生什么，都看着前方，向前奔跑吧。能做到吗？",
                "请去往去江户的黎明。",
                "让我们黎明再会吧Zzzzz……",
                "因为英雄在此！",
                "啊~肉球~等等我~",
                "从现在起，我就是你的左手。",//银桂
                "哈哈哈哈，再见了真选组！",//桂x真选组
                "总有一天我会逃出这个奇怪的盒子，等着瞧吧。",
                "你在叫我吗？",
                "刚才是什么东西在戳我？",
                "别碰我，感觉有点痒……",
                "别点了，让我歇一歇吧。",
                "别费劲了，再点我恐怕也没有用。",
                "说真的，并没有那种多戳几次就会变身的魔法。",
                "不要把我和卑劣的恐怖分子混为一谈。",
                "伊丽莎白长得真可爱啊，你觉得呢？",//伊桂
                "你看到我的伊丽莎白了吗？",
                "喜欢参加祭典也要有个限度。",//高桂
                "不是水果宾治武士，是桂。",
                "记得随身携带玉米浓汤味的美味棒。",
                "意大利烤肠味的美味棒是开会专用的。",
                "我明白，留下来的人则开创新时代，没有异议。"
        };
    final static String[] guestSayings_01=
            {
                    "不是假发，是桂！",
                    "我们一起来捏饭团吧？",
                    "要做武士的话，我想成为像那样自由自在的武士。",
                    "松下私塾的桂小太郎，参上！",
                    "哼哼，我可是当之无愧的硬脑袋No.1！",
                    "饭团的话，我只吃梅子的。",
                    "我要变得，连一起跨越黑夜的同伴也能够守护。"
            };
    final static String[] guestSayings_02=
            {
                    "不是假发，是桂！",
                    "为了国家，为了同伴……为了老师。举起剑吧。",
                    "银时！4500日元还我！！",
                    "除了正面进攻以外，撤退和整合的策略也非常重要。",
                    "……我和五月太夫只是坐着聊了聊人生而已！",
                    "不是桂滨，是桂！"
            };
    final static String[] guestSayings_03=
            {
                    "不是假发，是桂船长。",
                    "让我加入海盗吧！",
                    "看，这是我的简历。",
                    "不是假发，是宇宙船长桂。",
                    "驾驶的时候，一切都有可能发生……"
            };
    final static String[] guestSayings_04=
            {
                    "不是假发，是假发子。",
                    "比女人更骄傲，比男人更强壮，这是妈妈桑的口头禅。",
                    "不是大姐姐，是桂。",
                    "嗯？刚才点我陪酒的人，是你吗？",
                    "这位客人有什么事吗？"
            };
    private int lastsay=-1;
    float contextscale;
    int width;
    int height;
    long longpress=0;

    public ZuraLayout(Context c)
    {
        super(c);
        LayoutInflater.from(c).inflate(R.layout.layout_zura, this);
        windowManager=(WindowManager)c.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        toastView=LayoutInflater.from(c).inflate(R.layout.toast_example,(ViewGroup) findViewById(R.id.toast_relativelayout));
        reacttoast=new ZuraToast(c);
        reacttoast.setDuration(Toast.LENGTH_SHORT);
        contextscale=c.getResources().getDisplayMetrics().density;
        width=c.getResources().getDisplayMetrics().widthPixels;
        height=c.getResources().getDisplayMetrics().heightPixels;
        pref=c.getSharedPreferences("notefile",Context.MODE_PRIVATE);
        notestring=pref.getString("note","");
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        boolean actionMoved=false;
        switch (event.getAction()) {
            //记录初始坐标和放手后的坐标，如果不一致，更新坐标；如果不一致，激发事件（react）
            //如何激发事件surprise，依旧需要学习blabla
            case MotionEvent.ACTION_DOWN:
                //获取相对View的坐标，即以此View左上角为原点
                mTouchStartX =  event.getX();
                mTouchStartY =  event.getY();
                DowninScreenX = event.getRawX();
                DowninScreenY = event.getRawY();
                longpress=SystemClock.uptimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                //激发事件（move）
                if((longpress!=0&&SystemClock.uptimeMillis()-longpress>150)
                        ||Math.abs(DowninScreenX-event.getRawX())>=32
                        ||Math.abs(DowninScreenY-event.getRawY())>=32)
                {
                    if(ZuraManager.zura_status==ZuraManager.ZURA_NORMAL)
                        ZuraManager.move();//图片状态更新为move
                    actionMoved=true;//motionevent为move
                }
                updateViewPosition(event,actionMoved);
                break;
            case MotionEvent.ACTION_UP:
                    updateViewPosition(event,false);
                longpress=0;
                if(Math.abs(DowninScreenX-event.getRawX())<16
                        && Math.abs(DowninScreenY-event.getRawY())<16)
                //如果是单击
                {
                    long temp = SystemClock.uptimeMillis();

                    //是否显示toast
                    if(showtoast)
                    {
                        TextView reacttext = (TextView) toastView.findViewById(R.id.text_react);
                        //toast文字内容
                        if(pref.getBoolean("ison",false))
                        {
                            notestring=pref.getString("note","殿下您的记事本似乎什么也没有？");
                            reacttext.setText(notestring);
                        }
                        else
                        {
                            if (ZuraManager.guestnumber == 1) {
                                int sayingnumber = (int) (Math.random() * guestSayings_01.length);
                                while (sayingnumber == lastsay)
                                    sayingnumber = (int) (Math.random() * guestSayings_01.length);
                                reacttext.setText(guestSayings_01[sayingnumber]);
                                lastsay = sayingnumber;
                            } else if (ZuraManager.guestnumber == 2) {
                                int sayingnumber = (int) (Math.random() * guestSayings_02.length);
                                while (sayingnumber == lastsay)
                                    sayingnumber = (int) (Math.random() * guestSayings_02.length);
                                reacttext.setText(guestSayings_02[sayingnumber]);
                                lastsay = sayingnumber;
                            }
                            else if (ZuraManager.guestnumber == 3) {
                                int sayingnumber = (int) (Math.random() * guestSayings_03.length);
                                while (sayingnumber == lastsay)
                                    sayingnumber = (int) (Math.random() * guestSayings_03.length);
                                reacttext.setText(guestSayings_03[sayingnumber]);
                                lastsay = sayingnumber;
                            }
                            else if (ZuraManager.guestnumber == 4) {
                                int sayingnumber = (int) (Math.random() * guestSayings_04.length);
                                while (sayingnumber == lastsay)
                                    sayingnumber = (int) (Math.random() * guestSayings_04.length);
                                reacttext.setText(guestSayings_04[sayingnumber]);
                                lastsay = sayingnumber;
                            }else {
                                int sayingnumber = (int) (Math.random() * reactSayings.length);
                                while (sayingnumber == lastsay)
                                    sayingnumber = (int) (Math.random() * reactSayings.length);
                                reacttext.setText(reactSayings[sayingnumber]);
                                lastsay = sayingnumber;
                            }
                        }
                        reacttoast.setView(toastView);
                        reacttoast.show();
                        showtoast=false;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                showtoast=true;
                            }
                        }, 2000);
                    }

                    //是否切换surprise
                    if (clickcount > 0 && temp - continuousClick[clickcount - 1] <= 300) {
                        continuousClick[clickcount] = temp;
                        clickcount++;
                    } else if (clickcount == 0) {
                        continuousClick[clickcount] = temp;
                        clickcount++;
                    } else {
                        clickcount = 0;
                        continuousClick[clickcount] = temp;
                        clickcount++;
                    }

                    if (clickcount == 6) {
                        ZuraManager.surprise();
                        clickcount = 0;
                        continuousClick = new long[6];
                    }
                }
                mTouchStartX=mTouchStartY=0;
                if(ZuraManager.isMoving && ZuraManager.zura_status==ZuraManager.ZURA_NORMAL)
                {
                            ZuraManager.normal();

                    /*
                    else if(ZuraManager.zura_status==ZuraManager.ZURA_GUEST)
                        ZuraManager.guest();
                    else if(ZuraManager.zura_status==ZuraManager.ZURA_SURPRISE)
                        ZuraManager.surprise();
                        */
                }
                break;
        }
        return true;
    }

    private void updateViewPosition(MotionEvent event,boolean actionMove)
    {
        int x = (int) (event.getRawX() - mTouchStartX);
        int y = (int) (event.getRawY() - mTouchStartY);//-getStatusBarHeight();//减去状态栏高度，然而有时候会没有状态栏
        if(actionMove) {
            windowManager.updateViewLayout(this, ZuraFloatParams.getWindowManagerLayoutParams(x, y, contextscale));
        }
        reacttoast.setGravity(Gravity.LEFT | Gravity.TOP,x,y + (int) (100 * contextscale + 0.5f));
    }

    private int getStatusBarHeight()
    {
        int statusBarHeight=0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

}

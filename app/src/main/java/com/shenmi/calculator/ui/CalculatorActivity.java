package com.shenmi.calculator.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shenmi.calculator.R;
import com.shenmi.calculator.bean.InputItem;
import com.shenmi.calculator.util.AppMarketUtil;
import com.shenmi.calculator.util.AudioUtils;
import com.shenmi.calculator.util.SPUtil;
import com.shenmi.calculator.util.SharedPUtils;
import com.shenmi.calculator.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class CalculatorActivity extends Activity implements View.OnClickListener {

    private TextView mShowResultTvTwo;  //��ʾ���
    private TextView mShowResultTv;  //��ʾ���
    private TextView mShowInputTv;   //��ʾ������ַ�
    private Button mCBtn;
    private ImageView mDelBtn;
    private Button mAddBtn;
    private Button mSubBtn;
    private Button mMultiplyBtn;
    private Button mDividebtn;
    private Button mZeroButton;
    private Button mOnebtn;
    private Button mTwoBtn;
    private Button mThreeBtn;
    private Button mFourBtn;
    private Button mFiveBtn;
    private Button mSixBtn;
    private Button mSevenBtn;
    private Button mEightBtn;
    private Button mNineBtn;
    private Button mPointtn;
    private Button mEqualBtn;
    private Button mpercent;
    private HashMap<View, String> map; //��View��Stringӳ������
    private List<InputItem> mInputList; //�����¼ÿ���������
    private int mLastInputstatus = INPUT_NUMBER; //��¼��һ������״̬
    public static final int INPUT_NUMBER = 1;
    public static final int INPUT_POINT = 0;
    public static final int INPUT_OPERATOR = -1;
    public static final int END = -2;
    public static final int ERROR = -3;
    public static final int SHOW_RESULT_DATA = 1;
    public static final int SHOW_RESULT_DATA2 = 2;
    public static final String nan = "NaN";
    public static final String infinite = "��";

    //基础，科学
    private TextView title_jc, title_KX;
    //震动,声音
    private ImageView music_icon, shake_icon;
    private boolean isfalse_muisc, isfalse_shake;

    private SoundPool mSoundPool;
    private Map<Integer, Integer> mSoundResource;
    private AudioManager mAudioManager;
    private int mSoundStreamId;
    //震动
    private Vibrator vibrator = null;
    private boolean isFrist; //表示是否是第一次进入页面

    //基础布局和科学布局
    private LinearLayout kexue_menu, jichu_menu;
    private LinearLayout kexue_result_menu, jishu_result_menu;

    private Button[] btn = new Button[12];// 0~9 + E + pi 十二个数字
    private TextView tvShow;// 用于显示输出结果
    private Button div, mul, sub, add, equal, sin, cos, tan, log, ln, sqrt,
            square, factorial, back, left, right, point, clear;
    public String str_old;
    public String str_new;
    public boolean vbegin = true;// 控制输入，true为重新输入，false为接着输入
    public double pi = 4 * Math.atan(1);// π值
    public boolean tip_lock = true;// true为正确，可以继续输入，false错误，输入锁定
    public boolean equals_flag = true;// 是否在按下=之后输入，true为之前，false为之后

    private List<Button> btnList;
    private TextView prefix;

    private String result;//3*0.1问题结果
    int times;//三次计算成功跳转应用市场
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            if (msg.what == SHOW_RESULT_DATA) {
                if (mInputList != null && mInputList.size() > 0) {
                    mShowResultTvTwo.setText(mShowResultTv.getText() + "");
                    mShowResultTv.setText(mShowInputTv.getText());
                    mShowInputTv.setText(mInputList.get(0).getInput());
                    AudioUtils.getInstance().speakText(mInputList.get(0).getInput()); //播放语音
                    clearScreen(mInputList.get(0));
                }
            }

            if (msg.what == SHOW_RESULT_DATA2) {
                if (mInputList != null && mInputList.size() > 0) {
                    mShowResultTvTwo.setText(mShowResultTv.getText() + "");
                    mShowResultTv.setText(mShowInputTv.getText());
                    mShowInputTv.setText(result);
                    AudioUtils.getInstance().speakText(result); //播放语音
                    clearScreen(mInputList.get(0));
                }
            }
        }
    };
    private ImageView mIb_back;
    private AudioManager mAm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calculator);
        initView();
        initData();
        MobclickAgent.onEvent(this, "Calculator_science");
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * ��ʼ��view
     */
    private void initView() {

        new Thread() {
            @Override
            public void run() {
                initMusic();
            }
        }.start();
        tvShow = findViewById(R.id.inputOrOutput);
        prefix = findViewById(R.id.prefix);
        jishu_result_menu = findViewById(R.id.jichu_result_menu);
        kexue_result_menu = findViewById(R.id.kexue_result_menu);
        div = findViewById(R.id.division);
        mul = findViewById(R.id.mutiply);
        sub = findViewById(R.id.Subtraction);
        add = findViewById(R.id.add);
        equal = findViewById(R.id.equal);
        sin = findViewById(R.id.sin);
        cos = findViewById(R.id.cos);
        tan = findViewById(R.id.tan);
        log = findViewById(R.id.lg);
        ln = findViewById(R.id.ln);
        sqrt = findViewById(R.id.sqrt);
        square = findViewById(R.id.square);
        factorial = findViewById(R.id.factorial);
        back = findViewById(R.id.back);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        point = findViewById(R.id.point);
        clear = findViewById(R.id.clear);
        btn[0] = findViewById(R.id.zero);
        btn[1] = findViewById(R.id.one);
        btn[2] = findViewById(R.id.two);
        btn[3] = findViewById(R.id.three);
        btn[4] = findViewById(R.id.four);
        btn[5] = findViewById(R.id.five);
        btn[6] = findViewById(R.id.six);
        btn[7] = findViewById(R.id.seven);
        btn[8] = findViewById(R.id.eight);
        btn[9] = findViewById(R.id.nine);
        btn[10] = findViewById(R.id.e);
        btn[11] = findViewById(R.id.pi);
        kexue_menu = findViewById(R.id.kexue_menu);
        jichu_menu = findViewById(R.id.jichu_menu);
        music_icon = findViewById(R.id.music_icon);
        shake_icon = findViewById(R.id.shake_icon);
        title_jc = findViewById(R.id.title_jc);
        title_KX = findViewById(R.id.title_kx);
        mShowResultTv = this.findViewById(R.id.show_result_tv);
        mShowResultTvTwo = this.findViewById(R.id.show_result_tv_two);
        mShowInputTv = this.findViewById(R.id.show_input_tv);
        mCBtn = this.findViewById(R.id.c_btn);
        mDelBtn = this.findViewById(R.id.del_btn);
        mAddBtn = this.findViewById(R.id.add_btn);
        mMultiplyBtn = this.findViewById(R.id.multiply_btn);
        mDividebtn = this.findViewById(R.id.divide_btn);
        mZeroButton = this.findViewById(R.id.zero_btn);
        mOnebtn = this.findViewById(R.id.one_btn);
        mTwoBtn = this.findViewById(R.id.two_btn);
        mThreeBtn = this.findViewById(R.id.three_btn);
        mFourBtn = this.findViewById(R.id.four_btn);
        mFiveBtn = this.findViewById(R.id.five_btn);
        mSixBtn = this.findViewById(R.id.six_btn);
        mSevenBtn = this.findViewById(R.id.seven_btn);
        mEightBtn = this.findViewById(R.id.eight_btn);
        mNineBtn = this.findViewById(R.id.nine_btn);
        mPointtn = this.findViewById(R.id.point_btn);
        mEqualBtn = this.findViewById(R.id.equal_btn);
        mSubBtn = this.findViewById(R.id.sub_btn);
        mpercent = this.findViewById(R.id.percent);
        mIb_back = this.findViewById(R.id.ib_back);
        setOnClickListener();

    }

    /**
     * 初始化音频
     */
    public void initMusic() {
        mSoundResource = new HashMap<>();
        if (mSoundPool == null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes;
                audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

                mSoundPool = new SoundPool.Builder()
                        .setMaxStreams(1)
                        .setAudioAttributes(audioAttributes)
                        .build();
            } else { // 5.0 以前
                mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);  // 创建SoundPool
            }
            // 设置加载完成监听
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    Log.i("snmilog", "音频加载完成");
                }
            });
        }
        if (mSoundPool != null) {
            try {
                mSoundResource.put(R.id.zero_btn, mSoundPool.load(this, R.raw.num0, 1));
                mSoundResource.put(R.id.one_btn, mSoundPool.load(this, R.raw.num1, 1));
                mSoundResource.put(R.id.two_btn, mSoundPool.load(this, R.raw.num2, 1));
                mSoundResource.put(R.id.three_btn, mSoundPool.load(this, R.raw.num3, 1));
                mSoundResource.put(R.id.four_btn, mSoundPool.load(this, R.raw.num4, 1));
                mSoundResource.put(R.id.five_btn, mSoundPool.load(this, R.raw.num5, 1));
                mSoundResource.put(R.id.six_btn, mSoundPool.load(this, R.raw.num6, 1));
                mSoundResource.put(R.id.seven_btn, mSoundPool.load(this, R.raw.num7, 1));
                mSoundResource.put(R.id.eight_btn, mSoundPool.load(this, R.raw.num8, 1));
                mSoundResource.put(R.id.nine_btn, mSoundPool.load(this, R.raw.num9, 1));
                mSoundResource.put(R.id.equal_btn, mSoundPool.load(this, R.raw.dengyu, 1));
                mSoundResource.put(R.id.add_btn, mSoundPool.load(this, R.raw.jia, 1));
                mSoundResource.put(R.id.sub_btn, mSoundPool.load(this, R.raw.jian, 1));
                mSoundResource.put(R.id.multiply_btn, mSoundPool.load(this, R.raw.chengyi, 1));
                mSoundResource.put(R.id.divide_btn, mSoundPool.load(this, R.raw.chuyi, 1));
                mSoundResource.put(R.id.del_btn, mSoundPool.load(this, R.raw.huitui, 1));
                mSoundResource.put(R.id.point_btn, mSoundPool.load(this, R.raw.dian, 1));
                mSoundResource.put(R.id.percent, mSoundPool.load(this, R.raw.baifenhao, 1));
                mSoundResource.put(R.id.c_btn, mSoundPool.load(this, R.raw.qingchu, 1));

                mSoundResource.put(R.id.zero, mSoundPool.load(this, R.raw.num0, 1));
                mSoundResource.put(R.id.one, mSoundPool.load(this, R.raw.num1, 1));
                mSoundResource.put(R.id.two, mSoundPool.load(this, R.raw.num2, 1));
                mSoundResource.put(R.id.three, mSoundPool.load(this, R.raw.num3, 1));
                mSoundResource.put(R.id.four, mSoundPool.load(this, R.raw.num4, 1));
                mSoundResource.put(R.id.five, mSoundPool.load(this, R.raw.num5, 1));
                mSoundResource.put(R.id.six, mSoundPool.load(this, R.raw.num6, 1));
                mSoundResource.put(R.id.seven, mSoundPool.load(this, R.raw.num7, 1));
                mSoundResource.put(R.id.eight, mSoundPool.load(this, R.raw.num8, 1));
                mSoundResource.put(R.id.nine, mSoundPool.load(this, R.raw.num9, 1));
                mSoundResource.put(R.id.equal, mSoundPool.load(this, R.raw.dengyu, 1));
                mSoundResource.put(R.id.add, mSoundPool.load(this, R.raw.jia, 1));
                mSoundResource.put(R.id.Subtraction, mSoundPool.load(this, R.raw.jian, 1));
                mSoundResource.put(R.id.mutiply, mSoundPool.load(this, R.raw.chengyi, 1));
                mSoundResource.put(R.id.division, mSoundPool.load(this, R.raw.chuyi, 1));
                mSoundResource.put(R.id.back, mSoundPool.load(this, R.raw.huitui, 1));
                mSoundResource.put(R.id.point, mSoundPool.load(this, R.raw.dian, 1));
                mSoundResource.put(R.id.clear, mSoundPool.load(this, R.raw.qingchu, 1));
                mSoundResource.put(R.id.left, mSoundPool.load(this, R.raw.zuokuohao, 1));
                mSoundResource.put(R.id.right, mSoundPool.load(this, R.raw.youkuohao, 1));

                mSoundResource.put(R.id.pi, mSoundPool.load(this, R.raw.w, 1));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("smnilog", "音频加载失败");
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        times = (int) SPUtil.get(CalculatorActivity.this, "calculatorSuccessTimes", 0);

        if (map == null)
            map = new HashMap<>();
        map.put(mAddBtn, getResources().getString(R.string.add));
        map.put(mMultiplyBtn, getResources().getString(R.string.multply));
        map.put(mDividebtn, getResources().getString(R.string.divide));
        map.put(mSubBtn, getResources().getString(R.string.sub));
        map.put(mZeroButton, getResources().getString(R.string.zero));
        map.put(mOnebtn, getResources().getString(R.string.one));
        map.put(mTwoBtn, getResources().getString(R.string.two));
        map.put(mThreeBtn, getResources().getString(R.string.three));
        map.put(mFourBtn, getResources().getString(R.string.four));
        map.put(mFiveBtn, getResources().getString(R.string.five));
        map.put(mSixBtn, getResources().getString(R.string.six));
        map.put(mSevenBtn, getResources().getString(R.string.seven));
        map.put(mEightBtn, getResources().getString(R.string.eight));
        map.put(mNineBtn, getResources().getString(R.string.nine));
        map.put(mPointtn, getResources().getString(R.string.point));
        map.put(mEqualBtn, getResources().getString(R.string.equal));
        map.put(mpercent, getResources().getString(R.string.percent));
        mInputList = new ArrayList<>();
        mShowResultTv.setText("");
        mShowResultTvTwo.setText("");
        tvShow.setText("0");
        clearAllScreen();

        btnList = new ArrayList<>();

        for (int i = 0; i < btn.length; i++) {
            btnList.add(btn[i]);
        }
        btnList.add(div);
        btnList.add(mul);
        btnList.add(sub);
        btnList.add(add);
        btnList.add(equal);
        btnList.add(sin);
        btnList.add(cos);
        btnList.add(tan);
        btnList.add(log);
        btnList.add(ln);
        btnList.add(sqrt);
        btnList.add(square);
        btnList.add(factorial);
        btnList.add(back);
        btnList.add(left);
        btnList.add(right);
        btnList.add(point);
        btnList.add(clear);

        for (int i = 0; i < btnList.size(); i++) {
            //给每一个按钮绑定监听事件
            setListenerForEachButton(btnList.get(i));
        }

        AudioUtils.getInstance().init(this); //初始化语音对象
        //创建vibrator对象
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        isfalse_shake = SharedPUtils.getShake(this);
        isfalse_muisc = SharedPUtils.getMusic(this);

        if (isfalse_muisc) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            music_icon.setImageResource(R.drawable.nomusic_icon);
        } else {
//            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);
            //tempVolume:音量绝对值
            music_icon.setImageResource(R.drawable.music_icon);
        }

        if (isfalse_shake) {
            isFrist = true;
            shake_icon.setImageResource(R.drawable.noshake_icon);
        } else {
            //震动
            isFrist = true;
            shake_icon.setImageResource(R.drawable.shake_icon);
        }

    }


    //给每一个按钮绑定监听事件
    private void setListenerForEachButton(final Button btn) {
        btn.setOnClickListener(actionPerformed);
    }

    /**
     * 键盘命令捕捉
     */
    String[] TipCommand = new String[500];
    int tip_i = 0;// TipCommand的指针
    private View.OnClickListener actionPerformed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e("onClick", "onClick科学");
            // 按键上的命令获取
            String command = ((Button) view).getText().toString();
            // 获取显示器上的字符串
            String str = tvShow.getText().toString();
            // 检测输入是否合法
            if (equals_flag == false && "0123456789Eπ.()sincostanlnlogn!+-×÷√^".indexOf(command) != -1) {   //在按 = 之后输入，不是的话，直接跳过
                // 检测显示器上的字符串是否合法
                if (checkStr(str)) {  //——>检测显示器上的字符是否合法，true为合法
                    if ("+-×÷√^)".indexOf(command) != -1) {
                        for (int i = 0; i < str.length(); i++) {
                            TipCommand[tip_i] = String.valueOf(str.charAt(i));
                            tip_i++;  // TipCommand的指针
                        }
                        vbegin = false;//控制输入 ——>false为接着输入
                    }
                } else {
                    tvShow.setText("0");
                    prefix.setText("");
                    vbegin = true;//重新输入
                    tip_i = 0;
                    tip_lock = true;
                }
                equals_flag = true;
            }

            //给按键增加声音
            switch (command) {
                case "π":
                case "√":
                case "^":
                case "n!":
                case "sin":
                case "cos":
                case "tan":
                case "ln":
                case "log":
                case "E":
                    playSound(R.id.pi);
                    break;
                case "0":
                    playSound(R.id.zero);
                    break;
                case "1":
                    playSound(R.id.one);
                    break;
                case "2":
                    playSound(R.id.two);
                    break;
                case "3":
                    playSound(R.id.three);
                    break;
                case "4":
                    playSound(R.id.four);
                    break;
                case "5":
                    playSound(R.id.five);
                    break;
                case "6":
                    playSound(R.id.six);
                    break;
                case "7":
                    playSound(R.id.seven);
                    break;
                case "8":
                    playSound(R.id.eight);
                    break;
                case "9":
                    playSound(R.id.nine);
                    break;
                case ".":
                    playSound(R.id.point);
                    break;
                case "+":
                    playSound(R.id.add);
                    break;
                case "-":
                    playSound(R.id.Subtraction);
                    break;
                case "×":
                    playSound(R.id.mutiply);
                    break;
                case "÷":
                    playSound(R.id.division);
                    break;
                case "(":
                    playSound(R.id.left);
                    break;
                case ")":
                    playSound(R.id.right);
                    break;
            }

            if (tip_i > 0) // TipCommand的指针
                TipChecker(TipCommand[tip_i - 1], command);
            else if (tip_i == 0) {
                TipChecker("#", command);//当第一次输入按键的时候，检查是否输入合法
            }
            if ("0123456789Eπ.()sincostanlnlogn!+-×÷√^".indexOf(command) != -1
                    && tip_lock) {//tip_lock 可以持续输入
                TipCommand[tip_i] = command;
                tip_i++;
            }
            // 如果输入正确，就将输入信息显示到显示器上
            if ("0123456789Eπ.()sincostanlnlogn!+-×÷√^".indexOf(command) != -1
                    && tip_lock) { // 共25个按键
                show(command);
                // 如果输入时退格键，并且是在按=之前
            } else if (command.compareTo("←") == 0 && equals_flag) {
                playSound(R.id.back);
                // 一次删除3个字符
                if (delChar(str) == 3) {
                    if (str.length() > 3)
                        tvShow.setText(str.substring(0, str.length() - 3));
                    else if (str.length() == 3) {
                        tvShow.setText("0");
                        prefix.setText("");
                        vbegin = true;
                        tip_i = 0;
                    }
                    // 依次删除2个字符
                } else if (delChar(str) == 2) {
                    if (str.length() > 2)
                        tvShow.setText(str.substring(0, str.length() - 2));
                    else if (str.length() == 2) {
                        tvShow.setText("0");
                        prefix.setText("");
                        vbegin = true;
                        tip_i = 0;
                    }
                    // 依次删除一个字符
                } else if (delChar(str) == 1) {
                    // 若之前输入的字符串合法则删除一个字符
                    if (checkStr(str)) {
                        if (str.length() > 1)
                            tvShow.setText(str.substring(0, str.length() - 1));
                        else if (str.length() == 1) {
                            tvShow.setText("0");
                            prefix.setText("");
                            vbegin = true;
                            tip_i = 0;
                        }
                        // 若之前输入的字符串不合法则删除全部字符
                    } else {
                        tvShow.setText("0");
                        prefix.setText("");
                        vbegin = true;
                        tip_i = 0;
                    }
                }
                if (tvShow.getText().toString().compareTo("-") == 0
                        || equals_flag == false) {
                    tvShow.setText("0");
                    prefix.setText("");
                    vbegin = true;
                    tip_i = 0;
                }
                tip_lock = true;
                if (tip_i > 0)
                    tip_i--;
                // 如果是在按=之后输入退格键
            } else if (command.compareTo("←") == 0 && equals_flag == false) {
                playSound(R.id.back);
                // 将显示器内容设置为0
                tvShow.setText("0");
                prefix.setText("");
                vbegin = true;
                tip_i = 0;
                tip_lock = true;
                // 如果输入的是清除键
            } else if (command.compareTo("C") == 0) {
                playSound(R.id.clear);
                // 将显示器内容设置为0
                tvShow.setText("0");
                prefix.setText("");
                // 重新输入标志置为true
                vbegin = true;
                // 缓存命令位数清0
                tip_i = 0;
                // 表明可以继续输入
                tip_lock = true;
                // 表明输入=之前
                equals_flag = true;
                // 如果输入的是=号，并且输入合法
            } else if (command.compareTo("=") == 0 && tip_lock && checkStr(str)
                    && equals_flag) {
                playSound(R.id.equal);
                tip_i = 0; // TipCommand的指针
                // 表明不可以继续输入
                tip_lock = false;
                // 表明输入=之后
                equals_flag = false;
                // 保存原来算式样子
                str_old = str;
                // 替换算式中的运算符，便于计算
                str = str.replaceAll("sin", "s");
                str = str.replaceAll("cos", "c");
                str = str.replaceAll("tan", "t");
                str = str.replaceAll("log", "g");
                str = str.replaceAll("ln", "l");
                str = str.replaceAll("n!", "!");
                // 重新输入标志设置true
                vbegin = true;
                // 将-1x转换成-
                str_new = str.replaceAll("-", "-1×");
                // 计算算式结果
                try {
                    new calc().process(str_new, "1");
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(CalculatorActivity.this, "超出最大计算长度或输入有误");
                }
                //成功计算三次后提示应用市场评价
                if (times <= 2) {
                    if (times == 2) {
                        AppMarketUtil.goThirdApp(CalculatorActivity.this);
                    }
                    SPUtil.put(CalculatorActivity.this, "calculatorSuccessTimes", ++times);
                }
            }
            // 表明可以继续输入
            tip_lock = true;
        }
    };


    /*
     * 检测函数，对str进行前后语法检测 为Tip的提示方式提供依据，与TipShow()配合使用 编号 字符 其后可以跟随的合法字符 1 （
     * 数字|（|-|.|函数 2 ） 算符|）|√ ^ 3 . 数字|算符|）|√ ^ 4 数字 .|数字|算符|）|√ ^ 5 算符
     * 数字|（|.|函数 6 √ ^ （ |. | 数字 7 函数 数字|（|.
     *
     * 小数点前后均可省略，表示0 数字第一位可以为0
     */
    private void TipChecker(String tipcommand1, String tipcommand2) {
        // Tipcode1表示错误类型,0 表示没有错误类型
        int Tipcode1 = 0;
        // 表示命令类型
        int tiptype1 = 0, tiptype2 = 0;
        // 括号数
        int bracket = 0;
        // “+-x÷√^”不能作为第一位
        //这里是第一次在键盘上面按钮的时候进入的逻辑
        if (tipcommand1.compareTo("#") == 0
                && (tipcommand2.compareTo("÷") == 0
                || tipcommand2.compareTo("×") == 0
                || tipcommand2.compareTo("+") == 0
                || tipcommand2.compareTo(")") == 0
                || tipcommand2.compareTo("^") == 0)) {
            Tipcode1 = -1;
        }
        // 定义存储字符串中最后一位的类型
        //第一次输入之后进入的逻辑界面
        else if (tipcommand1.compareTo("#") != 0) {
            if (tipcommand1.compareTo("(") == 0) {
                tiptype1 = 1;
            } else if (tipcommand1.compareTo(")") == 0) {
                tiptype1 = 2;
            } else if (tipcommand1.compareTo(".") == 0) {
                tiptype1 = 3;
            } else if ("0123456789".indexOf(tipcommand1) != -1) {
                tiptype1 = 4;
            } else if ("+-×÷".indexOf(tipcommand1) != -1) {
                tiptype1 = 5;
            } else if ("^".indexOf(tipcommand1) != -1) {
                tiptype1 = 6;
            } else if ("sincostanloglnn!√".indexOf(tipcommand1) != -1) {
                tiptype1 = 7;
            } else if ("Eπ".indexOf(tipcommand1) != -1) {
                tiptype1 = 8;
            }
            // 定义欲输入的按键类型
            if (tipcommand2.compareTo("(") == 0) {
                tiptype2 = 1;
            } else if (tipcommand2.compareTo(")") == 0) {
                tiptype2 = 2;
            } else if (tipcommand2.compareTo(".") == 0) {
                tiptype2 = 3;
            } else if ("0123456789".indexOf(tipcommand2) != -1) {
                tiptype2 = 4;
            } else if ("+-×÷".indexOf(tipcommand2) != -1) {
                tiptype2 = 5;
            } else if ("^".indexOf(tipcommand2) != -1) {
                tiptype2 = 6;
            } else if ("sincostanloglnn!√".indexOf(tipcommand2) != -1) {
                tiptype2 = 7;
            } else if ("Eπ".indexOf(tipcommand2) != -1) {
                tiptype2 = 8;
            }
            switch (tiptype1) {
                case 1:
                    // 左括号后面直接接右括号,“+x÷”（负号“-”不算）,或者"√^"
                    if (tiptype2 == 2
                            || (tiptype2 == 5 && tipcommand2.compareTo("-") != 0)
                            || tiptype2 == 6)
                        Tipcode1 = 1;
                    break;
                case 2:
                    // 右括号后面接左括号，数字，“小数点”，“sincostanloglnn!”，“Eπ”
                    if (tiptype2 == 1 || tiptype2 == 3 || tiptype2 == 4
                            || tiptype2 == 7 || tiptype2 == 8)
                        Tipcode1 = 2;
                    break;
                case 3:
                    // “.”后面接左括号或者“sincos...”, “Eπ”
                    if (tiptype2 == 1 || tiptype2 == 7 || tiptype2 == 8)
                        Tipcode1 = 3;
                    // 连续输入两个“.”
                    if (tiptype2 == 3)
                        Tipcode1 = 8;
                    break;
                case 4:
                    // 数字后面直接接左括号或者“sincosEπ”
                    if (tiptype2 == 1 || tiptype2 == 7 || tiptype2 == 8)
                        Tipcode1 = 4;
                    break;
                case 5:
                    // “+-x÷”后面直接接右括号，“+-x÷√^”
                    if (tiptype2 == 2 || tiptype2 == 5 || tiptype2 == 6)
                        Tipcode1 = 5;
                    break;
                case 6:
                    // “√^”后面直接接右括号，“+-x÷√^”以及“sincos...”
                    if (tiptype2 == 2 || tiptype2 == 5 || tiptype2 == 6
                            || tiptype2 == 7)
                        Tipcode1 = 6;
                    break;
                case 7:
                    // “sincos...”后面直接接右括号“+-x÷√^”以及“sincos...”
                    if (tiptype2 == 2 || tiptype2 == 5 || tiptype2 == 6
                            || tiptype2 == 7)
                        Tipcode1 = 7;
                    break;
                case 8:
                    //"Eπ"后面跟 左括号， 小数点， 数字，sincos...， Eπ
                    if (tiptype2 == 1 || tiptype2 == 3 || tiptype2 == 4
                            || tiptype2 == 7 || tiptype2 == 8)
                        Tipcode1 = 8;
                    break;
            }
        }
        // 检测小数点的重复性，Tipconde1=0,表明满足前面的规则
        if (Tipcode1 == 0 && tipcommand2.compareTo(".") == 0) {
            int tip_point = 0;
            for (int i = 0; i < tip_i; i++) {
                // 若之前出现一个小数点点，则小数点计数加1
                if (TipCommand[i].compareTo(".") == 0) {
                    tip_point++;
                }
                // 若出现以下几个运算符之一，小数点计数清零
                if (TipCommand[i].compareTo("sin") == 0
                        || TipCommand[i].compareTo("cos") == 0
                        || TipCommand[i].compareTo("tan") == 0
                        || TipCommand[i].compareTo("log") == 0
                        || TipCommand[i].compareTo("ln") == 0
                        || TipCommand[i].compareTo("n!") == 0
                        || TipCommand[i].compareTo("√") == 0
                        || TipCommand[i].compareTo("^") == 0
                        || TipCommand[i].compareTo("÷") == 0
                        || TipCommand[i].compareTo("×") == 0
                        || TipCommand[i].compareTo("-") == 0
                        || TipCommand[i].compareTo("+") == 0
                        || TipCommand[i].compareTo("(") == 0
                        || TipCommand[i].compareTo(")") == 0) {
                    tip_point = 0;
                }
            }
            tip_point++;
            // 若小数点计数大于1，表明小数点重复了
            if (tip_point > 1) {
                Tipcode1 = 8;
            }
        }

        // 检测右括号是否匹配
        if (Tipcode1 == 0 && tipcommand2.compareTo(")") == 0) {
            int tip_right_bracket = 0;
            for (int i = 0; i < tip_i; i++) {
                // 如果出现一个左括号，则计数加1
                if (TipCommand[i].compareTo("(") == 0) {
                    tip_right_bracket++;
                }
                // 如果出现一个右括号，则计数减1
                if (TipCommand[i].compareTo(")") == 0) {
                    tip_right_bracket--;
                }
            }
            // 如果右括号计数=0,表明没有响应的左括号与当前右括号匹配
            if (tip_right_bracket == 0) {
                Tipcode1 = 10;
            }
        }
        // 检查输入=的合法性
        if (Tipcode1 == 0 && tipcommand2.compareTo("=") == 0) {
            // 括号匹配数
            int tip_bracket = 0;
            for (int i = 0; i < tip_i; i++) {
                if (TipCommand[i].compareTo("(") == 0) {
                    tip_bracket++;
                }
                if (TipCommand[i].compareTo(")") == 0) {
                    tip_bracket--;
                }
            }
            // 若大于0，表明左括号还有未匹配的
            if (tip_bracket > 0) {
                Tipcode1 = 9;
                bracket = tip_bracket;
            } else if (tip_bracket == 0) {
                // 若前一个字符是以下之一，表明=号不合法
                if ("√^sincostanloglnn!".indexOf(tipcommand1) != -1) {
                    Tipcode1 = 6;
                }
                // 若前一个字符是以下之一，表明=号不合法
                if ("+-×÷".indexOf(tipcommand1) != -1) {
                    Tipcode1 = 5;
                }
            }
        }

        if (Tipcode1 != 0)
            tip_lock = false;// 表明输入有误
    }


    /**
     * 将信息显示在显示屏上
     */
    private void show(String str) {
        // 清屏后输出
        if (vbegin) {
            tvShow.setText(str);
        } else {
            tvShow.append(str);
        }
        vbegin = false;
    }


    /*
     * 检测函数，返回值为3、2、1 表示应当一次删除几个？ Three+Two+One = TTO 为←按钮的删除方式提供依据
     * 返回3，表示str尾部为sin、cos、tan、log中的一个，应当一次删除3个 返回2，表示str尾部为ln、n!中的一个，应当一次删除2个
     * 返回1，表示为除返回3、2外的所有情况，只需删除一个（包含非法字符时要另外考虑：应清屏）
     */
    private int delChar(String str) {
        if ((str.charAt(str.length() - 1) == 'n'
                && str.charAt(str.length() - 2) == 'i' && str.charAt(str
                .length() - 3) == 's')
                || (str.charAt(str.length() - 1) == 's'
                && str.charAt(str.length() - 2) == 'o' && str
                .charAt(str.length() - 3) == 'c')
                || (str.charAt(str.length() - 1) == 'n'
                && str.charAt(str.length() - 2) == 'a' && str
                .charAt(str.length() - 3) == 't')
                || (str.charAt(str.length() - 1) == 'g'
                && str.charAt(str.length() - 2) == 'o' && str
                .charAt(str.length() - 3) == 'l')) {
            return 3;
        } else if ((str.charAt(str.length() - 1) == 'n' && str.charAt(str
                .length() - 2) == 'l')
                || (str.charAt(str.length() - 1) == '!' && str.charAt(str
                .length() - 2) == 'n')) {
            return 2;
        } else {
            return 1;
        }
    }


    /*
     * 判断一个str是否是合法的，返回值为true、false
     * 只包含0123456789Eπ.()sincostanlnlogn!+-×÷√^的是合法的str，返回true
     * 包含了除0123456789Eπ.()sincostanlnlogn!+-×÷√^以外的字符的str为非法的，返回false
     */
    private boolean checkStr(String str) {
        if (str.length() > 18) {
            Toast.makeText(CalculatorActivity.this, "最大计算长度限制", Toast.LENGTH_SHORT).show();
            return false;
        }
        int i = 0;
        for (i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0' && str.charAt(i) != '1'
                    && str.charAt(i) != '2' && str.charAt(i) != '3'
                    && str.charAt(i) != '4' && str.charAt(i) != '5'
                    && str.charAt(i) != '6' && str.charAt(i) != '7'
                    && str.charAt(i) != '8' && str.charAt(i) != '9'
                    && str.charAt(i) != '.' && str.charAt(i) != '-'
                    && str.charAt(i) != '+' && str.charAt(i) != '×'
                    && str.charAt(i) != '÷' && str.charAt(i) != '√'
                    && str.charAt(i) != '^' && str.charAt(i) != 's'
                    && str.charAt(i) != 'i' && str.charAt(i) != 'n'
                    && str.charAt(i) != 'c' && str.charAt(i) != 'o'
                    && str.charAt(i) != 't' && str.charAt(i) != 'a'
                    && str.charAt(i) != 'l' && str.charAt(i) != 'g'
                    && str.charAt(i) != '(' && str.charAt(i) != ')'
                    && str.charAt(i) != '!' && str.charAt(i) != 'E'
                    && str.charAt(i) != 'π')
                break;
        }
        if (i == str.length()) {
            return true;
        } else {
            return false;
        }
    }


    /*
     * 整个计算核心，
     * 只要将表达式的整个字符串传入calc().process()就可以实行计算了 算法包括以下几部分：
     *  1、计算部分
     * process(String str) 当然，这是建立在查错无错误的情况下
     * 2、数据格式化 FP(double n) 使数据有相当的精确度
     * 3、阶乘算法 N(double n) 计算n!，将结果返回
     *  4、错误提示 showError(int code ,String str)
     * 将错误返回
     */
    public class calc {

        final int MAXLEN = 500;

        /*
         * 计算表达式 从左向右扫描，数字入number栈，运算符入operator栈
         * +-基本优先级为1，
         * ×÷基本优先级为2，
         * log ln sin cos tan n!基本优先级为3，
         * √^基本优先级为4
         * 括号内层运算符比外层同级运算符优先级高4
         * 当前运算符优先级高于栈顶压栈，
         * 低于栈顶弹出一个运算符与两个数进行运算
         *  重复直到当前运算符大于栈顶
         *   扫描完后对剩下的运算符与数字依次计算
         */
        public void process(String str, String type) {
            int weightPlus = 0, topOp = 0, topNum = 0, flag = 1, weightTemp = 0;
            // weightPlus为同一（）下的基本优先级，weightTemp临时记录优先级的变化
            // topOp为weight[]，operator[]的计数器；topNum为number[]的计数器
            // flag为正负数的计数器，1为正数，-1为负数
            int weight[]; // 保存operator栈中运算符的优先级，以topOp计数
            double number[]; // 保存数字，以topNum计数
            char ch, ch_gai, operator[];// operator[]保存运算符，以topOp计数
            String num;// 记录数字，str以+-×÷()sctgl!√^分段，+-×÷()sctgl!√^字符之间的字符串即为数字
            weight = new int[MAXLEN];
            number = new double[MAXLEN];
            operator = new char[MAXLEN];
            String expression = str;
            StringTokenizer expToken = new StringTokenizer(expression,
                    "+-×÷()sctgl!√^");//"+-×÷()sctgl!√^"参数定界符里面都是分隔符
            int i = 0;

            while (i < expression.length()) {
                ch = expression.charAt(i);
                // 判断正负数
                // 取得数字，并将正负符号转移给数字
                if (i == 0) {
                    if (ch == '-')
                        flag = -1;//说明首个数字为负数
                } else if (expression.charAt(i - 1) == '(' && ch == '-')
                    flag = -1;//例如 (-X.....
                if (ch <= '9' && ch >= '0' || ch == '.' || ch == 'E' || ch == 'π') {
                    num = expToken.nextToken();
                    ch_gai = ch;
                    // 取得整个数字
                    // 并且把 i 迭代到一组数字的最后一个
                    while (i < expression.length() && (ch_gai <= '9' && ch_gai >= '0' || ch_gai == '.' || ch_gai == 'E' || ch == 'π')) {
                        ch_gai = expression.charAt(i++);
                    }
                    // 将指针退回之前的位置
                    if (i >= expression.length())
                        i -= 1;
                    else {
                        i -= 2;
                    }
                    if (num.compareTo(".") == 0) {
                        number[topNum++] = 0;
                        // 将正负符号转移给数字
                    } else {
                        //把操作数装进number[]栈里面
                        if (num.compareTo("E") == 0) {
                            number[topNum++] = Math.E * flag;
                            flag = 1;
                        } else if (num.compareTo("π") == 0) {
                            number[topNum++] = Math.PI * flag;
                            flag = 1;
                        } else {
                            number[topNum++] = Double.parseDouble(num) * flag;
                            flag = 1;
                        }
                    }
                }
                // 计算运算符的优先级
                if (ch == '(')
                    weightPlus += 4;
                if (ch == ')')
                    weightPlus -= 4;
                if (ch == '-' && flag == 1 || ch == '+' || ch == '×'
                        || ch == '÷' || ch == 's' || ch == 'c' || ch == 't'
                        || ch == 'g' || ch == 'l' || ch == '!' || ch == '√'
                        || ch == '^') {
                    switch (ch) {
                        // +-的优先级最低，为1
                        case '+':
                        case '-':
                            weightTemp = 1 + weightPlus;
                            break;
                        // x÷的优先级稍高，为2
                        case '×':
                        case '÷':
                            weightTemp = 2 + weightPlus;
                            break;
                        // sincos之类优先级为3
                        case 's':
                        case 'c':
                        case 't':
                        case 'g':
                        case 'l':
                        case '!':
                        case '√':
                            weightTemp = 3 + weightPlus;
                            break;
                        // 其余优先级为4
                        // case '^':
                        default:
                            weightTemp = 4 + weightPlus;
                            break;
                    }
                    // 如果当前优先级大于堆栈顶部元素，则直接入栈
                    if (topOp == 0 || weight[topOp - 1] < weightTemp) {
                        weight[topOp] = weightTemp;
                        operator[topOp] = ch;
                        topOp++;
                        // 否则将堆栈中运算符逐个取出，直到当前堆栈顶部运算符的优先级小于当前运算符
                    } else {
                        while (topOp > 0 && weight[topOp - 1] >= weightTemp) {
                            switch (operator[topOp - 1]) {
                                // 取出数字数组的相应元素进行运算
                                case '+':
                                    number[topNum - 2] += number[topNum - 1];
                                    break;
                                case '-':
                                    number[topNum - 2] -= number[topNum - 1];
                                    break;
                                case '×':
                                    number[topNum - 2] *= number[topNum - 1];
                                    break;
                                // 判断除数为0的情况
                                case '÷':
                                    if (number[topNum - 1] == 0) {
                                        showError(1, str_old);
                                        return;
                                    }
                                    number[topNum - 2] /= number[topNum - 1];
                                    break;
                                case '√':
                                    if (number[topNum - 1] == 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = Math.sqrt(
                                            number[topNum - 1]);
                                    topNum++;//为了对消该 switch 括号外的 topNum--;
                                    break;
                                case '^':
                                    number[topNum - 2] = Math.pow(
                                            number[topNum - 2], number[topNum - 1]);
                                    break;
                                // 计算时进行角度弧度的判断及转换
                                // sin
                                case 's':
                                    number[topNum - 1] = Math
                                            .sin((number[topNum - 1] / 180)
                                                    * pi);
                                    topNum++;
                                    break;
                                // cos
                                case 'c':
                                    number[topNum - 1] = Math
                                            .cos((number[topNum - 1] / 180)
                                                    * pi);
                                    topNum++;
                                    break;
                                // tan
                                case 't':
                                    if ((Math.abs(number[topNum - 1]) / 90) % 2 == 1) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = Math
                                            .tan((number[topNum - 1] / 180)
                                                    * pi);
                                    topNum++;
                                    break;
                                // log
                                case 'g':
                                    if (number[topNum - 1] <= 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = Math
                                            .log10(number[topNum - 1]);
                                    topNum++;
                                    break;
                                // ln
                                case 'l':
                                    if (number[topNum - 1] <= 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = Math
                                            .log(number[topNum - 1]);
                                    topNum++;
                                    break;
                                // 阶乘
                                case '!':
                                    if (number[topNum - 1] > 170) {
                                        showError(3, str_old);
                                        return;
                                    } else if (number[topNum - 1] < 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = N(number[topNum - 1]);
                                    topNum++;
                                    break;
                            }
                            // 继续取堆栈的下一个元素进行判断
                            topNum--;
                            topOp--;
                        }
                        // 将运算符如堆栈
                        weight[topOp] = weightTemp;
                        operator[topOp] = ch;
                        topOp++;
                    }
                }
                i++;
            }
            // 依次取出堆栈的运算符进行运算
            while (topOp > 0) {
                // +-x直接将数组的后两位数取出运算
                switch (operator[topOp - 1]) {
                    case '+':
                        number[topNum - 2] += number[topNum - 1];
                        break;
                    case '-':
                        number[topNum - 2] -= number[topNum - 1];
                        break;
                    case '×':
                        number[topNum - 2] *= number[topNum - 1];
                        break;
                    // 涉及到除法时要考虑除数不能为零的情况
                    case '÷':
                        if (number[topNum - 1] == 0) {
                            showError(1, str_old);
                            return;
                        }
                        number[topNum - 2] /= number[topNum - 1];
                        break;
                    case '√':
                        if (number[topNum - 1] == 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = Math.sqrt(number[topNum - 1]);
                        topNum++;
                        break;
                    case '^':
                        number[topNum - 2] = Math.pow(number[topNum - 2],
                                number[topNum - 1]);
                        break;
                    // sin
                    case 's':
                        number[topNum - 1] = Math
                                .sin((number[topNum - 1] / 180) * pi);
                        topNum++;
                        break;
                    // cos
                    case 'c':
                        number[topNum - 1] = Math
                                .cos((number[topNum - 1] / 180) * pi);
                        topNum++;
                        break;
                    // tan
                    case 't':
                        if ((Math.abs(number[topNum - 1]) / 90) % 2 == 1) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = Math
                                .tan((number[topNum - 1] / 180) * pi);
                        topNum++;
                        break;
                    // 对数log
                    case 'g':
                        if (number[topNum - 1] <= 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = Math.log10(number[topNum - 1]);
                        topNum++;
                        break;
                    // 自然对数ln
                    case 'l':
                        if (number[topNum - 1] <= 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = Math.log(number[topNum - 1]);
                        topNum++;
                        break;
                    // 阶乘
                    case '!':
                        if (number[topNum - 1] > 170) {
                            showError(3, str_old);
                            return;
                        } else if (number[topNum - 1] < 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = N(number[topNum - 1]);
                        topNum++;
                        break;
                }
                // 取堆栈下一个元素计算
                topNum--;
                topOp--;
            }
            // 如果是数字太大，提示错误信息
            if (number[0] > 7.3E306) {
                showError(3, str_old);
                return;
            }
            if (type.equals("1")) {
                // 输出最终结果
                prefix.setText(str_old);
                AudioUtils.getInstance().speakText(String.valueOf(checkResult(number[0]))); //播放语音
                tvShow.setText(" = " + String.valueOf(checkResult(number[0])));
            } else {
                result = checkResult(number[0]) + "";
            }
        }


        /*
         * FP = floating point 控制小数位数，达到精度 否则会出现
         * 0.6-0.2=0.39999999999999997的情况，用FP即可解决，使得数为0.4 本格式精度为15位
         */
        public double checkResult(double n) {
            // NumberFormat format=NumberFormat.getInstance(); //创建一个格式化类f
            // format.setMaximumFractionDigits(18); //设置小数位的格式
            DecimalFormat format = new DecimalFormat("0.#####");
            return Double.parseDouble(format.format(n));
        }

        /*
         * 阶乘算法
         */
        public double N(double n) {
            int i = 0;
            double sum = 1;
            // 依次将小于等于n的值相乘
            for (i = 1; i <= n; i++) {
                sum = sum * i;
            }
            return sum;
        }


        /*
         * 错误提示，按了"="之后，若计算式在process()过程中，出现错误，则进行提示
         */
        public void showError(int code, String str) {
            String message = "";
            switch (code) {
                case 1:
                    message = "零不能作除数";
                    break;
                case 2:
                    message = "函数格式错误";
                    break;
                case 3:
                    message = "值太大了，超出范围";
            }
            tvShow.setText("\"" + str + "\"" + ": " + message);
        }
    }


    /**
     * 播放声音
     */
    protected void playSound(int id) {
        if (isNoSound()) {
            //如果是静音就不播放
            return;
        }
        if (!isFrist) {
            vibrator.vibrate(new long[]{0, 100}, -1);
        } else {
            if (isfalse_shake) {
                vibrator.cancel();
            } else {
                vibrator.vibrate(new long[]{0, 100}, -1);
            }
        }
        if (id == 0) {
            return;
        }

//        int mSoundVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //当前音量
        int mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mSoundResource.get(id) != null) {
//            mSoundPool.stop(mSoundStreamId);
            Integer integer = mSoundResource.get(id);
            Log.e("mSoundResource", "播放" + mCurrentVolume);
            mSoundStreamId = mSoundPool.play(integer, 1, 1, 0, 0, 1.0f);
        }
    }


    /**
     * ���ü����¼�
     */
    private void setOnClickListener() {
        music_icon.setOnClickListener(this);
        shake_icon.setOnClickListener(this);
        title_KX.setOnClickListener(this);
        title_jc.setOnClickListener(this);
        mCBtn.setOnClickListener(this);
        mDelBtn.setOnClickListener(this);
        mAddBtn.setOnClickListener(this);
        mMultiplyBtn.setOnClickListener(this);
        mDividebtn.setOnClickListener(this);
        mSubBtn.setOnClickListener(this);
        mZeroButton.setOnClickListener(this);
        mOnebtn.setOnClickListener(this);
        mTwoBtn.setOnClickListener(this);
        mThreeBtn.setOnClickListener(this);
        mFourBtn.setOnClickListener(this);
        mFiveBtn.setOnClickListener(this);
        mSixBtn.setOnClickListener(this);
        mSevenBtn.setOnClickListener(this);
        mEightBtn.setOnClickListener(this);
        mNineBtn.setOnClickListener(this);
        mPointtn.setOnClickListener(this);
        mEqualBtn.setOnClickListener(this);
        mpercent.setOnClickListener(this);
        mIb_back.setOnClickListener(this);
    }

    /**
     * ����¼�
     */
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            //声音
            case R.id.music_icon:
                if (isfalse_muisc) {
                    SharedPUtils.setMusic(this, false);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 4, 0);//tempVolume:音量绝对值
                    isfalse_muisc = false;
                    music_icon.setImageResource(R.drawable.music_icon);
                } else {
                    SharedPUtils.setMusic(this, true);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    isfalse_muisc = true;
                    music_icon.setImageResource(R.drawable.nomusic_icon);
                }
                break;
            //震动
            case R.id.shake_icon:
                isFrist = true;
                if (isfalse_shake) {
                    SharedPUtils.setShake(this, false);
                    isfalse_shake = false;
                    shake_icon.setImageResource(R.drawable.shake_icon);
                } else {
                    SharedPUtils.setShake(this, true);
                    isfalse_shake = true;
                    shake_icon.setImageResource(R.drawable.noshake_icon);
                }
                break;
            //基础
            case R.id.title_jc:
                kexue_result_menu.setVisibility(View.GONE);
                jishu_result_menu.setVisibility(View.VISIBLE);
                kexue_menu.setVisibility(View.GONE);
                jichu_menu.setVisibility(View.VISIBLE);
                title_jc.setTextColor(getResources().getColor(R.color.white));
                title_KX.setTextColor(getResources().getColor(R.color.darker_gray));
                break;
            //科学
            case R.id.title_kx:
                jishu_result_menu.setVisibility(View.GONE);
                kexue_result_menu.setVisibility(View.VISIBLE);
                jichu_menu.setVisibility(View.GONE);
                kexue_menu.setVisibility(View.VISIBLE);
                title_KX.setTextColor(getResources().getColor(R.color.white));
                title_jc.setTextColor(getResources().getColor(R.color.darker_gray));
                break;
            case R.id.c_btn:
                playSound(R.id.c_btn);
                clearAllScreen();
                break;
            case R.id.ib_back:
                finish();
                break;
            case R.id.del_btn:
                playSound(R.id.del_btn);
                back();
                break;
            case R.id.point_btn:
                playSound(R.id.point_btn);
                inputPoint(arg0);
                break;
            case R.id.equal_btn:
                playSound(R.id.equal_btn);
                try {
                    operator();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(this, "超出最大计算长度或输入有误");
                }
                break;
            case R.id.add_btn:
            case R.id.sub_btn:
            case R.id.multiply_btn:
            case R.id.divide_btn:
                playSound(arg0.getId());
                inputOperator(arg0);
                break;
            case R.id.percent:
                playSound(arg0.getId());
                inputPresentOperator(arg0);
                try {
                    operator();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(this, "超出最大计算长度或输入有误");
                }
                break;
            default:
                playSound(arg0.getId());
                inputNumber(arg0);
                break;

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    /**
     * ���=֮��ʼ����
     */
    private void operator() {
        if (mLastInputstatus == END || mLastInputstatus == ERROR || mLastInputstatus == INPUT_OPERATOR) {
            return;
//            || mInputList.size() == 1
        }
        Log.e("trim", "operator");
//        mShowResultTv.setText("");
//        mShowResultTvTwo.setText("");
        startAnim();
        if (mShowInputTv.getText().toString().contains("×")) { //java中0.1问题 https://blog.csdn.net/weixin_44018338/article/details/91420963
            String inputString = mShowInputTv.getText().toString();
            new calc().process(inputString.substring(0, inputString.length() - 1), "2");
            findHighOperator(0);
            if (mLastInputstatus != ERROR) {
                findLowOperator(0);
            }
            mHandler.sendMessageDelayed(mHandler.obtainMessage(SHOW_RESULT_DATA2), 300);
        } else {
            findHighOperator(0);
            if (mLastInputstatus != ERROR) {
                findLowOperator(0);
            }
            mHandler.sendMessageDelayed(mHandler.obtainMessage(SHOW_RESULT_DATA), 300);
        }

        //成功计算三次后提示应用市场评价
        if (times <= 2) {
            if (times == 2) {
                AppMarketUtil.goThirdApp(CalculatorActivity.this);
            }
            SPUtil.put(CalculatorActivity.this, "calculatorSuccessTimes", ++times);
        }
    }

    private void startAnim() {
        mShowInputTv.append("=");
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.screen_anim);
        mShowInputTv.startAnimation(anim);
    }

    /**
     * �����
     *
     * @param view
     */
    private void inputPoint(View view) {
        if (mLastInputstatus == INPUT_POINT) {
            return;
        }
        if (mLastInputstatus == END || mLastInputstatus == ERROR) {
            clearInputScreen();
        }
        String key = map.get(view);
        String input = mShowInputTv.getText().toString();
        if (mLastInputstatus == INPUT_OPERATOR) {
            input = input + "0";
        }
        mShowInputTv.setText(input + key);
        addInputList(INPUT_POINT, key);
    }

    /**
     * ��������
     *
     * @param view
     */
    private void inputNumber(View view) {
        String trim = mShowInputTv.getText().toString().trim();
        if (trim.length() > 28) {
            ToastUtil.showToastShort(CalculatorActivity.this, "最大计算长度限制");
            return;
        }
        if (mLastInputstatus == END || mLastInputstatus == ERROR) {
            clearInputScreen();
        }
        String key = map.get(view);
        if ("0".equals(mShowInputTv.getText().toString())) {
            mShowInputTv.setText(key);
        } else {
            mShowInputTv.setText(mShowInputTv.getText() + key);
        }
        addInputList(INPUT_NUMBER, key);
    }

    /**
     * ���������
     *
     * @param view
     */
    private void inputOperator(View view) {
        if (mLastInputstatus == INPUT_OPERATOR || mLastInputstatus == ERROR) {
            return;
        }
        if (mLastInputstatus == END) {
            mLastInputstatus = INPUT_NUMBER;
        }

        String key = map.get(view);
        if ("0".equals(mShowInputTv.getText().toString())) {
            mShowInputTv.setText("0" + key);
            mInputList.set(0, new InputItem("0", InputItem.INT_TYPE));
        } else {
            mShowInputTv.setText(mShowInputTv.getText() + key);
        }
        addInputList(INPUT_OPERATOR, key);
    }

    /**
     * %操作
     */
    public void inputPresentOperator(View view) {
        if (mLastInputstatus == INPUT_OPERATOR || mLastInputstatus == ERROR) {
            return;
        }
        mLastInputstatus = END;
        String trim = mShowInputTv.getText().toString().trim();
        Log.e("trim", "百分号" + trim);
        if ("0".equals(mShowInputTv.getText().toString())) {
            mShowInputTv.setText("0");
            mInputList.set(0, new InputItem("0", InputItem.DOUBLE_TYPE));
        } else {
            try {
                double v = Double.parseDouble(trim);
                mShowInputTv.setText(v / 100 + "");
                mInputList.set(0, new InputItem(v / 100 + "", InputItem.DOUBLE_TYPE));
            } catch (Exception e) {

            }
        }
    }

    /**
     * ���˲���
     */
    private void back() {
        if (mLastInputstatus == ERROR) {
            clearInputScreen();
        }
        String str = mShowInputTv.getText().toString();
        if (!TextUtils.isEmpty(str) && str.length() > 1) {
            mShowInputTv.setText(str.substring(0, str.length() - 1));
            backList();
        } else {
            mShowInputTv.setText(getResources().getString(R.string.zero));
            clearScreen(new InputItem("", InputItem.INT_TYPE));
        }
    }

    /**
     * ����InputList����
     */
    private void backList() {
        InputItem item = mInputList.get(mInputList.size() - 1);
        if (item.getType() == InputItem.INT_TYPE) {
            //��ȡ�����һ��item,��ȥ�����һ���ַ�
            String input = item.getInput().substring(0,
                    item.getInput().length() - 1);
            //��������ˣ����Ƴ����item��������ǰ״̬��Ϊ���������
            if ("".equals(input)) {
                mInputList.remove(item);
                mLastInputstatus = INPUT_OPERATOR;
            } else {
                //��������itemΪ��ȡ����ַ�����������ǰ״̬��Ϊnumber
                item.setInput(input);
                mLastInputstatus = INPUT_NUMBER;
            }
            //���item����������� ���Ƴ���
        } else if (item.getType() == InputItem.OPERATOR_TYPE) {
            mInputList.remove(item);
            if (mInputList.get(mInputList.size() - 1).getType() == InputItem.INT_TYPE) {
                mLastInputstatus = INPUT_NUMBER;
            } else {
                mLastInputstatus = INPUT_POINT;
            }
            //�����ǰitem��С��
        } else {
            String input = item.getInput().substring(0,
                    item.getInput().length() - 1);
            if ("".equals(input)) {
                mInputList.remove(item);
                mLastInputstatus = INPUT_OPERATOR;
            } else {
                if (input.contains(".")) {
                    item.setInput(input);
                    mLastInputstatus = INPUT_POINT;
                } else {
                    item.setInput(input);
                    mLastInputstatus = INPUT_NUMBER;
                }
            }
        }
    }

    //������
    private void clearAllScreen() {

        clearResultScreen();
        clearInputScreen();

    }

    private void clearResultScreen() {
        mShowResultTv.setText("");
        mShowResultTvTwo.setText("");
    }

    private void clearInputScreen() {
        mShowInputTv.setText(getResources().getString(R.string.zero));
        mLastInputstatus = INPUT_NUMBER;
        mInputList.clear();
        mInputList.add(new InputItem("", InputItem.INT_TYPE));
    }

    //�������
    private void clearScreen(InputItem item) {
        if (mLastInputstatus != ERROR) {
            mLastInputstatus = END;
        }
        mInputList.clear();
        mInputList.add(item);
    }

    //ʵ�ָ߼�����
    public int findHighOperator(int index) {
        if (mInputList.size() > 1 && index >= 0 && index < mInputList.size())
            for (int i = index; i < mInputList.size(); i++) {
                InputItem item = mInputList.get(i);
                if (getResources().getString(R.string.divide).equals(item.getInput())
                        || getResources().getString(R.string.multply).equals(item.getInput())) {
                    int a, b;
                    double c, d;
                    if (mInputList.get(i - 1).getType() == InputItem.INT_TYPE) {
                        a = Integer.parseInt(mInputList.get(i - 1).getInput());
                        if (mInputList.get(i + 1).getType() == InputItem.INT_TYPE) {
                            b = Integer.parseInt(mInputList.get(i + 1).getInput());
                            if (getResources().getString(R.string.multply).equals(item.getInput())) {
                                mInputList.set(i - 1, new InputItem(String.valueOf(a * b), InputItem.INT_TYPE));
                            } else {
                                if (b == 0) {
                                    mLastInputstatus = ERROR;
                                    if (a == 0) {
                                        clearScreen(new InputItem(nan, InputItem.ERROR));
                                    } else {
                                        clearScreen(new InputItem(infinite, InputItem.ERROR));
                                    }
                                    return -1;
                                } else if (a % b != 0) {
                                    mInputList.set(i - 1, new InputItem(String.valueOf((double) a / b), InputItem.DOUBLE_TYPE));
                                } else {
                                    mInputList.set(i - 1, new InputItem(String.valueOf((Integer) a / b), InputItem.INT_TYPE));
                                }
                            }
                        } else {
                            d = Double.parseDouble(mInputList.get(i + 1).getInput());
                            if (getResources().getString(R.string.multply).equals(item.getInput())) {
                                Log.i("erictest", "a=" + a + ",d=" + d + "==" + a * d);
                                mInputList.set(i - 1, new InputItem(String.valueOf(a * d), InputItem.DOUBLE_TYPE));
                            } else {
                                if (d == 0) {
                                    mLastInputstatus = ERROR;
                                    if (a == 0) {
                                        clearScreen(new InputItem(nan, InputItem.ERROR));
                                    } else {
                                        clearScreen(new InputItem(infinite, InputItem.ERROR));
                                    }
                                    return -1;
                                }
                                mInputList.set(i - 1, new InputItem(String.valueOf(a / d), InputItem.DOUBLE_TYPE));
                            }
                        }
                    } else {
                        c = Double.parseDouble(mInputList.get(i - 1).getInput());
                        if (mInputList.get(i + 1).getType() == InputItem.INT_TYPE) {
                            b = Integer.parseInt(mInputList.get(i + 1).getInput());
                            if (getResources().getString(R.string.multply).equals(item.getInput())) {
                                mInputList.set(i - 1, new InputItem(String.valueOf(c * b), InputItem.DOUBLE_TYPE));
                            } else {
                                if (b == 0) {
                                    mLastInputstatus = ERROR;
                                    if (c == 0) {
                                        clearScreen(new InputItem(nan, InputItem.ERROR));
                                    } else {
                                        clearScreen(new InputItem(infinite, InputItem.ERROR));
                                    }
                                    return -1;
                                }
                                mInputList.set(i - 1, new InputItem(String.valueOf(c / b), InputItem.DOUBLE_TYPE));
                            }
                        } else {
                            d = Double.parseDouble(mInputList.get(i + 1).getInput());
                            if (getResources().getString(R.string.multply).equals(item.getInput())) {
                                mInputList.set(i - 1, new InputItem(String.valueOf(mul(c, d)), InputItem.DOUBLE_TYPE));
                            } else {
                                if (d == 0) {
                                    mLastInputstatus = ERROR;
                                    if (c == 0) {
                                        clearScreen(new InputItem(nan, InputItem.ERROR));
                                    } else {
                                        clearScreen(new InputItem(infinite, InputItem.ERROR));
                                    }
                                    return -1;
                                }
                                mInputList.set(i - 1, new InputItem(String.valueOf(div(c, d)), InputItem.DOUBLE_TYPE));
                            }
                        }
                    }
                    mInputList.remove(i + 1);
                    mInputList.remove(i);
                    return findHighOperator(i);
                }
            }
        return -1;

    }

    public int findLowOperator(int index) {
        if (mInputList.size() > 1 && index >= 0 && index < mInputList.size())
            for (int i = index; i < mInputList.size(); i++) {
                InputItem item = mInputList.get(i);
                if (getResources().getString(R.string.sub).equals(item.getInput())
                        || getResources().getString(R.string.add).equals(item.getInput())) {

                    Double a_ = Double.parseDouble(mInputList.get(i - 1).getInput());
                    Double d_ = Double.parseDouble(mInputList.get(i + 1).getInput());

                    if (getResources().getString(R.string.add).equals(item.getInput())) {
                        Double add = add(a_, d_);
                        if (add % 1 == 0) {
                            mInputList.set(i - 1, new InputItem(String.valueOf(add.intValue()), InputItem.INT_TYPE));
                        } else {
                            mInputList.set(i - 1, new InputItem(String.valueOf(add), InputItem.DOUBLE_TYPE));
                        }
                    } else {
                        Double sub = sub(a_, d_);
                        if (sub % 1 == 0) {
                            mInputList.set(i - 1, new InputItem(String.valueOf(sub.intValue()), InputItem.INT_TYPE));
                        } else {
                            mInputList.set(i - 1, new InputItem(String.valueOf(sub), InputItem.DOUBLE_TYPE));
                        }
                    }
//                    int a, b;
//                    double c, d;
//                    if (mInputList.get(i - 1).getType() == InputItem.INT_TYPE) {
//                        a = Integer.parseInt(mInputList.get(i - 1).getInput());
//                        if (mInputList.get(i + 1).getType() == InputItem.INT_TYPE) {
//                            b = Integer.parseInt(mInputList.get(i + 1).getInput());
//                            if (getResources().getString(R.string.add).equals(item.getInput())) {
//                                mInputList.set(i - 1, new InputItem(String.valueOf(a + b), InputItem.INT_TYPE));
//                            } else {
//                                mInputList.set(i - 1, new InputItem(String.valueOf(a - b), InputItem.INT_TYPE));
//                            }
//                        } else {
//                            d = Double.parseDouble(mInputList.get(i + 1).getInput());
//                            if (getResources().getString(R.string.add).equals(item.getInput())) {
//                                mInputList.set(i - 1, new InputItem(String.valueOf(a + d), InputItem.DOUBLE_TYPE));
//                            } else {
//                                mInputList.set(i - 1, new InputItem(String.valueOf(a - d), InputItem.DOUBLE_TYPE));
//                            }
//                        }
//                    } else {
//                        c = Double.parseDouble(mInputList.get(i - 1).getInput());
//                        if (mInputList.get(i + 1).getType() == InputItem.INT_TYPE) {
//                            b = Integer.parseInt(mInputList.get(i + 1).getInput());
//                            if (getResources().getString(R.string.add).equals(item.getInput())) {
//                                mInputList.set(i - 1, new InputItem(String.valueOf(c + b), InputItem.DOUBLE_TYPE));
//                            } else {
//                                mInputList.set(i - 1, new InputItem(String.valueOf(c - b), InputItem.DOUBLE_TYPE));
//                            }
//                        } else {
//                            d = Double.parseDouble(mInputList.get(i + 1).getInput());
//                            if (getResources().getString(R.string.add).equals(item.getInput())) {
//                                mInputList.set(i - 1, new InputItem(String.valueOf(add(c, d)), InputItem.DOUBLE_TYPE));
//                            } else {
//                                mInputList.set(i - 1, new InputItem(String.valueOf(sub(c, d)), InputItem.DOUBLE_TYPE));
//                            }
//                        }
//                    }
                    mInputList.remove(i + 1);
                    mInputList.remove(i);
                    return findLowOperator(i);
                }
            }
        return -1;

    }

    //currentStatus ��ǰ״̬  9  "9" "+"
    void addInputList(int currentStatus, String inputChar) {
        switch (currentStatus) {
            case INPUT_NUMBER:
                if (mLastInputstatus == INPUT_NUMBER) {
                    InputItem item = mInputList.get(mInputList.size() - 1);
                    item.setInput(item.getInput() + inputChar);
                    item.setType(InputItem.INT_TYPE);
                    mLastInputstatus = INPUT_NUMBER;
                } else if (mLastInputstatus == INPUT_OPERATOR) {
                    InputItem item = new InputItem(inputChar, InputItem.INT_TYPE);
                    mInputList.add(item);
                    mLastInputstatus = INPUT_NUMBER;
                } else if (mLastInputstatus == INPUT_POINT) {
                    InputItem item = mInputList.get(mInputList.size() - 1);
                    item.setInput(item.getInput() + inputChar);
                    item.setType(InputItem.DOUBLE_TYPE);
                    mLastInputstatus = INPUT_POINT;
                }
                break;
            case INPUT_OPERATOR:
                InputItem item = new InputItem(inputChar, InputItem.OPERATOR_TYPE);
                mInputList.add(item);
                mLastInputstatus = INPUT_OPERATOR;
                break;
            case INPUT_POINT://point
                if (mLastInputstatus == INPUT_OPERATOR) {
                    InputItem item1 = new InputItem("0" + inputChar, InputItem.DOUBLE_TYPE);
                    mInputList.add(item1);
                    mLastInputstatus = INPUT_POINT;
                } else {
                    InputItem item1 = mInputList.get(mInputList.size() - 1);
                    item1.setInput(item1.getInput() + inputChar);
                    item1.setType(InputItem.DOUBLE_TYPE);
                    mLastInputstatus = INPUT_POINT;
                }
                break;
        }
    }

    public static Double div(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.divide(b2, 10, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static Double sub(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.subtract(b2).doubleValue();
    }

    public static Double add(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.add(b2).doubleValue();
    }

    public static Double mul(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).doubleValue();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSoundPool != null) {
            mSoundPool = null;
        }
        if (mAudioManager != null) {
            mAudioManager = null;
        }
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }

        if (mSoundResource != null) {
            mSoundResource.clear();
            mSoundResource = null;
        }
    }

    /**
     * 获取当前音量，判断是否是静音状态
     */
    public boolean isNoSound() {
        if (mAm == null) {
            //得到音量
            mAm = (AudioManager) getSystemService(AUDIO_SERVICE);
        }
        int mode = mAm.getRingerMode();
        switch (mode) {
            case AudioManager.RINGER_MODE_NORMAL:
                //普通模式
                return false;
            case AudioManager.RINGER_MODE_VIBRATE:
                //振动模式
                return true;
            case AudioManager.RINGER_MODE_SILENT:
                //静音模式
                return true;
        }
        return false;
    }
}
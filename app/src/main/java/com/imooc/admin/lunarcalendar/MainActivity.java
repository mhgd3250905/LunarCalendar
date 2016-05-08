package com.imooc.admin.lunarcalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private TextView date, text_yinli, text_wuxing, text_chongsha,
            text_baiji, text_jishen, text_yi, text_xiongshen, text_ji,
            baiji_title, jishen_title, yi_title, xiongshen_title, ji_title;
    final Calendar c = Calendar.getInstance();
    int mainYear = c.get(Calendar.YEAR);//获取当前年份
    int mainMonth = c.get(Calendar.MONTH);//获取当前的月份
    int mainday = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码 ;
    boolean first = true;
    String dateString = mainYear + "-" + (mainMonth+1) + "-" + mainday;//当前日期的需求字符串形式
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 12345:
                    String[] getData = (String[]) msg.obj;
                    text_yinli.setText("阴历" + getData[0]);
                    text_wuxing.setText("五行: " + getData[1]);
                    text_chongsha.setText("冲煞: " + getData[2]);
                    text_baiji.setText(getData[3]);
                    text_jishen.setText(getData[4]);
                    text_yi.setText(getData[5]);
                    text_xiongshen.setText(getData[6]);
                    text_ji.setText(getData[7]);
                    break;
                case 54321:
                    String[] getData0 = (String[]) msg.obj;
                    text_yinli.setText("阴历"+getData0[0]);
                    text_wuxing.setText("五行: " + getData0[1]);
                    text_chongsha.setText("冲煞: " + getData0[2]);
                    text_baiji.setText(getData0[3]);
                    text_jishen.setText(getData0[4]);
                    text_yi.setText(getData0[5]);
                    text_xiongshen.setText(getData0[6]);
                    text_ji.setText(getData0[7]);
                    break;
                default:
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        AssetManager mgr = getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "font/open.ttf");//根据路径得到Typeface
        Typeface tf2 = Typeface.createFromAsset(mgr, "font/aaa.ttf");
        if (first) {
            Log.d("MainActivity", "已接收2");
            first = false;
            Bundle bundle = this.getIntent().getExtras();
            String[] getFromOpen = bundle.getStringArray("dateString_fromOpen");
            Message messageGetFromOpen = new Message();
            messageGetFromOpen.what = 54321;
            messageGetFromOpen.obj = getFromOpen;
            handler.sendMessage(messageGetFromOpen);
        }

        date = (TextView) findViewById(R.id.date);
        text_yinli = (TextView) findViewById(R.id.yinli);
        text_wuxing = (TextView) findViewById(R.id.wuxing);
        text_chongsha = (TextView) findViewById(R.id.chongsha);
        text_baiji = (TextView) findViewById(R.id.baiji);
        text_jishen = (TextView) findViewById(R.id.jishen);
        text_yi = (TextView) findViewById(R.id.yi);
        text_xiongshen = (TextView) findViewById(R.id.xiongshen);
        text_ji = (TextView) findViewById(R.id.ji);
        baiji_title = (TextView) findViewById(R.id.baiji_title);
        jishen_title = (TextView) findViewById(R.id.jishen_title);
        yi_title = (TextView) findViewById(R.id.yi_title);
        xiongshen_title = (TextView) findViewById(R.id.xiongshen_title);
        ji_title = (TextView) findViewById(R.id.ji_title);
        date.setTypeface(tf2);
        text_yinli.setTypeface(tf2);
        text_wuxing.setTypeface(tf2);
        text_chongsha.setTypeface(tf2);
        text_baiji.setTypeface(tf2);
        text_jishen.setTypeface(tf2);
        text_yi.setTypeface(tf2);
        text_xiongshen.setTypeface(tf2);
        text_ji.setTypeface(tf2);
        baiji_title.setTypeface(tf2);
        jishen_title.setTypeface(tf2);
        yi_title.setTypeface(tf2);
        xiongshen_title.setTypeface(tf2);
        ji_title.setTypeface(tf2);
        date.setTypeface(tf);
        date.setText(dateString);
    }


    // 日期选择对话框的 DateSet 事件监听器
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {  //
        @Override
        public void onDateSet(DatePicker arg0, int selectYear, int selectMonth, int selectDay) {
            dateString = selectYear + "-" + (selectMonth + 1) + "-" + selectDay;
            date.setText(dateString);
            OpenWithHttpConnection(dateString);
        }
    };

    //appkey:92530afe8465b7f2d316a6c21ac614b7
    private void OpenWithHttpConnection(final String dateString) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://v.juhe.cn/laohuangli/d?date=" + dateString + "&key=92530afe8465b7f2d316a6c21ac614b7");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    /*
                    截止到以上在 线程中获得了 返回的JSON信息 response
                    使用时记得使用response.toString()
                     */
                    //在另一个方法中开启新线程中处理获得的JOSN数据
                    handeleJOSN(response.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /*
    下面的方法中处理接收到的JSON数据

    JSON返回示例：
    {
    "reason": "successed",
    "result": {
        "id": "1657",
        "yangli": "2014-09-11",
        "yinli": "甲午(马)年八月十八",
        "wuxing": "井泉水 建执位",
        "chongsha": "冲兔(己卯)煞东",
        "baiji": "乙不栽植千株不长 酉不宴客醉坐颠狂",
        "jishen": "官日 六仪 益後 月德合 除神 玉堂 鸣犬",
        "yi": "祭祀 出行 扫舍 馀事勿取",
        "xiongshen": "月建 小时 土府 月刑 厌对 招摇 五离",
        "ji": "诸事不宜"
    },
    "error_code": 0
    }
     */

    private void handeleJOSN(String s) {
        try {
            JSONObject getData = new JSONObject(s);
            String result = getData.getString("result");
            JSONObject data = new JSONObject(result);
            String yangli = data.getString("yangli");
            String yinli = data.getString("yinli");
            String wuxing = data.getString("wuxing");
            String chongsha = data.getString("chongsha");
            String baiji = data.getString("baiji");
            String jishen = data.getString("jishen");
            String yi = data.getString("yi");
            String xiongshen = data.getString("xiongshen");
            String ji = data.getString("ji");

            Log.d("MainActivity", yangli);
            Log.d("MainActivity", yinli);
            Log.d("MainActivity", wuxing);
            //把所有数据装载在一个数组里
            String[] allDate = {yinli, wuxing, chongsha, baiji, jishen, yi, xiongshen, ji};
            //然后把数组装载在message中传回去
            Message message = new Message();
            message.what = 12345;
            message.obj = allDate;
            handler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //选择日期按键的触发事件：显示一个DatePickerDialog
    public void dateClick(View view) {
        switch (view.getId()) {
            case R.id.date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, listener,
                        mainYear, mainMonth, mainday);
                datePickerDialog.show();
                break;
            default:
                break;
        }
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click(); //调用双击退出函数
        }
        return false;
    }
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2500); // 如果2.5秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }


}

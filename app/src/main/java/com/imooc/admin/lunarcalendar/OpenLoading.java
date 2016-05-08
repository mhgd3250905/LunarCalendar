package com.imooc.admin.lunarcalendar;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

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

/**
 * Created by admin on 2016/4/4.
 */
public class OpenLoading extends Activity {

    private TextView openText,coupletUp,coupletDown,myText;
    final Calendar c = Calendar.getInstance();
    int mainYear = c.get(Calendar.YEAR);//获取当前年份
    int mainMonth = c.get(Calendar.MONTH);//获取当前的月份
    int mainday = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码 ;
    boolean open = true;
    String dateString = mainYear + "-" + (mainMonth + 1) + "-" + mainday;//当前日期的需求字符串形式



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载欢迎页面
        setContentView(R.layout.login_layout);
        openText = (TextView) findViewById(R.id.open_text);
        coupletUp= (TextView) findViewById(R.id.textView1);
        coupletDown= (TextView) findViewById(R.id.textView2);
        myText= (TextView) findViewById(R.id.textView3);
        AssetManager mgr = getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "font/open.ttf");//根据路径得到Typeface
        openText.setTypeface(tf);
        coupletUp.setTypeface(tf);
        coupletDown.setTypeface(tf);
        myText.setTypeface(tf);
        OpenWithHttpConnection(dateString);
    }

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 308040) {
                Log.d("MainActivity", "已接收");
                String[] getdataOpen = (String[]) msg.obj;
                if (open) {
                    open = false;
                    Intent intent = new Intent(OpenLoading.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("dateString_fromOpen", getdataOpen);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
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
            Message messageOpen = new Message();
            messageOpen.what = 308040;
            messageOpen.obj = allDate;
            mhandler.sendMessageDelayed(messageOpen,2000);
            Log.d("MainActivity", "已发送");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}


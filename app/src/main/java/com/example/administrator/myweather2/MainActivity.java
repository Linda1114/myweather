package com.example.administrator.myweather2;

import android.content.SharedPreferences;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bean.TodayWeather;
import com.example.administrator.util.NetUtil;
import com.example.administrator.util.ParseXMLByPull;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.logging.LogRecord;
import java.util.zip.GZIPInputStream;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private ImageView mUpdateBtn;
    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,climateTv,windTv;
    private ImageView weatherImg,pmImg;
    private TodayWeather todayWeather = null;
    private static final int UPDATE_TODAY_WEATHER =1;
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        // NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE;
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        initView();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    void initView(){
         cityTv=(TextView) findViewById(R.id.city);
         timeTv=(TextView) findViewById(R.id.time);
         humidityTv=(TextView) findViewById(R.id.humidity);
         weekTv=(TextView) findViewById(R.id.week_today);
         pmDataTv=(TextView) findViewById(R.id.pm_data);
         pmQualityTv=(TextView) findViewById(R.id.pm2_5_quality);
         pmImg=(ImageView)findViewById(R.id.pm2_5_img);
         temperatureTv=(TextView) findViewById(R.id.temprature);
         climateTv=(TextView) findViewById(R.id.climate);
         windTv=(TextView) findViewById(R.id.wind);
         weatherImg=(ImageView)findViewById(R.id.weather_img);
         cityTv.setText("N/A");
         timeTv.setText("N/A");
         humidityTv.setText("N/A");
         pmDataTv.setText("N/A");
         pmQualityTv.setText("N/A");
         weekTv.setText("N/A");
         temperatureTv.setText("N/A");
         climateTv.setText("N/A");
         windTv.setText("N/A");
    }
    public void onClick(View view) {
        TodayWeather todayWeather=null;

        if (view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather", cityCode);
            System.out.println(NetUtil.getNetworkState(this));
           if(NetUtil.getNetworkState(this) !=NetUtil.NETWORN_NONE) {
               Log.d("myWeather","网络OK");
               queryWeatherCode(cityCode);
           }else
          {
               Log.d("myWeather","网络挂了");
               Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
           }
        }

    }

    private TodayWeather parseXML(String xmldata){

        try{
            int fengxiangCount=0;
            int fengliCount=0;
            int dateCount=0;
            int highCount=0;
            int lowCount=0;
            int typeCount=0;
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullparser = fac.newPullParser();
            xmlPullparser.setInput(new StringReader(xmldata));
            int eventType = xmlPullparser.getEventType();
            Log.d("myapp2", "parseXML");
            while(eventType!=XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                    //触发开始文档事件
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    //触发开始元素事件
                    case XmlPullParser.START_TAG:
                        //获取解析器当前指向的元素的名称
                        if(xmlPullparser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather!=null) {
                            if (xmlPullparser.getName().equals("city")) {
                                eventType = xmlPullparser.next();
                               todayWeather.setCity( xmlPullparser.getText());
                            } else if (xmlPullparser.getName().equals("updatetime")) {
                                eventType = xmlPullparser.next();
                               todayWeather.setUpdatetime( xmlPullparser.getText());
                            } else if (xmlPullparser.getName().equals("shidu")) {
                                eventType = xmlPullparser.next();
                               todayWeather.setShidu( xmlPullparser.getText());
                            } else if (xmlPullparser.getName().equals("wendu")) {
                                eventType = xmlPullparser.next();
                                todayWeather.setWendu( xmlPullparser.getText());
                            } else if (xmlPullparser.getName().equals("pm25")) {
                                eventType = xmlPullparser.next();
                               todayWeather.setPm25(xmlPullparser.getText());
                            } else if (xmlPullparser.getName().equals("quality")) {
                                eventType = xmlPullparser.next();
                                todayWeather.setQuality(xmlPullparser.getText());
                            } else if (xmlPullparser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullparser.next();
                                todayWeather.setFengxiang(xmlPullparser.getText());
                                fengxiangCount++;
                            } else if (xmlPullparser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullparser.next();
                               todayWeather.setFengli(xmlPullparser.getText());
                                fengliCount++;
                            } else if (xmlPullparser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullparser.next();
                                todayWeather.setDate(xmlPullparser.getText());
                                dateCount++;
                            } else if (xmlPullparser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullparser.next();
                                todayWeather.setHigh( xmlPullparser.getText());
                                highCount++;
                            } else if (xmlPullparser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullparser.next();
                               todayWeather.setLow(xmlPullparser.getText());
                                lowCount++;
                            } else if (xmlPullparser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullparser.next();
                               todayWeather.setType(xmlPullparser.getText());
                                typeCount++;
                            }
                        }

                        break;

                    //触发结束元素事件
                    case XmlPullParser.END_TAG:
                        //

                        break;
                    default:
                        break;
                }
                eventType = xmlPullparser.next();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return todayWeather;

    }

    private void queryWeatherCode(String cityCode) {

        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);

        new Thread(new Runnable() {
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(address);
                    HttpResponse httpResponse = httpclient.execute(httpget);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();

                        InputStream responseStream = entity.getContent();
                        responseStream = new GZIPInputStream(responseStream);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                        StringBuilder response = new StringBuilder();
                        String str;
                        while ((str = reader.readLine()) != null) {
                            response.append(str);
                        }
                        String responseStr = response.toString();
                        //Log.d("myWeather", responseStr);

                       todayWeather = parseXML(responseStr);
                        if(todayWeather !=null){
                           // Log.d("myWeather",todayWeather.toString());
                            Message msg = new Message();
                            msg.what=UPDATE_TODAY_WEATHER;
                            msg.obj=todayWeather;
                            mHandler.sendMessage(msg);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    void updateTodayWeather(TodayWeather todayWeather){
        Log.d("myapp3",todayWeather.toString());
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度:"+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();

    }


}
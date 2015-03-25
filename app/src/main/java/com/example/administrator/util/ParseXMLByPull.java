package com.example.administrator.util;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

/**
 * Created by Administrator on 2015/3/24.
 */
public class ParseXMLByPull {
    private void parseXML(String xmldata){
        try{



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

                        if(xmlPullparser.getName().equals("city"))
                        {
                            eventType = xmlPullparser.next();
                            Log.d("myapp2", "city: "+xmlPullparser.getText());
                         }
                        else if(xmlPullparser.getName().equals("updatetime")){
                            eventType = xmlPullparser.next();
                            Log.d("myapp2", "updatetime: "+xmlPullparser.getText());
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



    }
}

package com.runvision.webcore.handler;

import android.util.Log;

import com.google.gson.Gson;
import com.runvision.bean.AppData;
import com.runvision.g69a_sn.MyApplication;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QueryAttendanceRulesCount implements RequestHandler {

    @RequestMapping(method = RequestMethod.POST)
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Map<String, String> params = HttpRequestParser.parseParams(request);
        String beginTime = params.get("time_begin");
        String endTime = params.get("time_end");
        String cardNo = params.get("idcard");
        String name = params.get("name");
       // String up_startime=params.get("atd_up_startime");
      //  String up_endtime=params.get("atd_up_endtime");
       // String down_startime=params.get("atd_down_startime");
       // String down_endtime=params.get("atd_down_endtime");

        String atd_type=params.get("atd_type");
        AppData.getAppData().setAtd_type(atd_type);

       // AppData.getAppData().setAtd_up_startime(up_startime);
       // AppData.getAppData().setAtd_up_endtime(up_endtime);
       // AppData.getAppData().setAtd_down_startime(down_startime);
       // AppData.getAppData().setAtd_down_endtime(down_endtime);


        String atd_up_startime=params.get("atd_up_startime");
        if(atd_up_startime.equals("00:00"))
        {
            atd_up_startime="24:00";
        }
        String atd_up_endtime=params.get("atd_up_endtime");
        if(atd_up_endtime.equals("00:00"))
        {
            atd_up_endtime="24:00";
        }
        String atd_down_startime=params.get("atd_down_startime");
        if(atd_down_startime.equals("00:00"))
        {
            atd_down_startime="24:00";
        }
        String atd_down_endtime=params.get("atd_down_endtime");
        if(atd_down_endtime.equals("00:00"))
        {
            atd_down_endtime="24:00";
        }
        AppData.getAppData().setAtd_up_startime(atd_up_startime);
        AppData.getAppData().setAtd_up_endtime(atd_up_endtime);
        AppData.getAppData().setAtd_down_startime(atd_down_startime);
        AppData.getAppData().setAtd_down_endtime(atd_down_endtime);

        StringBuffer sb = new StringBuffer();
        sb.append("select count(id) from tRecord where 1 = 1");


        if (!beginTime.equals("")) {
            sb.append(" and createTime>" + beginTime);
        }
        if (!endTime.equals("")) {
            sb.append(" and createTime<" + endTime);
        }

        if(atd_type.equals("1")) {
            sb.append(" and time(createTime, 'unixepoch', 'localtime')>="+"'"+atd_up_startime+"'");
            sb.append(" and time(createTime, 'unixepoch', 'localtime')<="+"'"+atd_up_endtime+"'");
        }

        if(atd_type.equals("2")) {
            sb.append(" and time(createTime, 'unixepoch', 'localtime')>="+"'"+atd_down_startime+"'");
            sb.append(" and time(createTime, 'unixepoch', 'localtime')<="+"'"+atd_down_endtime+"'");
        }
        if(atd_type.equals("0"))
        {
            sb.append(" and (time(createTime, 'unixepoch', 'localtime')>="+"'"+atd_up_startime+"'");
            sb.append(" and time(createTime, 'unixepoch', 'localtime')<="+"'"+atd_up_endtime+"')");
            sb.append(" or (time(createTime, 'unixepoch', 'localtime')>="+"'"+atd_down_startime+"'");
            sb.append(" and time(createTime, 'unixepoch', 'localtime')<="+"'"+atd_down_endtime+"')");
        }

        if (!cardNo.equals("")) {
            sb.append(" and cardNo like '%" + cardNo + "%'");
        }
        if (!name.equals("")) {
            sb.append(" and name like '%" + name + "%'");
        }
        //上下班打卡时间段

        sb.append(" order by id desc");
        Log.i("HTTP", "QueryCardRecordCount:sql= " + sb.toString());
        //int count=0;
      //  List<Atd> mList = MyApplication.faceProvider.queryATDRecord(sb.toString());
       // int count =mList.size();
        int count = MyApplication.faceProvider.quaryRecordTableRowCount(sb.toString());
        Map<String, Integer> map = new HashMap<>();
        map.put("code", 200);
        map.put("count", count);
        Gson gson = new Gson();
        String json = gson.toJson(map);
        Log.i("HTTP", "QueryCardRecordCount:json= " + json);

        response.setEntity(new StringEntity(json, "UTF-8"));
        response.setStatusCode(200);


    }
}

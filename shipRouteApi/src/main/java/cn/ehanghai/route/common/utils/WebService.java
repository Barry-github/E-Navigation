package cn.ehanghai.route.common.utils;
import cn.ehanghai.route.nav.domain.BaseLine;
import cn.ehanghai.route.nav.domain.RouteLine;
import cn.ehanghai.route.nav.domain.RouteLineCheck;
import cn.ehanghai.route.nav.domain.ShipTrace;
import com.alibaba.fastjson.JSONObject;
import net.ryian.commons.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;


public class WebService {

    public  static  boolean checkRouteOver=true;
    //yulj:这里为什么直接写死？会不会和application.yml中的设置矛盾
    //private  static  String routeCalcUrl="http://172.16.43.215:9004";
    //private  static  String routeCheckUrl="http://172.16.43.215:9003";

    private  static  String routeCalcUrl="http://localhost:9004";
    private  static  String routeCheckUrl="http://localhost:9003";

    /**
     * 检查所有港口航线是否存在绕行
     * */
    public  static void checkRouteAsync() {

        Runnable myRunnable = new Runnable() {
            public void run() {
                String url = routeCalcUrl + "/nav/routecalc/checkRoute";
                checkRouteOver = false;
                sendGet(url);
                checkRouteOver = true;
            }
        };

        Thread thread = new Thread(myRunnable);
        thread.start();
    }


    public  static String checkRouteSync() {
        String url=routeCalcUrl+"/nav/routecalc/checkRoute";
        String content= sendGet( url);
        return content;
    }


    public  static String checkRouteLines(String areaCode,int maxNum) {

        String url=routeCalcUrl+"/nav/routelinelistcalc/checkHarours";
        if(!StringUtils.isEmpty(areaCode)){
            url=url+"/"+areaCode+"/"+maxNum;
        }
        else {
            url=url+"/"+maxNum;
        }
        return   sendGet( url);
    }


    public  static String calcDepthLineList(int maxNum,double shipTon) {

        String url=routeCalcUrl+"/nav/routelinelistcalc/depthNodes/"+maxNum+"/"+shipTon;
        return   sendGet( url);
    }


    public  static String checkRouteLinesRestart() {

        String url=routeCalcUrl+"/nav/routelinelistcalc/checkHarours/restart";
        return   sendGet( url);
    }


    public  static void calcInsersectPoints() {

        Runnable myRunnable = new Runnable(){
            public void run(){
                String url=routeCalcUrl+"/nav/routecalc/insersectPoints";
                sendGet( url);
            }
        };

        Thread thread = new Thread(myRunnable);
        thread.start();

    }


    public  static List<ShipTrace> queryAis(String starttime, String endtime, String mmsi) {
        String url=" http://localhost:9005/ais/clean/"+starttime+"/"+endtime+"/"+mmsi;
        String content= sendGet( url);
        List<ShipTrace> shipTraces=   JSONObject.parseArray(content,ShipTrace.class);
        return shipTraces;
    }



    public  static String checkHarours(String areaCode) {
        String url=routeCalcUrl+"/nav/area/route/checkHarours/"+areaCode;
        String content= sendGet( url);
        return content;
    }


    public  static String checkHarours(double shipHeight, double depth, double shipton) {
        String url=routeCalcUrl+"/nav/routecalc/checkHarours/"+shipHeight+"/"+depth+"/"+shipton;
        String content= sendGet( url);
        return content;
    }

   public  static String   calcReset()
   {
       String url=routeCalcUrl+"/nav/routecalc/calc/reset";
       String content= sendGet( url);

       return content;
   }

    public static   List<RouteLineCheck> calcRoute(String startCode, String endCode, double shipHeight, double depth, double shipton, String laneName)
    {

        String url=routeCalcUrl+"/nav/routecalc/calc/";
        url=url+startCode+"/"+endCode+"/"+shipHeight+"/"+depth+"/"+shipton+"/"+laneName;

        String content= sendGet( url);
        List<RouteLineCheck> lines=   JSONObject.parseArray(content,RouteLineCheck.class);
        return lines;
    }

    public static double calcDistance(String path){
        String url = routeCalcUrl + "/nav/routecalc/calc/distance/";
        url += path;
        String content = sendGet(url);
        double distance = Double.parseDouble(content);
        return distance;
    }



    public static  void checkRouteLine(List<BaseLine> baseLines)
    {

        Runnable myRunnable = new Runnable(){
            public void run(){
                // http://localhost:9003/nav/routeCheck/check/E-0012806/E-0012807
                 //String url=" http://192.168.50.72:9003/nav/routeCheck/check/";

                String url=routeCheckUrl+"/nav/routeCheck/check/";
                String json=JSONObject.toJSONString(baseLines);
                //url=url+json;
                //sendGet(url);
                String param="jsonargs="+json;
                sendPost( url,  param);


                 url=routeCheckUrl+"/nav/waterdepth/checklinedepth/";
                 param="jsonargs="+json;
                sendPost( url,  param);

            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    public static  void checkRouteLine(BaseLine baseLine)
    {
        List<BaseLine> baseLines=new ArrayList<>();
        baseLines.add(baseLine);
        checkRouteLine( baseLines);
    }

    private static String sendGet(String url)
    {
        String result="";
        BufferedReader in = null;// 读取响应输入流
        try {

            String full_url = url ;
            System.out.println(full_url);
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(full_url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL
                    .openConnection();
            // 设置通用属性

            // 建立实际的连接
            httpConn.connect();
            // 响应头部获取
            Map<String, List<String>> headers = httpConn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : headers.keySet()) {
                System.out.println(key + "\t：\t" + headers.get(key));
            }
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn
                    .getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result ;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();

            // 设置通用的请求属性
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //1.获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //2.中文有乱码的需要将PrintWriter改为如下
            //out=new OutputStreamWriter(conn.getOutputStream(),"UTF-8")
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        System.out.println("post推送结果："+result);
        return result;
    }



}

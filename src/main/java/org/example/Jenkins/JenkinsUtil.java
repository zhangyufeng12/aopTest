package org.example.Jenkins;
/*
*@author  zhangyufeng
*@data 2024/2/23 16:03
*/


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Map;

public class JenkinsUtil {

    public static String encoding;
    public static Map<String,String> header;
    public static final String jobName = "xx";
    public static final String jenkinsUrl = "xx";
    public static final String remotePath = "/buildWithParameters";
    public static final String detailPath = "/api/json";


    static{
        encoding = Base64.encodeBase64String(String.format("%s:%s","user","pwd").getBytes());
        header = new HashMap<>();
        header.put("Authorization","Basic "+encoding);
    }

    public static int buildJenkinsJobWithParameters(Map<String,String> paramters){
        return buildJenkinsJobWithParameters(jenkinsUrl,jobName,paramters);
    }

    private static int buildJenkinsJobWithParameters(String jenkinsUrl, String jobName, Map<String, String> paramters) {
        String url = jenkinsUrl + "/job/" +jobName + remotePath;
        try {
            int currentBuildNumber =  getNextBuildNumber(jenkinsUrl,jobName);
//            httpclient.post(url,paramters,header,"body传空");
//            if (httpclient.getStatusCode() == 201){
//                return currentBuildNumber;
//            }
            return -1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    private static int getNextBuildNumber(String jenkinsUrl, String jobName) {
        int nextBuildNumber = -1;
        JSONObject jsonObject = getJenkinsJobDetail(jenkinsUrl,jobName);
        if (jsonObject!=null){
            nextBuildNumber = jsonObject.getIntValue("nextBuildNumber");
        }
        return nextBuildNumber;
    }

    private static JSONObject getJenkinsJobDetail(String jenkinsUrl, String jobName) {
        String request = jenkinsUrl + "/job/" + jobName +detailPath;
        JSONObject jsonObject = null;
        try {
            jsonObject = getJenkinsJob(request);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static JSONObject getJenkinsJob(String request) {
//        httpClient.get(request, null, header);
//        if (httpclient.getStatusCode() == 200) {
//            return JSONObject.parseObject(httpClient.getBody);
//        } else {
//            //log
//        }
        return null;
    }
}

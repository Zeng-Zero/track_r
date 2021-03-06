package com.antilost.app.network;

import android.text.TextUtils;

import java.util.HashMap;

public abstract class Command {

    public static final String SERVER_URL = "http://www.ieasytec.com/communication";
    public static final String LOG_TAG = "Command";
    public static final String LINE_SPLITTER = "\r\n";

    private boolean mStatusBad = false;
    private boolean mNetworkError = false;
    protected HashMap<String, String> mResultMap;


    protected abstract String makeRequestString();

    public boolean execTask() {
        String request = makeRequestString();
        String entity = HttpRequest.sendPost(SERVER_URL, request);
        if(TextUtils.isEmpty(entity)) {
            return false;
        }
        return parseResponse(entity);
    }

    private boolean parseResponse(String entity) {
        try {
            String[] lines = entity.split("\r\n");
            mResultMap = new HashMap<String, String>();
            for(String line: lines) {
                String[] keyValues = line.split(":");
                if(keyValues.length == 2) {
                    mResultMap.put(keyValues[0], keyValues[1]);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            mStatusBad = true;
        }
        return false;
    }

    public boolean success() {
        if(mResultMap == null) {
            return false;
        }

        String status = mResultMap.get("status");
        return "success".equals(status);

    }

    public boolean err() {
        if(mResultMap == null) {
            return false;
        }

        String status = mResultMap.get("status");
        return "err".equals(status);
    }

    public boolean isNetworkError() {
        return mNetworkError;
    }

    public boolean isStatusBad() {
        return  mStatusBad;
    }
}

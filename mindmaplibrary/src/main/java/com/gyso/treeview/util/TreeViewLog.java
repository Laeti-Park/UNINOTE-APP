package com.gyso.treeview.util;

import android.util.Log;

import com.gyso.treeview.BuildConfig;

public class TreeViewLog{
    private static boolean isDebug = BuildConfig.isDebug;
    public static void d(String tag, String msg){
        if(isDebug){
            Log.d(tag, msg);
        }
    }
    public static void e(String tag, String msg){
        if(isDebug){
            Log.e(tag, msg);
        }
    }
}

package king.yunlesao.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Set;

import king.yunlesao.capture.ImageCaptureManager;
import king.yunlesao.translate.TranslateManager;

/**
 * Created by King on 2018/4/30.
 */

public class SettingManager {
    private final static String DEFAULT_SETTING_FILE_NAME="setting";
    private final static int DEFAULT_SCNAF_TYPE=ImageCaptureManager.REQUEST_CODE_GENERAL_BASIC;
    private final static int DEFAULT_TRANSLATE_TYPE=TranslateManager.TRANSLATE_AUTO;
    private final static int DEFAULT_HISTORY_MAX_RECORD=200;

    public final static String KEY_IS_INIT="is_init";
    public final static String KEY_SCANF_TYPE="scanfType";
    public final static String KEY_TRANSLATE_TYPE="translateType";
    public final static String KEY_HISTORY_MAX_RECORD="historyMaxRecord";

    public static void init(Context context){
        SharedPreferences sp=getSetting(context);
        if(sp!=null){
            boolean isInit=sp.getBoolean(KEY_IS_INIT,false);
            if(!isInit){
                SharedPreferences.Editor editor=sp.edit();
                if(editor!=null){
                    editor.putBoolean(KEY_IS_INIT,true);
                    editor.putInt(KEY_SCANF_TYPE,DEFAULT_SCNAF_TYPE);
                    editor.putInt(KEY_TRANSLATE_TYPE,DEFAULT_TRANSLATE_TYPE);
                    editor.putInt(KEY_HISTORY_MAX_RECORD,DEFAULT_HISTORY_MAX_RECORD);
                    editor.commit();
                }else {
                    Toast.makeText(context,"初始化错误！ editor:"+editor,Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(context,"初始化错误！ sp:"+sp,Toast.LENGTH_SHORT).show();
        }
    }

    private static SharedPreferences getSetting(Context context){
        if(context==null)return null;
        else return context.getSharedPreferences(DEFAULT_SETTING_FILE_NAME,Context.MODE_PRIVATE);
    }

    public static int getScanfType(Context context){
        return getSetting(context).getInt(KEY_SCANF_TYPE,DEFAULT_SCNAF_TYPE);
    }

    public static void saveScanfType(Context context,int type){
        SharedPreferences.Editor editor=getSetting(context).edit();
        editor.putInt(KEY_SCANF_TYPE,type);
        Toast.makeText(context,"保存精确度",Toast.LENGTH_SHORT).show();
        editor.commit();
    }

    public static int getTranslateType(Context context){
        return getSetting(context).getInt(KEY_TRANSLATE_TYPE,DEFAULT_TRANSLATE_TYPE);
    }

    public static void saveTranslateType(Context context,int type){
        SharedPreferences.Editor editor=getSetting(context).edit();
        editor.putInt(KEY_TRANSLATE_TYPE,type);
        Toast.makeText(context,"保存翻译类型",Toast.LENGTH_SHORT).show();
        editor.commit();
    }

    public static int getHistoryMaxRecord(Context context){
        return getSetting(context).getInt(KEY_HISTORY_MAX_RECORD,DEFAULT_HISTORY_MAX_RECORD);
    }

    public static void saveHistoryMaxRecord(Context context,int maxRecord){
        SharedPreferences.Editor editor=getSetting(context).edit();
        editor.putInt(KEY_HISTORY_MAX_RECORD,maxRecord);
        editor.commit();
    }
}

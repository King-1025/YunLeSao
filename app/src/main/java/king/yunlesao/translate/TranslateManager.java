package king.yunlesao.translate;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.hamcrest.StringDescription;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import king.yunlesao.R;
import king.yunlesao.ui.iface.TranslateEventNotify;

/**
 * Created by King on 2018/4/5.
 */

public class TranslateManager {
    private Context context;
    private String appId;
    private String securityKey;
    private TransApi api;
    private TranslateEventNotify translateEventNotify;
    public final static int FLAG_TRANSLATE_RESULT=0xC0;
    public final static int FLAG_TRANSLATE_ERROR=0xC1;
    private final static String TAG="TranslateManager";

    public final static int TRANSLATE_AUTO=0xaa;
    public final static int TRANSLATE_ZH_TO_EN=0xa0;
    public final static int TRANSLATE_EN_TO_ZH=0xa1;
    public final static int TRANSLATE_ZH_TO_JP=0xa2;
    public final static int TRANSLATE_JP_TO_ZH=0xa3;

    private final static String KEY_TRANSLATE_RESULT="trans_result";
    private final static String KEY_SOURCE_LANGUAGE="src";
    private final static String KEY_DESTINATION_LANGUAGE="dst";
    private final static String KEY_DESCRIPTION="description";
    public TranslateManager(Context context){
        this.context=context;
        appId=context.getResources().getString(R.string.app_id);
        securityKey=context.getResources().getString(R.string.security_key);
        api = new TransApi(appId, securityKey);
    }

    public void setTranslateEventNotify(TranslateEventNotify translateEventNotify){
        this.translateEventNotify=translateEventNotify;
    }

    public void translateByBaidu(final String query, final String from, final String to){
        if(translateEventNotify!=null){
            synchronized (this){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        String result=api.getTransResult(query,from,to);
                        if(result!=null){
                            try {
                                JSONObject translateResult=new JSONObject(result).
                                        getJSONArray(KEY_TRANSLATE_RESULT).getJSONObject(0);
                                String src=translateResult.getString(KEY_SOURCE_LANGUAGE);
                                String dst=translateResult.getString(KEY_DESTINATION_LANGUAGE);
                                translateEventNotify.onTranslateResult("原文:"+src+"\n译文:"+dst);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                translateEventNotify.onTranslateError("解析失败! result:"+result);
                            }
                        }else{
                            translateEventNotify.onTranslateError("无法解析JSON数据! result:"+result);
                            Log.i(TAG,"无法解析JSON数据，result is "+result);
                        }
                    }
                }.start();
            }
        }else{
            Toast.makeText(context,"translateEventNotify is null.",Toast.LENGTH_SHORT).show();
        }
    }

    public static Bundle getTranslateType(int flag){
        Bundle bundle=new Bundle();
        String src="auto";
        String dst="auto";
        String description="自动";
        switch (flag){
            case TRANSLATE_ZH_TO_EN:
                src="zh";dst="en";
                description="中文->英文";
                break;
            case TRANSLATE_EN_TO_ZH:
                src="en";dst="zh";
                description="英文->中文";
                break;
            case TRANSLATE_ZH_TO_JP:
                src="zh";dst="jp";
                description="中文->日文";
                break;
            case TRANSLATE_JP_TO_ZH:
                src="jp";dst="zh";
                description="日文->中文";
                break;
            case TRANSLATE_AUTO:
            default:Log.i(TAG,"没有此翻译类型,将选择自动类型。flag:"+flag);
                break;
        }
        bundle.putString(KEY_SOURCE_LANGUAGE,src);
        bundle.putString(KEY_DESTINATION_LANGUAGE,dst);
        bundle.putString(KEY_DESCRIPTION,description);
        return bundle;
    }

    public static String getSourceLanguage(Bundle type){
        return find(type,KEY_SOURCE_LANGUAGE,"auto");
    }

    public static String getDestinationLanguage(Bundle type){
        return find(type,KEY_DESTINATION_LANGUAGE,"auto");
    }

    public static String getDescription(Bundle type){
        return find(type,KEY_DESCRIPTION,"自动");
    }

    private static String find(Bundle type, String key,String defStr){
        if(type==null){
            Log.i(TAG,"翻译类型无效，type is "+type);
            return defStr;
        }else{
            return (String) type.getString(key,defStr);
        }
    }
}

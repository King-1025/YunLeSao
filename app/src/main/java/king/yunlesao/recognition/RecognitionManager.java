package king.yunlesao.recognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.OcrRequestParams;
import com.baidu.ocr.sdk.model.OcrResponseResult;
import com.baidu.ocr.sdk.model.Word;
import com.baidu.ocr.sdk.model.WordSimple;

import java.io.File;

import king.yunlesao.capture.ImageCaptureManager;
import king.yunlesao.ui.iface.RecognitionEventNotify;
import king.yunlesao.ui.iface.ServiceListener;

/**
 * Created by King on 2018/4/5.
 */

public class RecognitionManager implements ServiceListener{
    private Context context;
    private final static String TAG="RecognitionManager";
    private boolean hasGotToken;
    private AlertDialog.Builder alertDialog;
    private RecognitionEventNotify recognitionEventNotify;
    public final static int FLAG_RECOGNITION_RESULT=0xB0;
    public final static int FLAG_RECOGNITION_ERROR=0xB1;

    public final static int TYPE_JSON_RES=0xc0;
    public final static int TYPE_WORD_LIST=0xc1;

    private boolean isInitingAccessToken=false;
    private int currentRecognitionTypeValue=0xFF;
    private String currentRecognitionTypeName=null;

    public RecognitionManager(Context context){
        this.context=context;
        hasGotToken=false;
        alertDialog= new AlertDialog.Builder(context);
    }

    public void initAccessToken() {
        if(hasGotToken)return;
        isInitingAccessToken=true;
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
                Log.i(TAG,"licence方式获取token成功");
                //alertText("licence方式获取token成功", "成功！");
                isInitingAccessToken=false;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                hasGotToken=false;
                //Toast.makeText(context,"licence方式获取token失败",Toast.LENGTH_SHORT).show();
                alertText("licence方式获取token失败", error.getMessage());
                isInitingAccessToken=false;
            }
        }, context.getApplicationContext());
    }

    public void initAccessTokenWithAkSk() {
        if(hasGotToken)return;
        isInitingAccessToken=true;
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
                isInitingAccessToken=false;
            }


            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                hasGotToken=false;
                alertText("AK，SK方式获取token失败", error.getMessage());
                isInitingAccessToken=false;
            }
        }, context.getApplicationContext(), "TwLkLRGA3bNhIBdL3EQ7hwxR", "lbUWFmDSOFALsz0CiBpedYIuqPLsbBaM");
    }

    public void recognitionByBaiduOCR(String imagePath){
        recognitionByBaiduOCR(imagePath,ImageCaptureManager.REQUEST_CODE_GENERAL);
    }

    public boolean isInitingAccessToken() {
        return isInitingAccessToken;
    }



    public void recognitionByBaiduOCR(String imagePath , int flag){

            //测试
//            if(recognitionEventNotify!=null){
//                recognitionEventNotify.onRecognitionResult("这是一个识别结果。",TYPE_WORD_LIST);
//                Log.i(TAG,"测试：这是一个识别结果");
//                return;
//            }

           if(ImageCaptureManager.checkImagePath(imagePath)){
               switch(flag){
                   case ImageCaptureManager.REQUEST_CODE_GENERAL:  // 识别成功回调，通用文字识别（含位置信息）
                       RecognizeService.recGeneral(imagePath,this);
                       currentRecognitionTypeName="通用文字识别（含位置信息）";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_GENERAL_BASIC: // 识别成功回调，通用文字识别
                       RecognizeService.recGeneralBasic(imagePath,this);
                       currentRecognitionTypeName="通用文字识别";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_ACCURATE: // 识别成功回调，通用文字识别（含位置信息高精度版）
                       RecognizeService.recAccurate(imagePath,this);
                       currentRecognitionTypeName="通用文字识别（含位置信息高精度版）";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_ACCURATE_BASIC: // 识别成功回调，通用文字识别（高精度版）
                       RecognizeService.recAccurateBasic(imagePath,this);
                       currentRecognitionTypeName="通用文字识别（高精度版）";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_GENERAL_ENHANCED:// 识别成功回调，通用文字识别（含生僻字版）
                       RecognizeService.recGeneralEnhanced(imagePath,this);
                       currentRecognitionTypeName="通用文字识别（含生僻字版）";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_GENERAL_WEBIMAGE:// 识别成功回调，网络图片文字识别
                       RecognizeService.recWebimage(imagePath,this);
                       currentRecognitionTypeName="网络图片文字识别";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_BANKCARD:  // 识别成功回调，银行卡识别
                       RecognizeService.recBankCard(imagePath,this);
                       currentRecognitionTypeName="银行卡识别";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_VEHICLE_LICENSE: // 识别成功回调，行驶证识别
                       RecognizeService.recVehicleLicense(imagePath,this);
                       currentRecognitionTypeName="行驶证识别";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_DRIVING_LICENSE:  // 识别成功回调，驾驶证识别
                       RecognizeService.recDrivingLicense(imagePath,this);
                       currentRecognitionTypeName="驾驶证识别";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_LICENSE_PLATE:   // 识别成功回调，车牌识别
                       RecognizeService.recLicensePlate(imagePath,this);
                       currentRecognitionTypeName="车牌识别";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_BUSINESS_LICENSE:  // 识别成功回调，营业执照识别
                       RecognizeService.recBusinessLicense(imagePath,this);
                       currentRecognitionTypeName="营业执照识别";
                       break;
                   case ImageCaptureManager.REQUEST_CODE_RECEIPT: // 识别成功回调，通用票据识别
                       RecognizeService.recReceipt(imagePath,this);
                       currentRecognitionTypeName="通用票据识别";
                       break;
                   default:
                       Toast.makeText(context,"未知识别类型:"+flag,Toast.LENGTH_LONG).show();
                       currentRecognitionTypeName="未知识别类型";
                       break;
               }
               currentRecognitionTypeValue=flag;
           }else{
               Toast.makeText(context,"error image path:"+imagePath,Toast.LENGTH_LONG).show();
           }
    }

    public int getCurrentRecognitionTypeValue() {
        return currentRecognitionTypeValue;
    }

    public String getCurrentRecognitionTypeName() {
        return currentRecognitionTypeName;
    }

    public String whatType(int typeValue){
        String typeName=null;
        switch(typeValue){
            case ImageCaptureManager.REQUEST_CODE_GENERAL:  // 识别成功回调，通用文字识别（含位置信息）
                typeName="通用文字识别（含位置信息）";
                break;
            case ImageCaptureManager.REQUEST_CODE_GENERAL_BASIC: // 识别成功回调，通用文字识别
                typeName="通用文字识别";
                break;
            case ImageCaptureManager.REQUEST_CODE_ACCURATE: // 识别成功回调，通用文字识别（含位置信息高精度版）
                typeName="通用文字识别（含位置信息高精度版）";
                break;
            case ImageCaptureManager.REQUEST_CODE_ACCURATE_BASIC: // 识别成功回调，通用文字识别（高精度版）
                typeName="通用文字识别（高精度版）";
                break;
            case ImageCaptureManager.REQUEST_CODE_GENERAL_ENHANCED:// 识别成功回调，通用文字识别（含生僻字版）
                typeName="通用文字识别（含生僻字版）";
                break;
            case ImageCaptureManager.REQUEST_CODE_GENERAL_WEBIMAGE:// 识别成功回调，网络图片文字识别
                typeName="网络图片文字识别";
                break;
            case ImageCaptureManager.REQUEST_CODE_BANKCARD:  // 识别成功回调，银行卡识别
                typeName="银行卡识别";
                break;
            case ImageCaptureManager.REQUEST_CODE_VEHICLE_LICENSE: // 识别成功回调，行驶证识别
                typeName="行驶证识别";
                break;
            case ImageCaptureManager.REQUEST_CODE_DRIVING_LICENSE:  // 识别成功回调，驾驶证识别
                typeName="驾驶证识别";
                break;
            case ImageCaptureManager.REQUEST_CODE_LICENSE_PLATE:   // 识别成功回调，车牌识别
                typeName="车牌识别";
                break;
            case ImageCaptureManager.REQUEST_CODE_BUSINESS_LICENSE:  // 识别成功回调，营业执照识别
                typeName="营业执照识别";
                break;
            case ImageCaptureManager.REQUEST_CODE_RECEIPT: // 识别成功回调，通用票据识别
                typeName="通用票据识别";
                break;
            default:
                typeName="未知识别类型";
                break;
        }
        return typeName;
    }
    public void release(){
        OCR.getInstance().release();
    }

    public boolean isHasGotToken() {
        return hasGotToken;
    }

    public void setRecognitionEventNotify(RecognitionEventNotify recognitionEventNotify){
        this.recognitionEventNotify=recognitionEventNotify;
    }

    private void alertText(final String title, final String message) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private void notifyResult(String result,int type){
        if(recognitionEventNotify!=null){
            recognitionEventNotify.onRecognitionResult(result,type);
            //Toast.makeText(context," recognitionEventNotify:onRecognitionResult(result);",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"recognitionEventNotify is null",Toast.LENGTH_SHORT).show();
        }
        //alertText("识别结果",result);
        Log.i(TAG,result);
    }

    @Override
    public void onResultWordList(String result) {
        notifyResult(result,TYPE_WORD_LIST);
    }

    @Override
    public void onResultJsonRes(String result) {
        //notifyResult(result,TYPE_JSON_RES);
    }

    @Override
    public void onError(String error) {
        if(recognitionEventNotify!=null){
            recognitionEventNotify.onRecognitionError(error);
        }
        //alertText("提示","识别结果获取失败！");
        Log.e( TAG,error);
    }

}

package king.yunlesao.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import king.yunlesao.R;
import king.yunlesao.capture.ImageCaptureManager;
import king.yunlesao.recognition.RecognitionManager;
import king.yunlesao.translate.TranslateManager;
import king.yunlesao.ui.iface.MainActionListener;
import king.yunlesao.ui.iface.RecognitionEventNotify;
import king.yunlesao.ui.iface.TranslateEventNotify;
import king.yunlesao.utils.SettingManager;
import king.yunlesao.utils.Tools;

/**
 * Created by King on 2018/1/3.
 * 自动模式碎片
 */

public class AutoModeFragment extends AbilityFragment implements
        View.OnClickListener{
    @BindView(R.id.auto_scanf_button)Button scanf;
    @BindView(R.id.back_to)Button backTo;
    @BindView(R.id.go_on)Button goOn;
    @BindView(R.id.result_panel)RelativeLayout resultPanel;
    @BindView(R.id.status)TextView status;
    @BindView(R.id.scanf_result)TextView scanfResult;
    @BindView(R.id.translate_result)TextView translateResult;
    @BindView(R.id.scanf_image)ImageView scanfImage;

    private final static String TAG="AutoModeFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.auto_mode_fragment,container,false);
        unbinder= ButterKnife.bind(this,view);
        scanf.setOnClickListener(this);
        goOn.setOnClickListener(this);
        backTo.setOnClickListener(this);
        scanfResult.setOnClickListener(this);
        translateResult.setOnClickListener(this);
        setNormal(true);
        initActionHandler();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null)unbinder.unbind();
    }

    private void showResultPanel(){
        scanf.setVisibility(View.INVISIBLE);
        resultPanel.setVisibility(View.VISIBLE);
        setNormal(false);
    }

    private void hideResultPanel(){
        resultPanel.setVisibility(View.INVISIBLE);
        scanf.setVisibility(View.VISIBLE);
        setNormal(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.auto_scanf_button:
            case R.id.go_on:
                scanf(getDefaultScanfType());
                break;
            case R.id.back_to:
                hideResultPanel();
                changeHomeFragmentState(HomeFragment.FLAG_NOEMAL);
                break;
            case R.id.scanf_result:
                Tools.copyToClipboard(mActivity.getApplicationContext(),((TextView)v).getText().toString());
                Toast.makeText(mActivity,"识别结果已复制",Toast.LENGTH_SHORT).show();
                break;
            case R.id.translate_result:
                Tools.copyToClipboard(mActivity.getApplicationContext(),((TextView)v).getText().toString());
                Toast.makeText(mActivity,"翻译结果已复制",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void initActionHandler(){
        mActivity=getActivity();
        setActionHandler(new Handler(mActivity.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                String value= (String) msg.obj;
                switch (msg.what){
                    case ImageCaptureManager.FLAG_TAKE_PICTURE_FINISH:
                        handleTakePictureFinish(msg.arg1);
                        break;
                    case ImageCaptureManager.FLAG_TAKE_PICTURE_ERROR:
                        handleTakePictureError();
                        break;
                    case RecognitionManager.FLAG_RECOGNITION_RESULT:
                        handleRecognitionResult(value);
                        break;
                    case RecognitionManager.FLAG_RECOGNITION_ERROR:
                        handleRecognitionError(value);
                        break;
                    case TranslateManager.FLAG_TRANSLATE_RESULT:
                        handleTranslateResult(value);
                        break;
                    case TranslateManager.FLAG_TRANSLATE_ERROR:
                        handleTranslateError(value);
                        break;
                }
            }
        });
    }

    //扫描完成
    private void handleTakePictureFinish(int type){
        String imagePath=getDefaultScanfImagePath();
        changeHomeFragmentState(HomeFragment.FLAG_ONLY_SHOW_HOME_FRAGMENT);

        updateImage(scanfImage,imagePath);

        status.setText("正在识别中...");
        showResultPanel();
        getRecognitionManager().recognitionByBaiduOCR(imagePath,type);
        Log.i(TAG,getRecognitionManager().whatType(type)+":"+type);
    }

    //扫描失败
    private void handleTakePictureError(){
        hideResultPanel();
        changeHomeFragmentState(HomeFragment.FLAG_NOEMAL);
    }
    //处理识别结果
    private void handleRecognitionResult(String result) {
        status.setText("正在翻译中...");
        scanfResult.setText(result);
        Log.i(TAG,"handleRecognitionResult() is called.");
        Bundle type=TranslateManager.getTranslateType(getDefaultTranslateType());
        getTranslateManager().translateByBaidu(result,
                TranslateManager.getSourceLanguage(type),
                TranslateManager.getDestinationLanguage(type));
    }

    //处理识别错误
    private void handleRecognitionError(String error) {
        status.setText("识别出错!");
        scanfResult.setText(error);
    }

    //处理翻译结果
    private void handleTranslateResult(String result) {
        status.setText("翻译成功");
        translateResult.setText(result);
    }

    //处理翻译错误
    private void handleTranslateError(String error) {
        status.setText("翻译出错!");
        translateResult.setText(error);
    }


    @Override
    public void onRecognitionResult(String result, int type) {
        sendSimpleMessage(getActionHandler(),RecognitionManager.FLAG_RECOGNITION_RESULT,result);
        Log.i(TAG,"识别结果:"+result);
    }

    @Override
    public void onRecognitionError(String error) {
        sendSimpleMessage(getActionHandler(),RecognitionManager.FLAG_RECOGNITION_ERROR,error);
        Log.i(TAG,"识别错误:"+error);
    }

    @Override
    public void onTranslateResult(String result) {
        sendSimpleMessage(getActionHandler(),TranslateManager.FLAG_TRANSLATE_RESULT,result);
        Log.i(TAG,"翻译结果："+result);
    }

    @Override
    public void onTranslateError(String error) {
        sendSimpleMessage(getActionHandler(),TranslateManager.FLAG_TRANSLATE_ERROR,error);
        Log.i(TAG,"翻译错误:"+error);
    }

}

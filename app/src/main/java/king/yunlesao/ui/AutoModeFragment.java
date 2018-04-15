package king.yunlesao.ui;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import king.yunlesao.ui.iface.ViewChanger;

/**
 * Created by King on 2018/1/3.
 * 自动模式碎片
 */

public class AutoModeFragment extends AbilityFragment implements
        View.OnClickListener,RecognitionEventNotify,TranslateEventNotify {
    //view绑定
    @BindView(R.id.auto_scanf_button)Button scanf;
    @BindView(R.id.back_to)Button backTo;
    @BindView(R.id.go_on)Button goOn;
    @BindView(R.id.result_panel)RelativeLayout resultPanel;
    @BindView(R.id.status)TextView status;
    @BindView(R.id.scanf_result)TextView scanfResult;
    @BindView(R.id.translate_result)TextView translateResult;
    @BindView(R.id.scanf_image)ImageView scanfImage;
    //图片捕捉，识别，翻译管理器
    private ImageCaptureManager icm;
    private RecognitionManager rm;
    private TranslateManager tm;
    private final static String TAG="AutoModeFragment";
    //动作处理Handler
    private Handler mhandler;
    //主Action监听接口，与关联的Activity通信
    private MainActionListener mainActionListener;
    private ViewChanger viewChanger;
    private boolean isNormal;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initManagers();
            initHandler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.auto_mode_fragment,container,false);
        unbinder= ButterKnife.bind(this,view);
        scanf.setOnClickListener(this);
        goOn.setOnClickListener(this);
        backTo.setOnClickListener(this);
        isNormal=true;
        return view;
    }

    private void initManagers(){
        //获取关联的Activity
        mActivity=getActivity();
        //初始化图片捕捉，识别，翻译管理器
        icm=new ImageCaptureManager(mActivity);
        rm=new RecognitionManager(mActivity);
        tm=new TranslateManager(mActivity);
        //设置识别事件回调接口
        rm.setRecognitionEventNotify(this);
        //设置翻译事件回调接口
        tm.setTranslateEventNotify(this);
        //初始化主Action监听接口
        mainActionListener= (MainActionListener) mActivity;
        //rm.initAccessToken();
        rm.initAccessTokenWithAkSk();
    }

    private void initHandler(){
        mhandler=new Handler(mActivity.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ImageCaptureManager.FLAG_TAKE_PICTURE_FINISH:
                        //请求改变视图
                        viewChanger.changeViewVisibility(HomeFragment.FLAG_ONLY_SHOW_HOME_FRAGMENT,icm.getImagePath());
                        updateImage(icm.getImagePath());
                        status.setText("正在识别中...");
                        showResultPanel();
                        rm.recognitionByBaiduOCR(icm.getImagePath());
                        break;
                    case ImageCaptureManager.FLAG_TAKE_PICTURE_ERROR:
                        hideResultPanel();
                        viewChanger.changeViewVisibility(HomeFragment.FLAG_NOEMAL,null);
                        break;
                    case RecognitionManager.FLAG_RECOGNITION_RESULT:
                        status.setText("正在翻译中...");
                        scanfResult.setText((String)msg.obj);
                        tm.translateByBaidu((String)msg.obj,"auto","en");
                        break;
                    case RecognitionManager.FLAG_RECOGNITION_ERROR:
                        status.setText("识别出错!");
                        scanfResult.setText((String)msg.obj);
                        break;
                    case TranslateManager.FLAG_TRANSLATE_RESULT:
                        status.setText("翻译成功");
                        translateResult.setText((String)msg.obj);
                        break;
                    case TranslateManager.FLAG_TRANSLATE_ERROR:
                        status.setText("翻译出错!");
                        translateResult.setText((String)msg.obj);
                        break;
                }
            }
        };
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null)unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rm.release(); //释放识别任务占用的资源
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mActivity,"onRequestPermissionsResult() is called.",Toast.LENGTH_SHORT).show();
            //rm.initAccessToken();
            rm.initAccessTokenWithAkSk();
        } else {
            Toast.makeText(mActivity, "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    private void showResultPanel(){
        scanf.setVisibility(View.INVISIBLE);
        resultPanel.setVisibility(View.VISIBLE);
        isNormal=false;
    }

    private void hideResultPanel(){
        resultPanel.setVisibility(View.INVISIBLE);
        scanf.setVisibility(View.VISIBLE);
        isNormal=true;
    }

    public boolean isNormal() {
        return isNormal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.auto_scanf_button:
            case R.id.go_on:
               if(!rm.isHasGotToken()){
                    Toast.makeText(mActivity,"请检查网络设置！ Token is "+rm.isHasGotToken(),Toast.LENGTH_LONG).show();
                   // rm.initAccessToken();
                    return;
                }
                if(mainActionListener!=null) {
                    //获取图片保存路径
                    String path=icm.getImagePath();
                    Log.i(TAG,"imagePath:"+path);
                    Bundle bundle=new Bundle();
                    bundle.putString(ImageCaptureManager.IMAGE_SAVE_PATH,path);
                    //请求拍照动作
                    mainActionListener.onActionRequest(mhandler,ImageCaptureManager.BAIDU_CAMERA_REQUEST_CODE_GENERAL_BASIC,bundle);
                }else{
                    Toast.makeText(mActivity,"MainActionListener is null.",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.back_to:
                hideResultPanel();
                viewChanger.changeViewVisibility(HomeFragment.FLAG_NOEMAL,null);
                break;
        }
    }

    @Override
    public void onRecognitionResult(String result) {
        sendSimpleMessage(mhandler,RecognitionManager.FLAG_RECOGNITION_RESULT,result);
    }

    @Override
    public void onRecognitionError(String error) {
        Log.i(TAG,"识别错误:"+error);
        sendSimpleMessage(mhandler,RecognitionManager.FLAG_RECOGNITION_ERROR,error);
    }

    @Override
    public void onTranslateResult(String result) {
        sendSimpleMessage(mhandler,TranslateManager.FLAG_TRANSLATE_RESULT,result);
    }

    @Override
    public void onTranslateError(String error) {
        Log.i(TAG,"翻译错误:"+error);
        sendSimpleMessage(mhandler,TranslateManager.FLAG_TRANSLATE_ERROR,error);
    }

    public void setViewChanger(ViewChanger viewChanger){
        this.viewChanger=viewChanger;
    }

    private void sendSimpleMessage(Handler handler,int what,Object obj){
        Message msg=handler.obtainMessage();
        msg.what=what;
        msg.obj=obj;
        handler.sendMessage(msg);
    }

    private void updateImage(String path){
        Toast.makeText(mActivity,"更新图片"+path,Toast.LENGTH_SHORT).show();
        if(path==null){
            scanfImage.setImageBitmap(null);
        }else{
            Bitmap bitmap= BitmapFactory.decodeFile(path);
            scanfImage.setImageBitmap(bitmap);
        }
    }

}

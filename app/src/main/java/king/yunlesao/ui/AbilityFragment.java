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
import android.widget.ImageView;
import android.widget.Toast;

import king.yunlesao.capture.ImageCaptureManager;
import king.yunlesao.recognition.RecognitionManager;
import king.yunlesao.translate.TranslateManager;
import king.yunlesao.ui.iface.MainActionListener;
import king.yunlesao.ui.iface.RecognitionEventNotify;
import king.yunlesao.ui.iface.TranslateEventNotify;
import king.yunlesao.ui.iface.ViewChanger;

/**
 * Created by King on 2018/1/3.
 * AbilityFragment作为功能碎片布局的父类
 */

public class AbilityFragment extends BasedFragment implements RecognitionEventNotify,TranslateEventNotify {

    //主线程处理Handler
    private Handler actionHandler;
    private static ViewChanger viewChanger;
    private static MainActionListener mainActionListener;

    //图片捕捉，识别，翻译管理器
    private static ImageCaptureManager imageCaptureManager;
    private static RecognitionManager recognitionManager;
    private static TranslateManager translateManager;

    private boolean isNormal=true;
    private static boolean isInit=false;
    private static boolean isRelease=false;
    private final static String TAG="AbilityFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isInit){
            initAllManagers();
            isInit=true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!isRelease){
            recognitionManager.release(); //释放识别任务占用的资源
            isRelease=true;
        }
    }

    private void initAllManagers(){
        //获取关联的Activity
        mActivity=getActivity();
        //初始化图片捕捉，识别，翻译管理器
        imageCaptureManager=new ImageCaptureManager(mActivity);
        recognitionManager=new RecognitionManager(mActivity);
        translateManager=new TranslateManager(mActivity);
        //初始化主Action监听接口
        setMainActionListener((MainActionListener)mActivity);
        //获取Token
        recognitionManager.initAccessToken();
        //recognitionManager.initAccessTokenWithAkSk();
    }

    public static void setManagerForFragment(AbilityFragment abilityFragment){
        //设置识别事件回调接口
        getRecognitionManager().setRecognitionEventNotify(abilityFragment);
        //设置翻译事件回调接口
        getTranslateManager().setTranslateEventNotify(abilityFragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mActivity,"onRequestPermissionsResult() is called.",Toast.LENGTH_SHORT).show();
            recognitionManager.initAccessToken();
            //recognitionManager.initAccessTokenWithAkSk();
        } else {
            Toast.makeText(mActivity, "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    public void sendSimpleMessage(Handler handler,int what,Object obj){
        Message msg=handler.obtainMessage();
        msg.what=what;
        msg.obj=obj;
        handler.sendMessage(msg);
    }

    public boolean checkAndUpdateRecognitionManagerState(){
        if(!getRecognitionManager().isHasGotToken()){
            if(!getRecognitionManager().isInitingAccessToken())
            {
                getRecognitionManager().initAccessToken();
            }
        }
        return getRecognitionManager().isHasGotToken();
    }

    public Bundle getDefaultTranslateType(){
        return TranslateManager.getTranslateType(TranslateManager.TRANSLATE_ZH_TO_EN);
    }

    public String getDefaultScanfImagePath(){
        return getImageCaptureManager().getImagePath();
    }

    public Bundle getDefaultImagePathBundle(){
        //获取图片保存路径
        String path=getImageCaptureManager().getImagePath();
        Log.i(TAG,"imagePath:"+path);
        Bundle bundle=new Bundle();
        bundle.putString(ImageCaptureManager.IMAGE_SAVE_PATH,path);
        return bundle;
    }

    public void changeHomeFragmentState(int flag){
        getViewChanger().changeViewVisibility(flag,getDefaultScanfImagePath());
    }

    public static ImageCaptureManager getImageCaptureManager() {
        return imageCaptureManager;
    }

    public static RecognitionManager getRecognitionManager() {
        return recognitionManager;
    }

    public static TranslateManager getTranslateManager() {
        return translateManager;
    }

    public boolean isNormal() {
        return isNormal;
    }

    public void setNormal(boolean normal) {
        isNormal = normal;
    }

    public static ViewChanger getViewChanger() {
        return viewChanger;
    }

    public static void setViewChanger(ViewChanger viewChanger) {
        AbilityFragment.viewChanger = viewChanger;
    }

    public Handler getActionHandler() {
        return actionHandler;
    }

    public void setActionHandler(Handler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public MainActionListener getMainActionListener() {
        return mainActionListener;
    }

    public void setMainActionListener(MainActionListener mainActionListener) {
        this.mainActionListener = mainActionListener;
    }

    protected void scanf(int type){

//        if(1==1){
//            getMainActionListener().onActionRequest(getActionHandler(),type,getDefaultImagePathBundle());
//            return;
//        }

        if(checkAndUpdateRecognitionManagerState()){
            if(getMainActionListener()!=null){
                getMainActionListener().onActionRequest(getActionHandler(),type,getDefaultImagePathBundle());
            }else{
                Toast.makeText(mActivity,"MainActionListener is null.",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getActivity(), "请检查网络设置！ Token is "+getRecognitionManager().isHasGotToken(), Toast.LENGTH_LONG).show();
        }
    }

    public void updateImage(ImageView iv, String path){
        if(ImageCaptureManager.checkImagePath(path)){
            Bitmap bitmap= BitmapFactory.decodeFile(path);
            iv.setImageBitmap(bitmap);
        }else{
            iv.setImageBitmap(null);
            Toast.makeText(mActivity,"更新图片失败！"+path,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTranslateResult(String result) {

    }

    @Override
    public void onTranslateError(String error) {

    }

    @Override
    public void onRecognitionResult(String result, int type) {

    }

    @Override
    public void onRecognitionError(String error) {

    }
}

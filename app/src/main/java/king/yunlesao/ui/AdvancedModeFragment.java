package king.yunlesao.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import king.yunlesao.utils.Tools;

/**
 * Created by King on 2018/1/3.
 * 高级模式碎片
 */

public class AdvancedModeFragment extends AbilityFragment implements
        View.OnClickListener, RadioGroup.OnCheckedChangeListener{


    @BindView(R.id.advanced_container)FrameLayout container;
    @BindView(R.id.advanced_based_panel)LinearLayout basedPanel;
    @BindView(R.id.advanced_content_panel)RelativeLayout contentPanel;
    @BindView(R.id.advanced_control_panel)RelativeLayout controlPanel;
    @BindView(R.id.based_scanf_button)Button btScanf;
    @BindView(R.id.based_recognition_button)Button btRecognition;
    @BindView(R.id.based_translate_button)Button btTranslate;
    @BindView(R.id.based_word_button)Button btWord;
    @BindView(R.id.advanced_back_button)Button back;
    @BindView(R.id.advanced_next_button)Button next;
    @BindView(R.id.advanced_ability_button)Button ability;
    private final static int STATE_NORMAL=0xa0;
    private final static int STATE_CONTENT=0xa1;

    private final static int FLAG_SCANF_PANEL=0xb0;
    private final static int FLAG_RECOGNITION_PANEL=0xb1;
    private final static int FLAG_TRANSLATE_PANEL=0xb2;
    private final static int FLAG_WORD_PANEL=0xb3;
    private final static int FLAG_WITHOUT_PANEL=0xb4;

    private final static int FLAG_DIALOG_SCANF_TYPE=-0xc0;
    private final static int FLAG_DIALOG_TRANSLATE_TYPE=0xc1;
    private final static String TAG="AdvancedModeFragment";

    private LayoutInflater panelInflater;
    private int currentPanelFlag;
    private View currentPanel;
    private int scanfType;
    private String scanfImagePath;
    private String recoginitionResult;
    private String translateResult;
    private boolean isRecognition=false;
    private boolean isTranslate=false;
    private boolean isFromScanf=false;
    private Bundle translateType;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.advanced_mode_fragment,container,false);
        unbinder= ButterKnife.bind(this,view);
        btScanf.setOnClickListener(this);
        btRecognition.setOnClickListener(this);
        btTranslate.setOnClickListener(this);
        btWord.setOnClickListener(this);
        back.setOnClickListener(this);
        next.setOnClickListener(this);
        ability.setOnClickListener(this);
        panelInflater=inflater;
        initActionHandler();
        showPanel(FLAG_WITHOUT_PANEL);
        translateType=getDefaultTranslateType();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null)unbinder.unbind();
    }

    private void initActionHandler(){
        mActivity=getActivity();
        setActionHandler(new Handler(mActivity.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                String value= (String) msg.obj;
                isRecognition=false;
                isTranslate=false;
                switch (msg.what){
                    case ImageCaptureManager.FLAG_TAKE_PICTURE_FINISH:
                        scanfImagePath=getDefaultScanfImagePath();
                        recognition();
                        Log.i(TAG,"扫描成功，开始识别");
                        Toast.makeText(mActivity,"扫描成功！",Toast.LENGTH_SHORT).show();
                        break;
                    case ImageCaptureManager.FLAG_TAKE_PICTURE_ERROR:
                        Toast.makeText(mActivity,"扫描失败！",Toast.LENGTH_SHORT).show();
                        break;
                    case RecognitionManager.FLAG_RECOGNITION_RESULT:
                        recoginitionResult=value;
                        showPanel(FLAG_RECOGNITION_PANEL);
                        Toast.makeText(mActivity,"识别成功!",Toast.LENGTH_SHORT).show();
                        break;
                    case RecognitionManager.FLAG_RECOGNITION_ERROR:
                        showPanel(FLAG_RECOGNITION_PANEL);
                        Toast.makeText(mActivity,"识别失败！",Toast.LENGTH_SHORT).show();
                        break;
                    case TranslateManager.FLAG_TRANSLATE_RESULT:
                        translateResult=value;
                        showPanel(FLAG_TRANSLATE_PANEL);
                        Toast.makeText(mActivity,"翻译成功！",Toast.LENGTH_SHORT).show();
                        break;
                    case TranslateManager.FLAG_TRANSLATE_ERROR:
                        showPanel(FLAG_TRANSLATE_PANEL);
                        Toast.makeText(mActivity,"翻译失败！",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.based_scanf_button:
                isFromScanf=true;
                showPanel(FLAG_SCANF_PANEL);
                break;
            case R.id.based_recognition_button:
                showPanel(FLAG_RECOGNITION_PANEL);
                break;
            case R.id.based_translate_button:
                showPanel(FLAG_TRANSLATE_PANEL);
                break;
            case R.id.based_word_button:
                showPanel(FLAG_WORD_PANEL);
                break;
            case R.id.advanced_back_button:
                doBack(currentPanelFlag);
                break;
            case R.id.advanced_next_button:
                doNext(currentPanelFlag);
                break;
            case R.id.advanced_ability_button:
                useAbility(currentPanelFlag);
                break;
            case R.id.recgnition_change_button:
                showDialog(FLAG_DIALOG_SCANF_TYPE);
                break;
            case R.id.translate_change_button:
                showDialog(FLAG_DIALOG_TRANSLATE_TYPE);
                break;
        }
    }
    private void doBack(int flag){
        if(!isFromScanf){
            showPanel(FLAG_WITHOUT_PANEL);
            return;
        }
        switch(flag){
            case FLAG_SCANF_PANEL:
                isFromScanf=false;
                showPanel(FLAG_WITHOUT_PANEL);
                break;
            case FLAG_RECOGNITION_PANEL:
                showPanel(FLAG_SCANF_PANEL);
                break;
            case FLAG_TRANSLATE_PANEL:
                showPanel(FLAG_RECOGNITION_PANEL);
                break;
            case FLAG_WORD_PANEL:
                showPanel(FLAG_TRANSLATE_PANEL);
                break;
        }
    }
    private void doNext(int flag){
        switch(flag){
            case FLAG_SCANF_PANEL:
                scanf(scanfType);
                Log.i(TAG,"scanf() is called");
                break;
            case FLAG_RECOGNITION_PANEL:
                translate();
                break;
            case FLAG_TRANSLATE_PANEL:
                showPanel(FLAG_WORD_PANEL);
                break;
            case FLAG_WORD_PANEL:
                Toast.makeText(mActivity,"添加词条:"+recoginitionResult+"->"+translateResult,Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void translate(){
        if(!isTranslate){
            isTranslate=true;
            getTranslateManager().translateByBaidu(recoginitionResult,
                    TranslateManager.getSourceLanguage(translateType),
                    TranslateManager.getDestinationLanguage(translateType));
            Log.i(TAG,"翻译方向：src:"+TranslateManager.getSourceLanguage(translateType)+" "+
                                    "dst:"+TranslateManager.getDestinationLanguage(translateType));
            Log.i(TAG,"translate()->recognitionResult:"+recoginitionResult);
        }else{
            Toast.makeText(mActivity,"请等待，正在翻译中...",Toast.LENGTH_SHORT).show();
        }
    }
    private void recognition(){
        if(!isRecognition){
            isRecognition=true;
            getRecognitionManager().recognitionByBaiduOCR(scanfImagePath,scanfType);
            Log.i(TAG,"scnafImagePath:"+scanfImagePath+"\n"+
                    "scanfType:"+getRecognitionManager().whatType(scanfType));
        }else{
            Toast.makeText(mActivity,"请等待，正在识别中...",Toast.LENGTH_SHORT).show();
        }
    }
    private void useAbility(int flag){
            switch(flag){
                case FLAG_RECOGNITION_PANEL:
                    recognition();
                    break;
                case FLAG_TRANSLATE_PANEL:
                    recoginitionResult=((EditText)fetch(currentPanel,R.id.source_result)).getText().toString();
                    translate();
                    break;
            }
    }

    private View showPanel(int flag){
        View view=null;
        if(flag==FLAG_WITHOUT_PANEL){
            switchState(STATE_NORMAL);
        }else{
            switchState(STATE_CONTENT);
        }
        if(isFromScanf&&flag!=FLAG_SCANF_PANEL){
            back.setText("上一步");
        }else {
            back.setText("返回");
        }
        switch (flag){
            case FLAG_SCANF_PANEL:
                view=makeScanfPanel();
                next.setText("捕捉");
                ability.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
                currentPanelFlag=FLAG_SCANF_PANEL;
                break;
            case FLAG_RECOGNITION_PANEL:
                view=makeRecognitionPanel();
                ability.setText("识别");
                next.setText("下一步");
                ability.setVisibility(View.VISIBLE);
                currentPanelFlag=FLAG_RECOGNITION_PANEL;
                break;
            case FLAG_TRANSLATE_PANEL:
                view=makeTranslatePanel();
                ability.setText("翻译");
                next.setText("下一步");
                ability.setVisibility(View.VISIBLE);
                currentPanelFlag=FLAG_TRANSLATE_PANEL;
                break;
            case FLAG_WORD_PANEL:
                view=makeWordPanel();
                next.setText("添加");
                ability.setVisibility(View.INVISIBLE);
                currentPanelFlag=FLAG_WORD_PANEL;
                break;
            case FLAG_WITHOUT_PANEL:
                view=null;
                currentPanelFlag=FLAG_WITHOUT_PANEL;
                break;

        }
        if(view!=null){
            if(!("panel:"+currentPanelFlag).equals(view.getTag())){
                view.setTag("panel:"+currentPanelFlag);
                contentPanel.removeAllViews();
                contentPanel.addView(view);
                currentPanel=view;
            }
        }
        return view;
    }

    //调整状态
    private void switchState(int state){
        switch (state){
            case STATE_NORMAL:
                if(isNormal())return;
                basedPanel.setVisibility(View.VISIBLE);
                contentPanel.setVisibility(View.INVISIBLE);
                controlPanel.setVisibility(View.INVISIBLE);
                changeHomeFragmentState(HomeFragment.FLAG_NOEMAL);
                break;
            case STATE_CONTENT:
                if(!isNormal())return;
                basedPanel.setVisibility(View.INVISIBLE);
                contentPanel.setVisibility(View.VISIBLE);
                controlPanel.setVisibility(View.VISIBLE);
                changeHomeFragmentState(HomeFragment.FLAG_ONLY_SHOW_HOME_FRAGMENT);
                break;
        }
        if(state==STATE_NORMAL)setNormal(true);
        else setNormal(false);
    }

    private View makeScanfPanel(){
        View parent=panelInflater.inflate(R.layout.scanf_panel,contentPanel,false);
        ((RadioGroup) fetch(parent,R.id.group_type)).setOnCheckedChangeListener(this);
        ((RadioButton) fetch(parent,R.id.rd_0)).setChecked(true);
        scanfType=ImageCaptureManager.REQUEST_CODE_ACCURATE_BASIC;
        return parent;
    }
    private View makeRecognitionPanel(){
        View parent=null;
        if(currentPanelFlag!=FLAG_RECOGNITION_PANEL){
            parent=panelInflater.inflate(R.layout.recognition_panel,contentPanel,false);
        }else{
            parent=currentPanel;
        }
        updateImage((ImageView)fetch(parent,R.id.recgnition_scanf_image),scanfImagePath);
        ((Button)fetch(parent,R.id.recgnition_change_button)).setOnClickListener(this);
        ((TextView) fetch(parent,R.id.scanf_type_info)).setText(getRecognitionManager().whatType(scanfType));
        TextView textView=(TextView) fetch(parent,R.id.recgnition_result);
        textView.setText(recoginitionResult);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Tools.copyToClipboard(mActivity.getApplicationContext(),recoginitionResult);
                Toast.makeText(mActivity,"结果已复制",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return parent;
    }
    private View makeTranslatePanel(){
        View parent=null;
        if(currentPanelFlag!=FLAG_TRANSLATE_PANEL){
            parent=panelInflater.inflate(R.layout.translate_panel,contentPanel,false);
        }else{
            parent=currentPanel;
        }
        fetch(parent,R.id.translate_change_button).setOnClickListener(this);
        ((EditText) fetch(parent,R.id.source_result)).setText(recoginitionResult);
        ((EditText) fetch(parent,R.id.translate_result)).setText(translateResult);
        return parent;
    }
    private View makeWordPanel(){
        View parent=panelInflater.inflate(R.layout.word_panel,contentPanel,false);
        return parent;
    }

    private View fetch(View parent,int viewId){
        return parent.findViewById(viewId);
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId){
            case R.id.rd_0:
                scanfType=ImageCaptureManager.REQUEST_CODE_GENERAL_BASIC;
                break;
            case R.id.rd_1:
                scanfType=ImageCaptureManager.REQUEST_CODE_ACCURATE_BASIC;
                break;
            case R.id.rd_2:
                scanfType=ImageCaptureManager.REQUEST_CODE_GENERAL_ENHANCED;
                break;
            case R.id.rd_3:
                scanfType=ImageCaptureManager.REQUEST_CODE_GENERAL_WEBIMAGE;
                break;
            case R.id.rd_4:
                scanfType=ImageCaptureManager.REQUEST_CODE_BANKCARD;
                break;
            case R.id.rd_5:
                scanfType=ImageCaptureManager.REQUEST_CODE_VEHICLE_LICENSE;
                break;
            case R.id.rd_6:
                scanfType=ImageCaptureManager.REQUEST_CODE_DRIVING_LICENSE;
                break;
            case R.id.rd_7:
                scanfType=ImageCaptureManager.REQUEST_CODE_LICENSE_PLATE;
                break;
            case R.id.rd_8:
                scanfType=ImageCaptureManager.REQUEST_CODE_BUSINESS_LICENSE;
                break;
            case R.id.rd_9:
                scanfType=ImageCaptureManager.REQUEST_CODE_RECEIPT;
                break;
            case R.id.auto:
                translateType=TranslateManager.getTranslateType(TranslateManager.TRANSLATE_AUTO);
                break;
            case R.id.zh_to_en:
                translateType=TranslateManager.getTranslateType(TranslateManager.TRANSLATE_ZH_TO_EN);
                break;
            case R.id.en_to_zh:
                translateType=TranslateManager.getTranslateType(TranslateManager.TRANSLATE_EN_TO_ZH);
                break;
            case R.id.zh_to_jp:
                translateType=TranslateManager.getTranslateType(TranslateManager.TRANSLATE_ZH_TO_JP);
                break;
            case R.id.jp_to_zh:
                translateType=TranslateManager.getTranslateType(TranslateManager.TRANSLATE_JP_TO_ZH);
                break;
        }
       //Toast.makeText(mActivity,"scanfType:"+scanfType,Toast.LENGTH_SHORT).show();
    }

    private void showDialog(int flag){
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        View layout=null;
        String title=null;
            switch(flag){
                case FLAG_DIALOG_SCANF_TYPE:
                    layout=panelInflater.inflate(R.layout.rg_scanf_type,contentPanel,false);
                    title="请选择识别类型：";
                    ((RadioGroup) fetch(layout,R.id.group_type)).setOnCheckedChangeListener(this);
                    ((RadioButton) fetch(layout,R.id.rd_0)).setChecked(true);
                    scanfType=ImageCaptureManager.REQUEST_CODE_ACCURATE_BASIC;
                    break;
                case FLAG_DIALOG_TRANSLATE_TYPE:
                    layout=panelInflater.inflate(R.layout.rg_translate_type,contentPanel,false);
                    title="请选择翻译方式：";
                    ((RadioGroup) fetch(layout,R.id.translate_group_type)).setOnCheckedChangeListener(this);
                    ((RadioButton) fetch(layout,R.id.zh_to_en)).setChecked(true);
                    translateType=TranslateManager.getTranslateType(TranslateManager.TRANSLATE_ZH_TO_EN);
                    break;
        }
        builder.setTitle(title);
        builder.setView(layout);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(mActivity,"扫描类型:"+scanfType+" "+
                            "翻译方向：src:"+TranslateManager.getSourceLanguage(translateType)+" "+
                                    "dst:"+TranslateManager.getDestinationLanguage(translateType),Toast.LENGTH_SHORT).show();
                if(currentPanelFlag==FLAG_RECOGNITION_PANEL){
                    ((TextView) fetch(currentPanel,R.id.scanf_type_info)).setText(getRecognitionManager().whatType(scanfType));
                }else if(currentPanelFlag==FLAG_TRANSLATE_PANEL){
                    ((TextView) fetch(currentPanel,R.id.translate_type_info)).setText(TranslateManager.getDescription(translateType));
                }
            }
        });
        builder.create().show();
    }
}

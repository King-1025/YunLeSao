package king.yunlesao.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import butterknife.BindView;
import butterknife.ButterKnife;
import king.yunlesao.R;
import king.yunlesao.capture.ImageCaptureManager;
import king.yunlesao.recognition.RecognitionManager;
import king.yunlesao.translate.TranslateManager;
import king.yunlesao.ui.iface.MainActionListener;
import king.yunlesao.ui.iface.ModeSwitcher;
import king.yunlesao.ui.iface.OnResetHistoryListener;
import king.yunlesao.utils.SettingManager;
import king.yunlesao.word.HistoryManager;

/**
 * Created by King on 2018/3/26.
 * 主页
 */

public class MainActivity extends BasedActivity implements MainActionListener,RadioGroup.OnCheckedChangeListener {

    //总体布局
    @BindView(R.id.main_layout)DrawerLayout drawer;
    //工具栏
    @BindView(R.id.main_toolbar)Toolbar toolbar;
    //侧边栏
    @BindView(R.id.left_navigation)NavigationView leftNavigationView;
    //底部导航栏
    @BindView(R.id.bottom_navigation)BottomNavigationView bottomNavigationView;

    private Context context;
    private HomeFragment homeFragment;
    private HistoryFragment historyFragment;
    private int currentflag;
    private BasedFragment currentFragment;
    private final static String TAG="MainActivity";
    private final static int FLAG_HOME_FRAGMENT=0x00;
    private final static int FLAG_HISTORY_FRAGMENT=0x01;

    private final static int DIALOG_RECOGNITION_TYPE=0xa0;
    private final static int DIALOG_TRANSLATE_TYPE=0xa1;
    private final static int DIALOG_ABOUT_US=0xa2;

    private int scanfType;
    private int translateType;

    //主事件处理handler
    private Handler mainHandler;
    //Action处理Handler
    private Handler actionHandler;

    //模式转换回调接口
    private ModeSwitcher modeSwitcher;
    private ActionBarDrawerToggle toggle;

    private int dialogFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolBarNoTitle(toolbar);
        initLeftNavigation();
        initBottomNavigation();
        if(savedInstanceState==null){
            homeFragment= (HomeFragment) BasedFragment.makeFragment(BasedFragment.HOME_FRAGMENT);
            historyFragment= (HistoryFragment) BasedFragment.makeFragment(BasedFragment.HISTORY_FRAGMENT);
        }
        show(FLAG_HOME_FRAGMENT);
        setModeSwitcher(homeFragment);
        initHandler();
    }

    public void setModeSwitcher(ModeSwitcher modeSwitcher){
        this.modeSwitcher=modeSwitcher;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_theme:
                Toast.makeText(getApplicationContext(),"主题",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_recognition_type:
                showAlertDialog(DIALOG_RECOGNITION_TYPE);
                Toast.makeText(getApplicationContext(),"精确度",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_translate_type:
                showAlertDialog(DIALOG_TRANSLATE_TYPE);
                Toast.makeText(getApplicationContext(),"翻译方式",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_clean_history:
                HistoryManager.reset(this);
                Toast.makeText(getApplicationContext(),"已清空历史！",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_more_setting:
                Toast.makeText(getApplicationContext(),"更多设置",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_auto_mode:
                modeSwitcher.requestModeChange(HomeFragment.MODE_CHANGE_AUTO);
                break;
            case R.id.nav_advanced_mode:
                modeSwitcher.requestModeChange(HomeFragment.MODE_CHANGE_ADVANCED);
                break;
            case R.id.nav_using_help:
                startActivity(new Intent(MainActivity.this,UsingHelpActivity.class));
                break;
            case R.id.nav_about:
                showAlertDialog(DIALOG_ABOUT_US);
                break;
            case R.id.bottom_navigation_home:
                show(FLAG_HOME_FRAGMENT);
                return true;
            case R.id.bottom_navigation_history:
                show(FLAG_HISTORY_FRAGMENT);
                return true;
        }
        //侧边栏自动关闭
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initLeftNavigation(){
        //侧边栏初始化
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //侧边栏菜单启用监听
        leftNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initBottomNavigation(){
        //底部导航栏启用监听
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void show(int flag){
        switch (flag){
            case FLAG_HOME_FRAGMENT:
                currentFragment=show(currentFragment,homeFragment,R.id.view_content);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                setToolbarVisibility(true);
                break;
            case FLAG_HISTORY_FRAGMENT:
                currentFragment=show(currentFragment,historyFragment,R.id.view_content);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                setToolbarVisibility(false);
                break;
        }
        currentflag=flag;
    }

    private void removeAllFragment(){
        remove(homeFragment);
        remove(historyFragment);
    }
    public void setToolbarVisibility(boolean isVisibility){
        setViewVisibility(toolbar,isVisibility);
    }

    public void setbottomNavigationVisibility(boolean isVisibility){
        setViewVisibility(bottomNavigationView,isVisibility);
    }

    public void setleftNavigationVisibility(boolean isVisibility){
        setViewVisibility(leftNavigationView,isVisibility);
    }

    private void setViewVisibility(View v,boolean isVisibility){
        if(v==null)return;
        if(isVisibility)v.setVisibility(View.VISIBLE);
        else v.setVisibility(View.INVISIBLE);
    }

    private void initHandler(){
        mainHandler=new Handler(this.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ImageCaptureManager.BAIDU_CAMERA:

                        // test(msg.arg1);

                        String imagePath=msg.getData().getString(ImageCaptureManager.IMAGE_SAVE_PATH);
                        Log.i(TAG,"imagePath:"+imagePath);
                        //Toast.makeText(MainActivity.this,"imagePath:"+imagePath,Toast.LENGTH_LONG).show();
                        if(!ImageCaptureManager.
                                takePictureByBaiduCamera(MainActivity.this,imagePath,msg.arg1)){
                            actionHandler.sendEmptyMessage(ImageCaptureManager.FLAG_TAKE_PICTURE_ERROR);
                        }
                        break;
                }
            }
        };
    }

    private void test(int flag){
        Message msg=actionHandler.obtainMessage();
        msg.what=ImageCaptureManager.FLAG_TAKE_PICTURE_FINISH;
        msg.arg1=flag;
        actionHandler.sendMessage(msg);
        Toast.makeText(MainActivity.this,"图片捕捉完成",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        if(resultCode == Activity.RESULT_OK){
                Message msg=actionHandler.obtainMessage();
                msg.what=ImageCaptureManager.FLAG_TAKE_PICTURE_FINISH;
                msg.arg1=requestCode;
                actionHandler.sendMessage(msg);
                Toast.makeText(MainActivity.this,"图片捕捉完成",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActionRequest(Handler actionHandler,int flag, Bundle args) {
        if(actionHandler==null){
            Toast.makeText(MainActivity.this,"actionHandler is null.",Toast.LENGTH_LONG).show();
            return;
        }
        this.actionHandler=actionHandler;
        Message msg=mainHandler.obtainMessage();
        msg.what=ImageCaptureManager.BAIDU_CAMERA;
        msg.arg1=flag;
        msg.setData(args);
        mainHandler.sendMessage(msg);
    }

    private void showAlertDialog(int type){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        String title=null;
        View view=null;
        switch (type){
            case DIALOG_RECOGNITION_TYPE:
                title="精确度";
                view= getLayoutInflater().inflate(R.layout.accuracy_layout,null);
                ((RadioGroup)view.findViewById(R.id.group_type)).setOnCheckedChangeListener(this);
                ((RadioButton)view.findViewById(R.id.rd_0)).setChecked(true);
                scanfType=ImageCaptureManager.REQUEST_CODE_GENERAL_BASIC;
                dialogFlag=DIALOG_RECOGNITION_TYPE;
                builder.setNegativeButton("取消",null);
                break;
            case DIALOG_TRANSLATE_TYPE:
                title="翻译方式";
                view= getLayoutInflater().inflate(R.layout.rg_translate_type,null);
                ((RadioGroup)view.findViewById(R.id.translate_group_type)).setOnCheckedChangeListener(this);
                ((RadioButton)view.findViewById(R.id.auto)).setChecked(true);
                translateType= TranslateManager.TRANSLATE_AUTO;
                dialogFlag=DIALOG_TRANSLATE_TYPE;
                builder.setNegativeButton("取消",null);
                break;
            case DIALOG_ABOUT_US:
                title="关于我们";
                view= getLayoutInflater().inflate(R.layout.about_us,null);
                dialogFlag=DIALOG_ABOUT_US;
                break;
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialogFlag==DIALOG_RECOGNITION_TYPE){
                    SettingManager.saveScanfType(context,scanfType);
                }else if(dialogFlag==DIALOG_TRANSLATE_TYPE){
                    SettingManager.saveTranslateType(context,translateType);
                }
                Bundle type=TranslateManager.getTranslateType(translateType);
                Toast.makeText(context,"扫描类型:"+scanfType+" "+
                        "翻译方向：src:"+TranslateManager.getSourceLanguage(type)+" "+
                        "dst:"+TranslateManager.getDestinationLanguage(type),Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view);
        builder.setTitle(title);
        builder.create().show();
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
            case R.id.auto:
                translateType=TranslateManager.TRANSLATE_AUTO;
                break;
            case R.id.zh_to_en:
                translateType=TranslateManager.TRANSLATE_ZH_TO_EN;
                break;
            case R.id.en_to_zh:
                translateType=TranslateManager.TRANSLATE_EN_TO_ZH;
                break;
            case R.id.zh_to_jp:
                translateType=TranslateManager.TRANSLATE_ZH_TO_JP;
                break;
            case R.id.jp_to_zh:
                translateType=TranslateManager.TRANSLATE_JP_TO_ZH;
                break;
        }

    }
}

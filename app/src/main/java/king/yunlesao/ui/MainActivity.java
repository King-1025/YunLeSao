package king.yunlesao.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.ui.camera.CameraActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import king.yunlesao.R;
import king.yunlesao.capture.ImageCaptureManager;
import king.yunlesao.recognition.RecognitionManager;
import king.yunlesao.ui.iface.MainActionListener;
import king.yunlesao.ui.iface.ModeSwitcher;

/**
 * Created by King on 2018/3/26.
 * 主页
 */

public class MainActivity extends BasedActivity implements MainActionListener{

    //总体布局
    @BindView(R.id.main_layout)DrawerLayout drawer;
    //工具栏
    @BindView(R.id.main_toolbar)Toolbar toolbar;
    //侧边栏
    @BindView(R.id.left_navigation)NavigationView leftNavigationView;
    //底部导航栏
    @BindView(R.id.bottom_navigation)BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment;
    private HistoryFragment historyFragment;
    private BasedFragment currentFragment;
    private final static String TAG="MainActivity";

    //主事件处理handler
    private Handler mainHandler;
    //Action处理Handler
    private Handler actionHandler;

    //模式转换回调接口
    private ModeSwitcher modeSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolBarNoTitle(toolbar);
        initLeftNavigation();
        initBottomNavigation();
        if(savedInstanceState==null){
            homeFragment= (HomeFragment) BasedFragment.makeFragment(BasedFragment.HOME_FRAGMENT);
            historyFragment= (HistoryFragment) BasedFragment.makeFragment(BasedFragment.HISTORY_FRAGMENT);
        }
        show(homeFragment);
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
            case R.id.nav_camera:
                Toast.makeText(getApplicationContext(),"nav_camera",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_gallery:
                Toast.makeText(getApplicationContext(),"nav_gallery",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_slideshow:
                Toast.makeText(getApplicationContext(),"nav_slideshow",Toast.LENGTH_LONG).show();
                break;
            case R.id.auto_mode:
                modeSwitcher.requestModeChange(HomeFragment.MODE_CHANGE_AUTO);
                break;
            case R.id.advanced_mode:
                modeSwitcher.requestModeChange(HomeFragment.MODE_CHANGE_ADVANCED);
                break;
            case R.id.bottom_navigation_home:
                show(homeFragment);
                return true;
            case R.id.bottom_navigation_history:
                show(historyFragment);
                return true;
        }
        //侧边栏自动关闭
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initLeftNavigation(){
        //侧边栏初始化
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
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

    private void show(BasedFragment basedFragment){
        currentFragment=show(currentFragment,basedFragment,R.id.view_content);
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

                         //test(msg.arg1);

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
}

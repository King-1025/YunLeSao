package king.yunlesao.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import king.yunlesao.R;

public class MainActivity extends BasedActivity {
    //总体布局
    @BindView(R.id.main_layout)DrawerLayout drawer;
    //工具栏
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    //侧边栏
    @BindView(R.id.left_navigation)NavigationView leftNavigationView;
    //底部导航栏
    @BindView(R.id.bottom_navigation)BottomNavigationView bottomNavigationView;

    @OnClick(R.id.auto)void auto(){
        if(test!=null){
            test.change(TEST_AUTO);
        }
    }
    @OnClick(R.id.advance)void advance(){
        if(test!=null){
            test.change(TEST_ADVANCE);
        }
    }
    private HomeFragment homeFragment;
    private HistoryFragment historyFragment;
    private Test test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolBarNoTitle(toolbar);
        initLeftNavigation();
        initBottomNavigation();
        initContentView();
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
            case R.id.bottom_navigation_home:
                replaceFragment(R.id.view_content,homeFragment);
                return true;
            case R.id.bottom_navigation_history:
                replaceFragment(R.id.view_content,historyFragment);
                return true;
        }

        //侧边栏自动关闭
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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
    public void initContentView(){
        homeFragment=HomeFragment.newInstance(0);
        historyFragment=HistoryFragment.newInstance(0);
        setTest(homeFragment);
        replaceFragment(R.id.view_content,homeFragment);
    }
    public void setTest(Test test){
        this.test=test;
    }
}

/*测试部分
 @OnClick(R.id.test_button)void test(){

 if(!hasGotToken){
 Toast.makeText(getApplicationContext(),"hasGotToken is "+hasGotToken,Toast.LENGTH_LONG).show();
 return;
 }
 Intent intent = new Intent(MainActivity.this, CameraActivity.class);
 intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
 new File(getFilesDir(), "pic.jpg").getAbsolutePath());
 intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
 CameraActivity.CONTENT_TYPE_GENERAL);
 startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC);

 }

 private boolean hasGotToken=false;
 private static final int REQUEST_CODE_GENERAL_BASIC = 106;
 private AlertDialog.Builder alertDialog;
 private final static String TAG ="MainActivity";
 @Override
 protected void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 setContentView(R.layout.activity_test);
 ButterKnife.bind(this);
 alertDialog = new AlertDialog.Builder(this);
 initAccessToken();
 //initAccessTokenWithAkSk();
 }

 @Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 super.onActivityResult(requestCode, resultCode, data);
 // 识别成功回调，通用文字识别
 if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
 GeneralBasicParams param = new GeneralBasicParams();
 param.setDetectDirection(true);
 param.setImageFile(new File(getFilesDir(), "pic.jpg"));
 OCR.getInstance().recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
 @Override
 public void onResult(GeneralResult result) {
 StringBuilder sb = new StringBuilder();
 for (WordSimple wordSimple : result.getWordList()) {
 WordSimple word = wordSimple;
 sb.append(word.getWords());
 sb.append("\n");
 }
 alertText("识别结果",sb.toString());
 }

 @Override
 public void onError(OCRError error) {
 Log.e( TAG,error.getMessage());
 }
 });
 }
 }

 @Override
 public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
 @NonNull int[] grantResults) {
 super.onRequestPermissionsResult(requestCode, permissions, grantResults);
 if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
 initAccessToken();
 } else {
 Toast.makeText(getApplicationContext(), "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
 }
 }

 @Override
 protected void onDestroy() {
 super.onDestroy();
 // 释放内存资源
 OCR.getInstance().release();
 }

 private void initAccessToken() {
 OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
 @Override
 public void onResult(AccessToken accessToken) {
 String token = accessToken.getAccessToken();
 hasGotToken = true;
 }

 @Override
 public void onError(OCRError error) {
 error.printStackTrace();
 alertText("licence方式获取token失败", error.getMessage());
 }
 }, getApplicationContext());
 }

 private void initAccessTokenWithAkSk() {
 OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
 @Override
 public void onResult(AccessToken result) {
 String token = result.getAccessToken();
 hasGotToken = true;
 }

 @Override
 public void onError(OCRError error) {
 error.printStackTrace();
 alertText("AK，SK方式获取token失败", error.getMessage());
 }
 }, getApplicationContext(), "TwLkLRGA3bNhIBdL3EQ7hwxR", "lbUWFmDSOFALsz0CiBpedYIuqPLsbBaM");
 }
 private void alertText(final String title, final String message) {
 this.runOnUiThread(new Runnable() {
 @Override
 public void run() {
 alertDialog.setTitle(title)
 .setMessage(message)
 .setPositiveButton("确定", null)
 .show();
 }
 });
 }*/

package king.yunlesao.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.TextView;

import king.yunlesao.R;
import king.yunlesao.utils.SettingManager;
import king.yunlesao.utils.Tools;
import king.yunlesao.word.HistoryManager;

/**
 * Created by King on 2018/3/26.
 * 欢迎页面，启动后将跳转到啊主页
 */

public class FlashActivity extends BasedActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_flash);
        ((TextView)findViewById(R.id.flash_bottom_info)).setText(Tools.getAppInfo(this));
        setToolBarNoTitle(null);
        SettingManager.init(this.getApplicationContext());
        HistoryManager.checkToReset(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(FlashActivity.this,MainActivity.class));
                finish();
            }
        },2000);
    }
}


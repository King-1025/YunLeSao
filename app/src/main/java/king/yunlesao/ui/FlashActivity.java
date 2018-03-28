package king.yunlesao.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

/**
 * Created by King on 2018/3/26.
 * 欢迎页面，启动后将跳转到啊主页
 */

public class FlashActivity extends BasedActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(FlashActivity.this,MainActivity.class));
                finish();
            }
        },1000);
    }
}


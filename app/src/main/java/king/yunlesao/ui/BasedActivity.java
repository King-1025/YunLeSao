package king.yunlesao.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import king.yunlesao.R;

/**
 * Created by King on 2018/1/3.
 */

public class BasedActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    protected void setToolBarNoTitle(Toolbar toolbar){
        //启用工具栏
        setSupportActionBar(toolbar);
        //标题为空
        getSupportActionBar().setTitle(null);
    }
    protected void replaceFragment(int layout_id,Fragment fragment ){
        FragmentManager fm=getFragmentManager();
        BasedFragment.replaceFragment(fm,layout_id,fragment);
    }

    public static final int TEST_AUTO=0x1;
    public static final int TEST_ADVANCE=0x2;
    public interface Test{
        void change(int flag);
    }
}

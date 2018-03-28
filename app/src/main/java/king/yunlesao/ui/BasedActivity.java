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
import android.util.Log;
import android.view.MenuItem;

import king.yunlesao.R;

/**
 * Created by King on 2018/1/3.
 * BasedActivity作为其他Activity的一个父类
 */

public class BasedActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private final static String TAG="BasedActivity";
    protected FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager=getFragmentManager();
        Log.i(TAG,"onCreate() is OK!");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(TAG,"onNavigationItemSelected() is OK!");
        return false;
    }

    //设置没有标题的工具栏
    protected void setToolBarNoTitle(Toolbar toolbar){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        Log.i(TAG,"setToolBarTitle() is OK!");
    }

   protected BasedFragment show(BasedFragment currentFragment,BasedFragment targetFragment,int resid){
        return BasedFragment.show(fragmentManager,currentFragment,targetFragment,resid);
   }

}
